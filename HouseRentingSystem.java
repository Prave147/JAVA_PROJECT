import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// ---------- MODEL ----------
class House {
    String name, location;
    double price;
    House(String n, String l, double p) { name = n; location = l; price = p; }

    @Override
    public String toString() {
        return String.format("%-25s | %-25s | ₹%.2f", name, location, price);
    }
}

// ---------- MAIN ----------
public class HouseRentingSystem {
    public static void main(String[] args) {
        new HomePage();
    }
}

// ---------- BASE FRAME ----------
abstract class BaseFrame extends Frame {

    protected final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    protected final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    protected final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    protected final Font HEADING_FONT_LARGE = new Font("Arial", Font.BOLD, 28);
    protected final Font HEADING_FONT_MEDIUM = new Font("Arial", Font.BOLD, 22);
    protected final Font HEADING_FONT_SMALL = new Font("Arial", Font.BOLD, 20);

    protected final Color PRIMARY_BLUE = new Color(52, 152, 219);
    protected final Color DARK_BLUE = new Color(41, 128, 185);
    protected final Color PRIMARY_GREEN = new Color(46, 204, 113);
    protected final Color DARK_GREEN = new Color(39, 174, 96);
    protected final Color LIGHT_GRAY_BG = new Color(236, 240, 241);
    protected final Color TEXT_COLOR_LIGHT = Color.WHITE;
    protected final Color TEXT_COLOR_DARK = Color.BLACK;

    BaseFrame(String title) {
        super(title);
        setSize(700, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }

    protected void styleButton(Button b) {
        b.setBackground(LIGHT_GRAY_BG);
        b.setForeground(TEXT_COLOR_DARK);
        b.setFont(BUTTON_FONT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    protected void showMsg(String title, String msg) {
        Dialog d = new Dialog(this, title, true);
        d.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        d.add(new Label(msg, Label.CENTER));
        Button ok = new Button("OK");
        ok.addActionListener(ae -> d.dispose());
        styleButton(ok);
        d.add(ok);
        d.setSize(300, 150);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}

// ---------- HOME PAGE ----------
class HomePage extends BaseFrame implements ActionListener {
    Button loginBtn = new Button("Login");
    Button signupBtn = new Button("Sign Up");
    Label title = new Label("🏠 House Renting System", Label.CENTER);

    HomePage() {
        super("Home Page");
        setBackground(PRIMARY_BLUE);

        title.setBounds(100, 80, 500, 50);
        title.setFont(HEADING_FONT_LARGE);
        title.setForeground(TEXT_COLOR_LIGHT);
        add(title);

        loginBtn.setBounds(220, 200, 120, 40);
        signupBtn.setBounds(380, 200, 120, 40);
        styleButton(loginBtn);
        styleButton(signupBtn);
        add(loginBtn);
        add(signupBtn);

        loginBtn.addActionListener(this);
        signupBtn.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        dispose();
        if (e.getSource() == loginBtn)
            new LoginPage();
        else
            new SignupPage();
    }
}

// ---------- LOGIN PAGE ----------
class LoginPage extends BaseFrame implements ActionListener {
    Choice roleChoice = new Choice();
    TextField userField = new TextField();
    TextField passField = new TextField();
    Button loginBtn = new Button("Login");
    Button backBtn = new Button("← Back");

    LoginPage() {
        super("Login");
        setBackground(DARK_BLUE);

        Label heading = new Label("Login to Continue", Label.CENTER);
        heading.setBounds(200, 60, 300, 40);
        heading.setFont(HEADING_FONT_MEDIUM);
        heading.setForeground(TEXT_COLOR_LIGHT);
        add(heading);

        Label roleL = new Label("Role:"), userL = new Label("Username:"), passL = new Label("Password:");
        roleL.setBounds(200, 130, 100, 25);
        userL.setBounds(200, 170, 100, 25);
        passL.setBounds(200, 210, 100, 25);

        roleL.setForeground(TEXT_COLOR_LIGHT);
        userL.setForeground(TEXT_COLOR_LIGHT);
        passL.setForeground(TEXT_COLOR_LIGHT);

        add(roleL); add(userL); add(passL);

        roleChoice.add("Admin");
        roleChoice.add("User");
        roleChoice.setBounds(320, 130, 160, 25);
        userField.setBounds(320, 170, 160, 25);
        passField.setBounds(320, 210, 160, 25);
        passField.setEchoChar('*');
        add(roleChoice); add(userField); add(passField);

        loginBtn.setBounds(280, 260, 140, 35);
        backBtn.setBounds(30, 330, 80, 30);
        styleButton(loginBtn); styleButton(backBtn);
        add(loginBtn); add(backBtn);

        loginBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backBtn) {
            dispose();
            new HomePage();
        } else {
            String u = userField.getText();
            String p = passField.getText();
            String r = roleChoice.getSelectedItem();

            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM users WHERE username=? AND password=? AND role=?");
                ps.setString(1, u);
                ps.setString(2, p);
                ps.setString(3, r);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    dispose();
                    if (r.equals("Admin")) new AdminPage();
                    else new UserPage();
                } else {
                    showMsg("Login Failed", "❌ Invalid credentials!");
                }
            } catch (Exception ex) {
                showMsg("Error", ex.getMessage());
            }
        }
    }
}

// ---------- SIGNUP PAGE ----------
class SignupPage extends BaseFrame implements ActionListener {
    Choice roleChoice = new Choice();
    TextField userField = new TextField();
    TextField passField = new TextField();
    Button signupBtn = new Button("Sign Up");
    Button backBtn = new Button("← Back");

    SignupPage() {
        super("Sign Up");
        setBackground(DARK_GREEN);

        Label heading = new Label("Create a New Account", Label.CENTER);
        heading.setBounds(200, 60, 300, 40);
        heading.setFont(HEADING_FONT_MEDIUM);
        heading.setForeground(TEXT_COLOR_LIGHT);
        add(heading);

        Label roleL = new Label("Role:"), userL = new Label("Username:"), passL = new Label("Password:");
        roleL.setBounds(200, 130, 100, 25);
        userL.setBounds(200, 170, 100, 25);
        passL.setBounds(200, 210, 100, 25);

        roleL.setForeground(TEXT_COLOR_LIGHT);
        userL.setForeground(TEXT_COLOR_LIGHT);
        passL.setForeground(TEXT_COLOR_LIGHT);

        add(roleL); add(userL); add(passL);

        roleChoice.add("Admin");
        roleChoice.add("User");
        roleChoice.setBounds(320, 130, 160, 25);
        userField.setBounds(320, 170, 160, 25);
        passField.setBounds(320, 210, 160, 25);
        passField.setEchoChar('*');
        add(roleChoice); add(userField); add(passField);

        signupBtn.setBounds(280, 260, 140, 35);
        backBtn.setBounds(30, 330, 80, 30);
        styleButton(signupBtn); styleButton(backBtn);
        add(signupBtn); add(backBtn);

        signupBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backBtn) {
            dispose();
            new HomePage();
        } else {
            String u = userField.getText();
            String p = passField.getText();
            String r = roleChoice.getSelectedItem();

            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement check = con.prepareStatement("SELECT * FROM users WHERE username=?");
                check.setString(1, u);
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    showMsg("Signup Failed", "Username already exists!");
                    return;
                }

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
                ps.setString(1, u);
                ps.setString(2, p);
                ps.setString(3, r);
                ps.executeUpdate();

                showMsg("Success", "✅ Account created successfully!");
                dispose();
                new LoginPage();
            } catch (Exception ex) {
                showMsg("Error", ex.getMessage());
            }
        }
    }
}

// ---------- ADMIN PAGE ----------
class AdminPage extends BaseFrame implements ActionListener {
    TextField name = new TextField(), loc = new TextField(), price = new TextField();
    List houseList = new List();
    Button addBtn = new Button("Add House"), backBtn = new Button("Logout");

    AdminPage() {
        super("Admin Panel");
        setSize(750, 500);
        setBackground(DARK_BLUE);

        Label heading = new Label("Admin - Manage Houses", Label.CENTER);
        heading.setBounds(200, 40, 350, 40);
        heading.setFont(HEADING_FONT_SMALL);
        heading.setForeground(TEXT_COLOR_LIGHT);
        add(heading);

        Label nL = new Label("House Name:"), lL = new Label("Location:"), pL = new Label("Price (₹):");
        nL.setBounds(170, 120, 100, 25);
        lL.setBounds(170, 160, 100, 25);
        pL.setBounds(170, 200, 100, 25);
        nL.setForeground(TEXT_COLOR_LIGHT); lL.setForeground(TEXT_COLOR_LIGHT); pL.setForeground(TEXT_COLOR_LIGHT);
        add(nL); add(lL); add(pL);

        name.setBounds(280, 120, 200, 25);
        loc.setBounds(280, 160, 200, 25);
        price.setBounds(280, 200, 200, 25);
        add(name); add(loc); add(price);

        addBtn.setBounds(300, 240, 120, 35);
        backBtn.setBounds(30, 420, 80, 30);
        styleButton(addBtn); styleButton(backBtn);
        add(addBtn); add(backBtn);

        houseList.setBounds(150, 290, 450, 150);
        houseList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(houseList);

        updateHouseList();
        addBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setVisible(true);
    }

    private void updateHouseList() {
        houseList.removeAll();
        houseList.add(String.format("%-25s | %-25s | %s", "House Name", "Location", "Price"));
        houseList.add("------------------------------------------------------------------");
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM houses")) {

            while (rs.next()) {
                houseList.add(String.format("%-25s | %-25s | ₹%.2f",
                        rs.getString("name"), rs.getString("location"), rs.getDouble("price")));
            }
        } catch (Exception e) {
            showMsg("Error", e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            try (Connection con = DBConnection.getConnection()) {
                String n = name.getText().trim();
                String l = loc.getText().trim();
                double p = Double.parseDouble(price.getText());

                PreparedStatement ps = con.prepareStatement("INSERT INTO houses (name, location, price) VALUES (?, ?, ?)");
                ps.setString(1, n);
                ps.setString(2, l);
                ps.setDouble(3, p);
                ps.executeUpdate();
                showMsg("Success", "✅ House added successfully!");
                updateHouseList();
                name.setText(""); loc.setText(""); price.setText("");
            } catch (NumberFormatException ex) {
                showMsg("Input Error", "⚠ Invalid price!");
            } catch (Exception ex) {
                showMsg("Error", ex.getMessage());
            }
        } else if (e.getSource() == backBtn) {
            dispose();
            new LoginPage();
        }
    }
}

// ---------- USER PAGE ----------
class UserPage extends BaseFrame implements ActionListener {
    List houseList = new List();
    Button refreshBtn = new Button("Refresh"), bookBtn = new Button("Book Selected"), backBtn = new Button("Logout");

    UserPage() {
        super("User Panel");
        setSize(750, 500);
        setBackground(PRIMARY_GREEN);

        Label heading = new Label("User - View & Book Houses", Label.CENTER);
        heading.setBounds(200, 40, 350, 40);
        heading.setFont(HEADING_FONT_SMALL);
        heading.setForeground(TEXT_COLOR_LIGHT);
        add(heading);

        houseList.setBounds(150, 120, 450, 200);
        houseList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(houseList);

        refreshBtn.setBounds(200, 340, 120, 35);
        bookBtn.setBounds(380, 340, 120, 35);
        backBtn.setBounds(30, 420, 80, 30);
        styleButton(refreshBtn); styleButton(bookBtn); styleButton(backBtn);
        add(refreshBtn); add(bookBtn); add(backBtn);

        refreshBtn.addActionListener(this);
        bookBtn.addActionListener(this);
        backBtn.addActionListener(this);

        updateHouseList();
        setVisible(true);
    }

    private void updateHouseList() {
        houseList.removeAll();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM houses")) {

            houseList.add(String.format("%-25s | %-25s | %s", "House Name", "Location", "Price"));
            houseList.add("------------------------------------------------------------------");
            while (rs.next()) {
                houseList.add(String.format("%-25s | %-25s | ₹%.2f",
                        rs.getString("name"), rs.getString("location"), rs.getDouble("price")));
            }
        } catch (Exception e) {
            showMsg("Error", e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshBtn) {
            updateHouseList();
        } else if (e.getSource() == bookBtn) {
            int index = houseList.getSelectedIndex();
            if (index > 1) {
                String selected = houseList.getItem(index);
                String name = selected.split("\\|")[0].trim();
                try (Connection con = DBConnection.getConnection()) {
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM houses WHERE name=?");
                    ps.setString(1, name);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        House h = new House(rs.getString("name"), rs.getString("location"), rs.getDouble("price"));
                        dispose();
                        new BookingPage(h);
                    }
                } catch (Exception ex) {
                    showMsg("Error", ex.getMessage());
                }
            } else showMsg("Error", "Please select a house to book!");
        } else if (e.getSource() == backBtn) {
            dispose();
            new LoginPage();
        }
    }
}

// ---------- BOOKING PAGE ----------
class BookingPage extends BaseFrame implements ActionListener {
    House house;
    Button confirmBtn = new Button("Confirm Booking"), backBtn = new Button("Back");

    BookingPage(House h) {
        super("Booking");
        this.house = h;
        setBackground(LIGHT_GRAY_BG);

        Label heading = new Label("Confirm Your Booking", Label.CENTER);
        heading.setBounds(180, 60, 350, 40);
        heading.setFont(HEADING_FONT_SMALL);
        add(heading);

        Label details = new Label(
            "House: " + h.name + " | Location: " + h.location + " | Price: ₹" + h.price, Label.CENTER);
        details.setBounds(100, 150, 500, 30);
        add(details);

        confirmBtn.setBounds(240, 230, 140, 40);
        backBtn.setBounds(400, 230, 140, 40);
        styleButton(confirmBtn); styleButton(backBtn);
        add(confirmBtn); add(backBtn);

        confirmBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmBtn) {
            showMsg("Booking Confirmed", "✅ You have booked " + house.name + " successfully!");
            dispose();
            new UserPage();
        } else {
            dispose();
            new UserPage();
        }
    }
}
