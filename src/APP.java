import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class APP {
    static final String  DB_URL = "jdbc:mysql://localhost:3306/store2025";
    static final String USER = "root";
    static final String PASS = "Masterchief177#";
    public static void main(String[] args) throws Exception {

        // Conectarnos a la base de datos
        try {
            Connection conn= DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("CONEXIÓN EXISTOSA");
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM customer"; 
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String nombre = rs.getString("name");
                String ciudad = rs.getString("city");
                String telefono = rs.getString("phone");

                System.out.println("ID: " + id + " | Nombre: " + nombre + " | Ciudad: " + ciudad + " | Tel: " + telefono);
            }
            
            rs.close();
            stmt.close();
            conn.close();



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}