/**
 * OperacionesCRUD.java
 * Clase para operaciones CRUD sobre la tabla Productos
 */

import java.sql.*;
import java.util.Scanner;

public class OperacionesCRUD {
    
    /**
     * CREATE - Crear un nuevo producto
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void crearProducto(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║          CREATE - CREAR NUEVO PRODUCTO              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nProductoID (ej: PR9): ");
        String prodID = scanner.nextLine();
        
        System.out.print("MarcaID (M1-M8): ");
        String marcaID = scanner.nextLine();
        
        System.out.print("Nombre Producto: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();
        
        System.out.print("Precio Base: ");
        String precio = scanner.nextLine();
        
        String sql = "INSERT INTO Productos VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, prodID);
            pstmt.setString(2, marcaID);
            pstmt.setString(3, nombre);
            pstmt.setString(4, descripcion);
            pstmt.setDouble(5, Double.parseDouble(precio));
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Producto creado exitosamente.");
                System.out.println("  ProductoID: " + prodID);
                System.out.println("  Nombre: " + nombre);
                System.out.println("  Precio: $" + precio);
            }
            
        } catch (SQLException e) {
            System.err.println(" Error: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("   El ProductoID ya existe.");
            } else if (e.getMessage().contains("foreign key")) {
                System.err.println("   La MarcaID no existe en la tabla Marcas.");
            }
        } catch (NumberFormatException e) {
            System.err.println(" Error: El precio debe ser un número válido.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * READ - Leer productos
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void leerProductos(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║            READ - LEER PRODUCTOS                     ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n  1. Ver todos los productos");
        System.out.println("  2. Buscar producto por ID");
        System.out.print("\n▶ Seleccione opción: ");
        
        int opcion = -1;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(" Opción inválida.");
            return;
        }
        
        if (opcion == 1) {
            String sql = "SELECT p.ProductoID, p.NombreProducto, " +
                        "COALESCE(m.NombreMarca, 'Sin Marca') AS NombreMarca, " +
                        "LEFT(p.Descripcion, 40) AS Descripcion, " +
                        "p.PrecioBase " +
                        "FROM Productos p " +
                        "LEFT JOIN Marcas m ON p.MarcaID = m.MarcaID " +
                        "ORDER BY p.ProductoID";
            
            Utilidades.ejecutarConsulta(sql, "LISTA DE TODOS LOS PRODUCTOS");
            
        } else if (opcion == 2) {
            System.out.print("\nIngrese ProductoID: ");
            String prodID = scanner.nextLine();
            
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                String sql = "SELECT p.*, m.NombreMarca " +
                            "FROM Productos p " +
                            "LEFT JOIN Marcas m ON p.MarcaID = m.MarcaID " +
                            "WHERE p.ProductoID = ?";
                
                conn = DatabaseConnection.getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, prodID);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    System.out.println("\n╔════════════════════════════════════════════════════════╗");
                    System.out.println("║           DETALLE DEL PRODUCTO                       ║");
                    System.out.println("╚════════════════════════════════════════════════════════╝");
                    System.out.println("\nProductoID:   " + rs.getString("ProductoID"));
                    System.out.println("Marca:        " + rs.getString("NombreMarca"));
                    System.out.println("Nombre:       " + rs.getString("NombreProducto"));
                    System.out.println("Descripción:  " + rs.getString("Descripcion"));
                    System.out.println("Precio Base:  $" + rs.getDouble("PrecioBase"));
                } else {
                    System.out.println("\n Producto no encontrado.");
                }
                
            } catch (SQLException e) {
                System.err.println(" Error: " + e.getMessage());
            } finally {
                DatabaseConnection.closeResources(conn, pstmt, rs);
            }
        } else {
            System.out.println(" Opción inválida.");
        }
    }
    
    /**
     * UPDATE - Actualizar un producto
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void actualizarProducto(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║         UPDATE - ACTUALIZAR PRODUCTO                 ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nProductoID a actualizar: ");
        String prodID = scanner.nextLine();
        
        System.out.print("Nuevo Precio Base: ");
        String precio = scanner.nextLine();
        
        System.out.print("Nueva Descripción (Enter para mantener la actual): ");
        String descripcion = scanner.nextLine();
        
        String sql;
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            if (descripcion.trim().isEmpty()) {
                // Solo actualizar precio
                sql = "UPDATE Productos SET PrecioBase = ? WHERE ProductoID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, Double.parseDouble(precio));
                pstmt.setString(2, prodID);
            } else {
                // Actualizar precio y descripción
                sql = "UPDATE Productos SET PrecioBase = ?, Descripcion = ? WHERE ProductoID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, Double.parseDouble(precio));
                pstmt.setString(2, descripcion);
                pstmt.setString(3, prodID);
            }
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Producto actualizado exitosamente.");
                System.out.println("  ProductoID: " + prodID);
                System.out.println("  Nuevo Precio: $" + precio);
                if (!descripcion.trim().isEmpty()) {
                    System.out.println("  Nueva Descripción: " + descripcion);
                }
            } else {
                System.out.println(" Producto no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println(" Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println(" Error: El precio debe ser un número válido.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * DELETE - Eliminar un producto
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void eliminarProducto(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║          DELETE - ELIMINAR PRODUCTO                  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nProductoID a eliminar: ");
        String prodID = scanner.nextLine();
        
        // Mostrar el producto antes de eliminarlo
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Primero verificar si existe
            String sqlSelect = "SELECT NombreProducto, PrecioBase FROM Productos WHERE ProductoID = ?";
            pstmt = conn.prepareStatement(sqlSelect);
            pstmt.setString(1, prodID);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String nombre = rs.getString("NombreProducto");
                double precio = rs.getDouble("PrecioBase");
                
                System.out.println("\nProducto a eliminar:");
                System.out.println("  ID: " + prodID);
                System.out.println("  Nombre: " + nombre);
                System.out.println("  Precio: $" + precio);
                
                System.out.print("\n¿Está seguro de eliminar este producto? (S/N): ");
                String confirmacion = scanner.nextLine();
                
                if (confirmacion.equalsIgnoreCase("S")) {
                    // Cerrar recursos anteriores
                    rs.close();
                    pstmt.close();
                    
                    // Proceder a eliminar
                    String sqlDelete = "DELETE FROM Productos WHERE ProductoID = ?";
                    pstmt = conn.prepareStatement(sqlDelete);
                    pstmt.setString(1, prodID);
                    
                    int rows = pstmt.executeUpdate();
                    
                    if (rows > 0) {
                        System.out.println("\n✓ Producto eliminado exitosamente.");
                    }
                } else {
                    System.out.println("\nOperación cancelada.");
                }
            } else {
                System.out.println("\n Producto no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println(" Error: " + e.getMessage());
            if (e.getMessage().contains("foreign key")) {
                System.err.println("   El producto no puede eliminarse porque está en pedidos o recetas.");
                System.err.println("   Primero elimine las referencias en esas tablas.");
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
    }
    
    /**
     * Eliminar registros de otras tablas
     * @param scanner Scanner para leer entrada del usuario
     */
    public static void eliminarOtrosRegistros(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║         ELIMINAR OTROS REGISTROS                     ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n  1. Eliminar Cliente");
        System.out.println("  2. Eliminar Ingrediente");
        System.out.println("  3. Eliminar Marca");
        System.out.println("  0. Volver");
        System.out.print("\n Seleccione tabla: ");
        
        int opcion = -1;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(" Opción inválida.");
            return;
        }
        
        String sql = "";
        String id = "";
        String tabla = "";
        String campo = "";
        
        switch (opcion) {
            case 1:
                System.out.print("\nIngrese ClienteID a eliminar: ");
                id = scanner.nextLine();
                sql = "DELETE FROM Cliente WHERE ClienteID = ?";
                tabla = "Cliente";
                campo = "ClienteID";
                break;
            case 2:
                System.out.print("\nIngrese IngredienteID a eliminar: ");
                id = scanner.nextLine();
                sql = "DELETE FROM Ingredientes WHERE IngredienteID = ?";
                tabla = "Ingrediente";
                campo = "IngredienteID";
                break;
            case 3:
                System.out.print("\nIngrese MarcaID a eliminar: ");
                id = scanner.nextLine();
                sql = "DELETE FROM Marcas WHERE MarcaID = ?";
                tabla = "Marca";
                campo = "MarcaID";
                break;
            case 0:
                return;
            default:
                System.out.println(" Opción inválida.");
                return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ " + tabla + " eliminado exitosamente.");
                System.out.println("  " + campo + ": " + id);
            } else {
                System.out.println( tabla + " no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println(" Error: " + e.getMessage());
            System.err.println("Posible causa: El registro tiene datos relacionados en otras tablas.");
            System.err.println("Debe eliminar primero las referencias antes de eliminar este registro.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
}