/**
 * DatabaseConnection.java
 * Clase para gestionar la conexión a la base de datos MySQL
 */

import java.sql.*;

public class DatabaseConnection {
    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/ProyectoFinal";
    private static final String USER = "root";
    private static final String PASSWORD = "tu_password"; // CAMBIAR AQUÍ
    
    /**
     * Obtiene una conexión a la base de datos
     * @return Objeto Connection
     * @throws SQLException si hay error en la conexión
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado. " +
                    "Asegúrate de tener mysql-connector-java en el classpath", e);
        }
    }
    
    /**
     * Cierra los recursos de base de datos de forma segura
     * @param conn Conexión a cerrar
     * @param stmt Statement a cerrar
     * @param rs ResultSet a cerrar
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si la conexión está funcionando
     * @return true si la conexión es exitosa
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            System.out.println("✓ Conexión exitosa a la base de datos");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, null, null);
        }
    }
}