/**
 * Utilidades.java
 * Clase con métodos auxiliares para el sistema
 */

import java.sql.*;

public class Utilidades {
    
    /**
     * Ejecuta una consulta SQL y muestra los resultados formateados
     * @param sql Consulta SQL a ejecutar
     * @param titulo Título para mostrar en la salida
     */
    public static void ejecutarConsulta(String sql, String titulo) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║  " + titulo);
            System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝\n");
            
            imprimirResultSet(rs);
            
        } catch (SQLException e) {
            System.err.println(" Error al ejecutar consulta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Imprime un ResultSet en formato de tabla
     * @param rs ResultSet a imprimir
     * @throws SQLException si hay error al leer el ResultSet
     */
    public static void imprimirResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // Imprimir encabezados
        for (int i = 1; i <= columnCount; i++) {
            System.out.printf("%-25s", metaData.getColumnName(i));
        }
        System.out.println("\n" + "─".repeat(columnCount * 25));
        
        // Imprimir datos
        int count = 0;
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);
                if (value != null && value.length() > 24) {
                    value = value.substring(0, 21) + "...";
                }
                System.out.printf("%-25s", value != null ? value : "NULL");
            }
            System.out.println();
            count++;
        }
        
        if (count == 0) {
            System.out.println("\n(Sin resultados)");
        } else {
            System.out.println("\n✓ Total de registros: " + count);
        }
    }
    
    /**
     * Pausa la ejecución hasta que el usuario presione Enter
     */
    public static void pausar(java.util.Scanner scanner) {
        System.out.print("\n💡 Presione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Limpia la pantalla (simulado con líneas en blanco)
     */
    public static void limpiarPantalla() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    /**
     * Muestra un separador visual
     */
    public static void mostrarSeparador() {
        System.out.println("\n" + "═".repeat(80) + "\n");
    }
    
    /**
     * Muestra mensaje de bienvenida
     */
    public static void mostrarBienvenida() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                                ║");
        System.out.println("║               SISTEMA DE GESTIÓN - PROYECTO FINAL                              ║");
        System.out.println("║                    Base de Datos con JDBC                                      ║");
        System.out.println("║                                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        
        // Probar conexión
        System.out.println("\nProbando conexión a la base de datos...");
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Sistema listo para usar.\n");
        } else {
            System.out.println(" No se pudo conectar a la base de datos.");
            System.out.println("  Verifique las credenciales en DatabaseConnection.java\n");
        }
    }
    
    /**
     * Valida si un string es un número entero
     * @param str String a validar
     * @return true si es un número entero válido
     */
    public static boolean esNumeroEntero(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida si un string es un número decimal
     * @param str String a validar
     * @return true si es un número decimal válido
     */
    public static boolean esNumeroDecimal(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Muestra estadísticas rápidas del sistema
     */
    public static void mostrarEstadisticas() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║          ESTADÍSTICAS DEL SISTEMA                    ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        String[] consultas = {
            "SELECT 'Clientes' AS Tabla, COUNT(*) AS Total FROM Cliente",
            "SELECT 'Empleados', COUNT(*) FROM Empleados",
            "SELECT 'Productos', COUNT(*) FROM Productos",
            "SELECT 'Pedidos', COUNT(*) FROM Pedidos",
            "SELECT 'Marcas', COUNT(*) FROM Marcas",
            "SELECT 'Proveedores', COUNT(*) FROM Proveedores"
        };
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            for (String sql : consultas) {
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    System.out.printf("  %-15s: %5d registros\n", 
                                    rs.getString(1), rs.getInt(2));
                }
                rs.close();
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener estadísticas: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
}