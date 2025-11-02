import java.sql.*;

public class DBConnection {
    private static Connection con = null;

    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://localhost:3306/houserenting?allowPublicKeyRetrieval=true&useSSL=false";
                String user = "root";
                String password = "pulsar"; // change this
                con = DriverManager.getConnection(url, user, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
