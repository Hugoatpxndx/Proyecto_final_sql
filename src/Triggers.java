/**
 * Triggers.java
 * Clase para probar los triggers (Puntos 5 y 6)
 */

import java.sql.*;
import java.util.Scanner;

public class Triggers {
    
    /**
     * PUNTO 5: Inserta un ingrediente para activar el trigger de inventario
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void insertarIngrediente(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  INSERTAR INGREDIENTE - Activa Trigger (Punto 5)    ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nIngredienteID (ej: I13): ");
        String ingID = scanner.nextLine();
        
        System.out.print("ProveedorID (ej: PRO1): ");
        String provID = scanner.nextLine();
        
        System.out.print("Nombre Ingrediente: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Unidad Medida (kg/lt/pza): ");
        String unidad = scanner.nextLine();
        
        System.out.print("Stock Actual: ");
        String stock = scanner.nextLine();
        
        System.out.print("Costo Unitario: ");
        String costo = scanner.nextLine();
        
        String sql = "INSERT INTO Ingredientes VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ingID);
            pstmt.setString(2, provID);
            pstmt.setString(3, nombre);
            pstmt.setString(4, unidad);
            pstmt.setDouble(5, Double.parseDouble(stock));
            pstmt.setDouble(6, Double.parseDouble(costo));
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Ingrediente insertado exitosamente.");
                System.out.println("✓ Trigger 'trg_RegistrarInventario_Insert' activado.");
                System.out.println("✓ Se registró en la tabla LogInventario.");
                System.out.println("\n Use la opción 'Ver Log de Inventario' para verificar.");
            }
            
        } catch (SQLException e) {
            System.err.println(" Error: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("   El ingrediente ya existe (Trigger de validación funcionando).");
            }
        } catch (NumberFormatException e) {
            System.err.println(" Error: Stock o Costo deben ser números válidos.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * PUNTO 5: Actualiza stock de ingrediente para activar trigger
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void actualizarStockIngrediente(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  ACTUALIZAR STOCK - Activa Trigger (Punto 5)        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nIngredienteID a actualizar: ");
        String ingID = scanner.nextLine();
        
        System.out.print("Nuevo stock (ingrese un número bajo como 8 para probar alerta): ");
        String stock = scanner.nextLine();
        
        String sql = "UPDATE Ingredientes SET StockActual = ? WHERE IngredienteID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, Double.parseDouble(stock));
            pstmt.setString(2, ingID);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Stock actualizado exitosamente.");
                System.out.println("✓ Trigger 'trg_RegistrarInventario_Update' activado.");
                
                double stockNum = Double.parseDouble(stock);
                if (stockNum < 10) {
                    System.out.println(" ALERTA: Stock bajo detectado por el trigger!");
                    System.out.println("✓ Se registró alerta en LogInventario.");
                }
                
                System.out.println("\n Use la opción 'Ver Log de Inventario' para verificar.");
            } else {
                System.out.println(" Ingrediente no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println(" Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println(" Error: El stock debe ser un número válido.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * PUNTO 6: Inserta un pedido online para activar trigger de seguimiento
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void insertarPedidoOnline(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  INSERTAR PEDIDO ONLINE - Activa Trigger (Punto 6)  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nPedidoID (ej: P21): ");
        String pedidoID = scanner.nextLine();
        
        System.out.print("ClienteID (ej: C1): ");
        String clienteID = scanner.nextLine();
        
        System.out.print("EmpleadoID (ej: E1): ");
        String empleadoID = scanner.nextLine();
        
        System.out.println("\nSeleccione Plataforma:");
        System.out.println("  1. Rappi");
        System.out.println("  2. Uber Eats");
        System.out.println("  3. Didi Food");
        System.out.print("Opción: ");
        
        int plat = -1;
        try {
            plat = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(" Opción inválida.");
            return;
        }
        
        String plataforma;
        switch (plat) {
            case 1: plataforma = "Rappi"; break;
            case 2: plataforma = "Uber Eats"; break;
            case 3: plataforma = "Didi Food"; break;
            default:
                System.out.println(" Opción inválida.");
                return;
        }
        
        System.out.print("\nTotal Pedido: ");
        String total = scanner.nextLine();
        
        String sql = "INSERT INTO Pedidos VALUES (?, ?, ?, CURDATE(), 'Pendiente', ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pedidoID);
            pstmt.setString(2, clienteID);
            pstmt.setString(3, empleadoID);
            pstmt.setString(4, plataforma);
            pstmt.setInt(5, Integer.parseInt(total));
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Pedido insertado exitosamente.");
                System.out.println("✓ Trigger 'trg_SeguimientoClientesOnline' activado.");
                System.out.println("✓ Se creó registro de seguimiento automáticamente.");
                System.out.println("✓ Plataforma: " + plataforma);
                System.out.println("\n Use la opción 'Ver Seguimiento Online' para verificar.");
            }
            
        } catch (SQLException e) {
            System.err.println(" Error: " + e.getMessage());
            if (e.getMessage().contains("foreign key")) {
                System.err.println("   Verifique que el ClienteID y EmpleadoID existan.");
            }
        } catch (NumberFormatException e) {
            System.err.println(" Error: El total debe ser un número válido.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Muestra el log de inventario (auditoría del trigger)
     */
    public static void verLogInventario() {
        String sql = "SELECT LogID, IngredienteID, Accion, StockAnterior, StockNuevo, " +
                    "Usuario, DATE_FORMAT(FechaHora, '%Y-%m-%d %H:%i:%s') AS FechaHora, " +
                    "LEFT(Mensaje, 50) AS Mensaje " +
                    "FROM LogInventario " +
                    "ORDER BY FechaHora DESC " +
                    "LIMIT 15";
        
        Utilidades.ejecutarConsulta(sql, "LOG DE INVENTARIO (últimos 15 registros)");
    }
    
    /**
     * Muestra el seguimiento de clientes online
     */
    public static void verSeguimientoClientesOnline() {
        String sql = "SELECT SeguimientoID, ClienteID, NombreCliente, PedidoID, " +
                    "PlataformaOrigen, TotalPedido, EstadoSeguimiento, " +
                    "DATE_FORMAT(FechaHoraCompra, '%Y-%m-%d %H:%i:%s') AS FechaHora " +
                    "FROM SeguimientoClientesOnline " +
                    "ORDER BY FechaHoraCompra DESC " +
                    "LIMIT 15";
        
        Utilidades.ejecutarConsulta(sql, "SEGUIMIENTO CLIENTES ONLINE (últimos 15 registros)");
    }
}