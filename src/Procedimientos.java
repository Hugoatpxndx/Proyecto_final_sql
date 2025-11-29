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
     * PUNTO 4: Insertar cliente con validación de email único (usando query directo)
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
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtInsert = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Validaciones previas
            if (email == null || email.trim().isEmpty()) {
                System.out.println("\n ERROR: El correo electrónico es obligatorio.");
                return;
            }
            
            if (clienteID == null || clienteID.trim().isEmpty()) {
                System.out.println("\n ERROR: El ID del cliente es obligatorio.");
                return;
            }
            
            // Verificar si el email ya existe
            String sqlCheck = "SELECT COUNT(*) FROM Cliente WHERE Email = ?";
            pstmtCheck = conn.prepareStatement(sqlCheck);
            pstmtCheck.setString(1, email);
            rs = pstmtCheck.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("\n ERROR: El correo electrónico '" + email + 
                    "' ya existe en la base de datos.");
                System.out.println("   No se pueden registrar dos clientes con el mismo correo.");
                return;
            }
            
            // Verificar si el ClienteID ya existe
            rs.close();
            pstmtCheck.close();
            sqlCheck = "SELECT COUNT(*) FROM Cliente WHERE ClienteID = ?";
            pstmtCheck = conn.prepareStatement(sqlCheck);
            pstmtCheck.setString(1, clienteID);
            rs = pstmtCheck.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("\n ERROR: El ClienteID '" + clienteID + 
                    "' ya existe en la base de datos.");
                return;
            }
            
            // Insertar el cliente
            String sqlInsert = "INSERT INTO Cliente (ClienteID, Nombre, Apellido, Email, " +
                              "Telefono, FechaRegistro, Direccion) " +
                              "VALUES (?, ?, ?, ?, ?, CURDATE(), ?)";
            
            pstmtInsert = conn.prepareStatement(sqlInsert);
            pstmtInsert.setString(1, clienteID);
            pstmtInsert.setString(2, nombre);
            pstmtInsert.setString(3, apellido);
            pstmtInsert.setString(4, email);
            pstmtInsert.setString(5, telefono);
            pstmtInsert.setString(6, direccion);
            
            int rows = pstmtInsert.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ ÉXITO: Cliente '" + nombre + " " + apellido + 
                    "' registrado exitosamente.");
                System.out.println("  ClienteID: " + clienteID);
                System.out.println("  Email: " + email);
                System.out.println("  Fecha de Registro: " + java.time.LocalDate.now());
                
                // Mostrar el cliente insertado
                System.out.println("\n--- Datos del Cliente Insertado ---");
                String sqlSelect = "SELECT * FROM Cliente WHERE ClienteID = ?";
                PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);
                pstmtSelect.setString(1, clienteID);
                ResultSet rsCliente = pstmtSelect.executeQuery();
                
                if (rsCliente.next()) {
                    System.out.println("ClienteID:      " + rsCliente.getString("ClienteID"));
                    System.out.println("Nombre:         " + rsCliente.getString("Nombre") + " " + 
                                     rsCliente.getString("Apellido"));
                    System.out.println("Email:          " + rsCliente.getString("Email"));
                    System.out.println("Teléfono:       " + rsCliente.getString("Telefono"));
                    System.out.println("Dirección:      " + rsCliente.getString("Direccion"));
                    System.out.println("Fecha Registro: " + rsCliente.getDate("FechaRegistro"));
                }
                
                rsCliente.close();
                pstmtSelect.close();
            }
            
        } catch (SQLException e) {
            System.out.println("\n ERROR al insertar cliente: " + e.getMessage());
            
            // Manejo específico de errores comunes
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.out.println("   Código de Error: 1062 - Entrada duplicada");
                System.out.println("   El email o ClienteID ya existe en el sistema.");
            } else {
                System.out.println("   Código de Error: " + e.getErrorCode());
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtCheck != null) pstmtCheck.close();
                if (pstmtInsert != null) pstmtInsert.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
}