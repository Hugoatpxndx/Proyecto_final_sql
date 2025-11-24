import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class APP {
    static final String  DB_URL = "jbc:mysql://localhost:3306/store2025";
    static final String USER = "root";
    static final String PASS = "masterchief177#";
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        // Conectarnos a la base de datos
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("CONEXIÓN EXISTOSA");
        } catch (SQLException e) {
            e.printStackTrace();
    }
}
}