/**
 * Procedimientos.java
 * Clase para ejecutar los procedimientos almacenados (Puntos 2, 3 y 4)
 */

import java.sql.*;
import java.util.Scanner;

public class Procedimientos {
    
    /**
     * PUNTO 2: Ejecuta el procedimiento sp_VentasDiarias
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void ejecutarVentasDiarias(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║      REPORTE DE VENTAS DIARIAS (Punto 2)            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nIngrese la fecha (YYYY-MM-DD, ej: 2024-11-24): ");
        String fecha = scanner.nextLine();
        
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            cstmt = conn.prepareCall("{CALL sp_VentasDiarias(?)}");
            cstmt.setString(1, fecha);
            
            boolean hasResults = cstmt.execute();
            int resultSetCount = 0;
            
            while (hasResults) {
                resultSetCount++;
                rs = cstmt.getResultSet();
                
                System.out.println("\n╔════════════════════════════════════════════════════════╗");
                System.out.println("║  Resultado " + resultSetCount);
                System.out.println("╚════════════════════════════════════════════════════════╝");
                
                Utilidades.imprimirResultSet(rs);
                
                hasResults = cstmt.getMoreResults();
            }
            
            System.out.println("\n✓ Procedimiento ejecutado exitosamente.");
            
        } catch (SQLException e) {
            System.err.println(" Error al ejecutar procedimiento: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, cstmt, rs);
        }
    }
    
    /**
     * PUNTO 3: Ejecuta el procedimiento sp_ClientesVigentesQ1
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void ejecutarClientesVigentesQ1(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   REPORTE CLIENTES VIGENTES Q1 (Punto 3)            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nIngrese el año (ej: 2024): ");
        String añoStr = scanner.nextLine();
        
        try {
            int año = Integer.parseInt(añoStr);
            
            Connection conn = null;
            CallableStatement cstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                cstmt = conn.prepareCall("{CALL sp_ClientesVigentesQ1(?)}");
                cstmt.setInt(1, año);
                
                boolean hasResults = cstmt.execute();
                int resultSetCount = 0;
                
                while (hasResults) {
                    resultSetCount++;
                    rs = cstmt.getResultSet();
                    
                    System.out.println("\n╔════════════════════════════════════════════════════════╗");
                    System.out.println("║  Resultado " + resultSetCount);
                    System.out.println("╚════════════════════════════════════════════════════════╝");
                    
                    Utilidades.imprimirResultSet(rs);
                    
                    hasResults = cstmt.getMoreResults();
                }
                
                System.out.println("\n✓ Procedimiento ejecutado exitosamente.");
                
            } catch (SQLException e) {
                System.err.println(" Error al ejecutar procedimiento: " + e.getMessage());
                e.printStackTrace();
            } finally {
                DatabaseConnection.closeResources(conn, cstmt, rs);
            }
            
        } catch (NumberFormatException e) {
            System.out.println(" Año inválido. Debe ser un número.");
        }
    }
    
     /**
     * PUNTO 4: Ejecuta el procedimiento sp_InsertarCliente con validación
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void ejecutarInsertarCliente(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   INSERTAR CLIENTE CON VALIDACIÓN (Punto 4)         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nIngrese ClienteID (ej: C9): ");
        String clienteID = scanner.nextLine();
        
        System.out.print("Ingrese Nombre: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Ingrese Apellido: ");
        String apellido = scanner.nextLine();
        
        System.out.print("Ingrese Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Ingrese Teléfono (10 dígitos): ");
        String telefono = scanner.nextLine();
        
        System.out.print("Ingrese Dirección: ");
        String direccion = scanner.nextLine();
        
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            cstmt = conn.prepareCall("{CALL sp_InsertarCliente(?, ?, ?, ?, ?, ?)}");
            cstmt.setString(1, clienteID);
            cstmt.setString(2, nombre);
            cstmt.setString(3, apellido);
            cstmt.setString(4, email);
            cstmt.setString(5, telefono);
            cstmt.setString(6, direccion);
            
            boolean hasResults = cstmt.execute();
            
            System.out.println("\n--- Resultado del Procedimiento ---");
            
            while (hasResults) {
                rs = cstmt.getResultSet();
                Utilidades.imprimirResultSet(rs);
                hasResults = cstmt.getMoreResults();
            }
            
        } catch (SQLException e) {
            System.err.println(" Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, cstmt, rs);
        }
    }
} 