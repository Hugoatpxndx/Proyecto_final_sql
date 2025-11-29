/**
 * =====================================================
 * SISTEMA DE GESTIÓN CON MENÚ INTERACTIVO - PROYECTO FINAL
 * Implementación JDBC con todas las funcionalidades requeridas
 * =====================================================
 */

import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;

/**
 * Clase de Conexión a la Base de Datos
 */
class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ProyectoFinal";
    private static final String USER = "root";
    private static final String PASSWORD = "tu_password";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado", e);
        }
    }
    
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Clase principal con menú interactivo
 */
public class SistemaGestionProyectoFinal {
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        boolean continuar = true;
        
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    menuConsultasAvanzadas();
                    break;
                case 2:
                    ejecutarProcedimientoVentasDiarias();
                    break;
                case 3:
                    ejecutarProcedimientoClientesQ1();
                    break;
                case 4:
                    ejecutarProcedimientoInsertarCliente();
                    break;
                case 5:
                    menuTriggers();
                    break;
                case 6:
                    eliminarRegistro();
                    break;
                case 7:
                    menuCRUD();
                    break;
                case 0:
                    System.out.println("\n¡Gracias por usar el sistema! Hasta pronto.");
                    continuar = false;
                    break;
                default:
                    System.out.println("\n❌ Opción inválida. Intente nuevamente.");
            }
        }
        
        scanner.close();
    }
    
    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║     SISTEMA DE GESTIÓN - PROYECTO FINAL              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n📋 MENÚ PRINCIPAL:");
        System.out.println("  1. 📊 Consultas Avanzadas SQL (Punto 1)");
        System.out.println("  2. 💰 Reporte de Ventas Diarias (Punto 2)");
        System.out.println("  3. 👥 Reporte Clientes Vigentes Q1 (Punto 3)");
        System.out.println("  4. ➕ Insertar Cliente con Validación (Punto 4)");
        System.out.println("  5. ⚡ Probar Triggers (Puntos 5 y 6)");
        System.out.println("  6. 🗑️  Eliminar Registro");
        System.out.println("  7. 🔧 Operaciones CRUD");
        System.out.println("  0. ❌ Salir");
        System.out.print("\n▶️  Seleccione una opción: ");
    }
    
    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    // ========== PUNTO 1: CONSULTAS AVANZADAS SQL ==========
    
    private static void menuConsultasAvanzadas() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║        CONSULTAS AVANZADAS SQL (Punto 1)             ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n  1. JOIN - Pedidos con clientes y empleados");
        System.out.println("  2. UNION - Lista de contactos unificada");
        System.out.println("  3. GROUP BY - Ventas totales por cliente");
        System.out.println("  4. ORDER BY - Pedidos más recientes");
        System.out.println("  5. Fechas - Análisis trimestral");
        System.out.println("  6. JOIN Múltiple - Detalle completo de pedidos");
        System.out.println("  0. Volver al menú principal");
        System.out.print("\n▶️  Seleccione consulta: ");
        
        int opcion = leerOpcion();
        
        switch (opcion) {
            case 1:
                consultaJoinPedidos();
                break;
            case 2:
                consultaUnionContactos();
                break;
            case 3:
                consultaGroupByVentas();
                break;
            case 4:
                consultaOrderByPedidos();
                break;
            case 5:
                consultaAnalisisTrimestral();
                break;
            case 6:
                consultaJoinMultiple();
                break;
            case 0:
                return;
            default:
                System.out.println("\n❌ Opción inválida.");
        }
        
        pausar();
    }
    
    private static void consultaJoinPedidos() {
        String sql = "SELECT " +
                    "    p.PedidoID, " +
                    "    p.FechaPedido, " +
                    "    CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente, " +
                    "    CONCAT(e.Nombre, ' ', e.Apellido) AS Empleado, " +
                    "    p.PlataformaOrigen, " +
                    "    p.EstadoPedido, " +
                    "    p.TotalPedido " +
                    "FROM Pedidos p " +
                    "INNER JOIN Cliente c ON p.ClienteID = c.ClienteID " +
                    "INNER JOIN Empleados e ON p.EmpleadoID = e.EmpleadoID " +
                    "ORDER BY p.FechaPedido DESC " +
                    "LIMIT 10";
        
        ejecutarConsulta(sql, "JOIN - Pedidos con información completa");
    }
    
    private static void consultaUnionContactos() {
        String sql = "SELECT 'Cliente' AS Tipo, CONCAT(Nombre, ' ', Apellido) AS Nombre, Email " +
                    "FROM Cliente " +
                    "UNION " +
                    "SELECT 'Proveedor', ContactoNombre, Email " +
                    "FROM Proveedores " +
                    "ORDER BY Tipo, Nombre";
        
        ejecutarConsulta(sql, "UNION - Lista unificada de contactos");
    }
    
    private static void consultaGroupByVentas() {
        String sql = "SELECT " +
                    "    c.ClienteID, " +
                    "    CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente, " +
                    "    COUNT(p.PedidoID) AS TotalPedidos, " +
                    "    COALESCE(SUM(p.TotalPedido), 0) AS MontoTotal, " +
                    "    COALESCE(AVG(p.TotalPedido), 0) AS PromedioCompra " +
                    "FROM Cliente c " +
                    "LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID " +
                    "GROUP BY c.ClienteID, c.Nombre, c.Apellido " +
                    "HAVING COUNT(p.PedidoID) > 0 " +
                    "ORDER BY MontoTotal DESC";
        
        ejecutarConsulta(sql, "GROUP BY - Ventas totales por cliente");
    }
    
    private static void consultaOrderByPedidos() {
        String sql = "SELECT " +
                    "    PedidoID, " +
                    "    ClienteID, " +
                    "    FechaPedido, " +
                    "    PlataformaOrigen, " +
                    "    EstadoPedido, " +
                    "    TotalPedido " +
                    "FROM Pedidos " +
                    "ORDER BY FechaPedido DESC, TotalPedido DESC " +
                    "LIMIT 10";
        
        ejecutarConsulta(sql, "ORDER BY - Pedidos más recientes");
    }
    
    private static void consultaAnalisisTrimestral() {
        String sql = "SELECT " +
                    "    YEAR(FechaPedido) AS Año, " +
                    "    QUARTER(FechaPedido) AS Trimestre, " +
                    "    COUNT(*) AS TotalPedidos, " +
                    "    SUM(TotalPedido) AS VentaTotal, " +
                    "    AVG(TotalPedido) AS PromedioVenta " +
                    "FROM Pedidos " +
                    "GROUP BY YEAR(FechaPedido), QUARTER(FechaPedido) " +
                    "ORDER BY Año DESC, Trimestre DESC";
        
        ejecutarConsulta(sql, "Análisis por Trimestre");
    }
    
    private static void consultaJoinMultiple() {
        String sql = "SELECT " +
                    "    p.PedidoID, " +
                    "    p.FechaPedido, " +
                    "    CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente, " +
                    "    pr.NombreProducto, " +
                    "    m.NombreMarca, " +
                    "    dp.Cantidad, " +
                    "    dp.PrecioUnitarioVenta, " +
                    "    dp.Subtotal " +
                    "FROM Pedidos p " +
                    "INNER JOIN Cliente c ON p.ClienteID = c.ClienteID " +
                    "INNER JOIN DetallePedido dp ON p.PedidoID = dp.PedidoID " +
                    "INNER JOIN Productos pr ON dp.ProductoID = pr.ProductoID " +
                    "LEFT JOIN Marcas m ON pr.MarcaID = m.MarcaID " +
                    "ORDER BY p.FechaPedido DESC " +
                    "LIMIT 15";
        
        ejecutarConsulta(sql, "JOIN Múltiple - Detalle completo de pedidos");
    }
    
    private static void ejecutarConsulta(String sql, String titulo) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║  " + titulo);
            System.out.println("╚════════════════════════════════════════════════════════╝\n");
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Imprimir encabezados
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-20s", metaData.getColumnName(i));
            }
            System.out.println("\n" + "─".repeat(columnCount * 20));
            
            // Imprimir datos
            int count = 0;
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    System.out.printf("%-20s", value != null ? value : "NULL");
                }
                System.out.println();
                count++;
            }
            
            System.out.println("\n✓ Total de registros: " + count);
            
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar consulta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
    }
    
    // ========== PUNTO 2: PROCEDIMIENTO VENTAS DIARIAS ==========
    
    private static void ejecutarProcedimientoVentasDiarias() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║      REPORTE DE VENTAS DIARIAS (Punto 2)            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.print("\nIngrese la fecha (YYYY-MM-DD): ");
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
                
                System.out.println("\n--- Resultado " + resultSetCount + " ---");
                imprimirResultSet(rs);
                
                hasResults = cstmt.getMoreResults();
            }
            
            System.out.println("\n✓ Procedimiento ejecutado exitosamente.");
            
        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar procedimiento: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, cstmt, rs);
        }
        
        pausar();
    }
    
    // ========== PUNTO 3: PROCEDIMIENTO CLIENTES Q1 ==========
    
    private static void ejecutarProcedimientoClientesQ1() {
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
                    
                    System.out.println("\n--- Resultado " + resultSetCount + " ---");
                    imprimirResultSet(rs);
                    
                    hasResults = cstmt.getMoreResults();
                }
                
                System.out.println("\n✓ Procedimiento ejecutado exitosamente.");
                
            } catch (SQLException e) {
                System.err.println("❌ Error al ejecutar procedimiento: " + e.getMessage());
                e.printStackTrace();
            } finally {
                DatabaseConnection.closeResources(conn, cstmt, rs);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Año inválido.");
        }
        
        pausar();
    }
    
    // ========== PUNTO 4: PROCEDIMIENTO INSERTAR CLIENTE CON VALIDACIÓN ==========
    
    private static void ejecutarProcedimientoInsertarCliente() {
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
            
            while (hasResults) {
                rs = cstmt.getResultSet();
                imprimirResultSet(rs);
                hasResults = cstmt.getMoreResults();
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, cstmt, rs);
        }
        
        pausar();
    }
    
    // ========== PUNTOS 5 Y 6: TRIGGERS ==========
    
    private static void menuTriggers() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║           PROBAR TRIGGERS (Puntos 5 y 6)            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n  1. 📦 Trigger Inventario - Insertar ingrediente");
        System.out.println("  2. 📦 Trigger Inventario - Actualizar stock");
        System.out.println("  3. 🛍️  Trigger Seguimiento - Insertar pedido online");
        System.out.println("  4. 📋 Ver Log de Inventario");
        System.out.println("  5. 📋 Ver Seguimiento de Clientes Online");
        System.out.println("  0. Volver al menú principal");
        System.out.print("\n▶️  Seleccione opción: ");
        
        int opcion = leerOpcion();
        
        switch (opcion) {
            case 1:
                insertarIngrediente();
                break;
            case 2:
                actualizarStockIngrediente();
                break;
            case 3:
                insertarPedidoOnline();
                break;
            case 4:
                verLogInventario();
                break;
            case 5:
                verSeguimientoClientesOnline();
                break;
            case 0:
                return;
            default:
                System.out.println("\n❌ Opción inválida.");
        }
        
        pausar();
    }
    
    private static void insertarIngrediente() {
        System.out.println("\n=== INSERTAR INGREDIENTE (Activa Trigger) ===");
        
        System.out.print("IngredienteID (ej: ING013): ");
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
                System.out.println("\n✓ Marca eliminada exitosamente.");
            } else {
                System.out.println("❌ Marca no encontrada.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("Posible causa: La marca tiene productos asociados.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    // ========== OPERACIONES CRUD ==========
    
    private static void menuCRUD() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║            OPERACIONES CRUD                          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n  1. ➕ CREATE - Crear nuevo producto");
        System.out.println("  2. 📖 READ - Leer productos");
        System.out.println("  3. ✏️  UPDATE - Actualizar producto");
        System.out.println("  4. 🗑️  DELETE - Eliminar producto");
        System.out.println("  0. Volver");
        System.out.print("\n▶️  Seleccione operación: ");
        
        int opcion = leerOpcion();
        
        switch (opcion) {
            case 1:
                crearProducto();
                break;
            case 2:
                leerProductos();
                break;
            case 3:
                actualizarProducto();
                break;
            case 4:
                eliminarProducto();
                break;
            case 0:
                return;
            default:
                System.out.println("\n❌ Opción inválida.");
        }
        
        pausar();
    }
    
    private static void crearProducto() {
        System.out.println("\n=== CREATE - CREAR NUEVO PRODUCTO ===");
        
        System.out.print("ProductoID (ej: PR9): ");
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
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    private static void leerProductos() {
        System.out.println("\n=== READ - LEER PRODUCTOS ===");
        System.out.println("  1. Ver todos los productos");
        System.out.println("  2. Buscar producto por ID");
        System.out.print("\n▶️  Seleccione: ");
        
        int opcion = leerOpcion();
        
        if (opcion == 1) {
            String sql = "SELECT p.ProductoID, p.NombreProducto, m.NombreMarca, " +
                        "p.Descripcion, p.PrecioBase " +
                        "FROM Productos p " +
                        "LEFT JOIN Marcas m ON p.MarcaID = m.MarcaID " +
                        "ORDER BY p.ProductoID";
            ejecutarConsulta(sql, "LISTA DE PRODUCTOS");
        } else if (opcion == 2) {
            System.out.print("\nIngrese ProductoID: ");
            String prodID = scanner.nextLine();
            
            String sql = "SELECT p.*, m.NombreMarca " +
                        "FROM Productos p " +
                        "LEFT JOIN Marcas m ON p.MarcaID = m.MarcaID " +
                        "WHERE p.ProductoID = '" + prodID + "'";
            ejecutarConsulta(sql, "DETALLE DEL PRODUCTO");
        }
    }
    
    private static void actualizarProducto() {
        System.out.println("\n=== UPDATE - ACTUALIZAR PRODUCTO ===");
        
        System.out.print("ProductoID a actualizar: ");
        String prodID = scanner.nextLine();
        
        System.out.print("Nuevo Precio Base: ");
        String precio = scanner.nextLine();
        
        System.out.print("Nueva Descripción (Enter para mantener): ");
        String descripcion = scanner.nextLine();
        
        String sql;
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            if (descripcion.isEmpty()) {
                sql = "UPDATE Productos SET PrecioBase = ? WHERE ProductoID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, Double.parseDouble(precio));
                pstmt.setString(2, prodID);
            } else {
                sql = "UPDATE Productos SET PrecioBase = ?, Descripcion = ? WHERE ProductoID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, Double.parseDouble(precio));
                pstmt.setString(2, descripcion);
                pstmt.setString(3, prodID);
            }
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Producto actualizado exitosamente.");
            } else {
                System.out.println("❌ Producto no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    private static void eliminarProducto() {
        System.out.println("\n=== DELETE - ELIMINAR PRODUCTO ===");
        
        System.out.print("ProductoID a eliminar: ");
        String prodID = scanner.nextLine();
        
        System.out.print("¿Está seguro? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        String sql = "DELETE FROM Productos WHERE ProductoID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, prodID);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Producto eliminado exitosamente.");
            } else {
                System.out.println("❌ Producto no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("Posible causa: El producto está en pedidos o recetas.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private static void imprimirResultSet(ResultSet rs) throws SQLException {
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
                System.out.printf("%-25s", value != null ? value : "NULL");
            }
            System.out.println();
            count++;
        }
        
        if (count == 0) {
            System.out.println("(Sin resultados)");
        } else {
            System.out.println("\nTotal: " + count + " registro(s)");
        }
    }
    
    private static void pausar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}

/**
 * INSTRUCCIONES DE COMPILACIÓN Y EJECUCIÓN:
 * 
 * 1. Asegúrate de tener MySQL Connector/J en tu classpath
 * 
 * 2. Compilar (Windows):
 *    javac -cp .;mysql-connector-java-8.0.33.jar SistemaGestionProyectoFinal.java
 * 
 * 3. Compilar (Linux/Mac):
 *    javac -cp .:mysql-connector-java-8.0.33.jar SistemaGestionProyectoFinal.java
 * 
 * 4. Ejecutar (Windows):
 *    java -cp .;mysql-connector-java-8.0.33.jar SistemaGestionProyectoFinal
 * 
 * 5. Ejecutar (Linux/Mac):
 *    java -cp .:mysql-connector-java-8.0.33.jar SistemaGestionProyectoFinal
 * 
 * NOTA: No olvides cambiar las credenciales de conexión en DatabaseConnection
 */
            
            if (rows > 0) {
                System.out.println("\n✓ Ingrediente insertado exitosamente.");
                System.out.println("✓ Trigger activado - Revise el Log de Inventario.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    private static void actualizarStockIngrediente() {
        System.out.println("\n=== ACTUALIZAR STOCK (Activa Trigger) ===");
        
        System.out.print("IngredienteID a actualizar: ");
        String ingID = scanner.nextLine();
        
        System.out.print("Nuevo stock: ");
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
                System.out.println("✓ Trigger activado - Revise el Log de Inventario.");
            } else {
                System.out.println("❌ Ingrediente no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    private static void insertarPedidoOnline() {
        System.out.println("\n=== INSERTAR PEDIDO ONLINE (Activa Trigger) ===");
        
        System.out.print("PedidoID (ej: P21): ");
        String pedidoID = scanner.nextLine();
        
        System.out.print("ClienteID: ");
        String clienteID = scanner.nextLine();
        
        System.out.print("EmpleadoID: ");
        String empleadoID = scanner.nextLine();
        
        System.out.println("Plataforma (1=Rappi, 2=Uber Eats, 3=Didi Food): ");
        int plat = leerOpcion();
        String plataforma = plat == 1 ? "Rappi" : plat == 2 ? "Uber Eats" : "Didi Food";
        
        System.out.print("Total Pedido: ");
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
                System.out.println("✓ Trigger activado - Revise Seguimiento de Clientes Online.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    private static void verLogInventario() {
        String sql = "SELECT * FROM LogInventario ORDER BY FechaHora DESC LIMIT 10";
        ejecutarConsulta(sql, "LOG DE INVENTARIO (últimos 10 registros)");
    }
    
    private static void verSeguimientoClientesOnline() {
        String sql = "SELECT * FROM SeguimientoClientesOnline ORDER BY FechaHoraCompra DESC LIMIT 10";
        ejecutarConsulta(sql, "SEGUIMIENTO CLIENTES ONLINE (últimos 10 registros)");
    }
    
    // ========== ELIMINAR REGISTRO ==========
    
    private static void eliminarRegistro() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              ELIMINAR REGISTRO                       ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n  1. Eliminar Cliente");
        System.out.println("  2. Eliminar Ingrediente");
        System.out.println("  3. Eliminar Marca");
        System.out.println("  0. Volver");
        System.out.print("\n▶️  Seleccione tabla: ");
        
        int opcion = leerOpcion();
        
        switch (opcion) {
            case 1:
                eliminarCliente();
                break;
            case 2:
                eliminarIngrediente();
                break;
            case 3:
                eliminarMarca();
                break;
            case 0:
                return;
            default:
                System.out.println("\n❌ Opción inválida.");
        }
        
        pausar();
    }
    
    private static void eliminarCliente() {
        System.out.print("\nIngrese ClienteID a eliminar: ");
        String clienteID = scanner.nextLine();
        
        String sql = "DELETE FROM Cliente WHERE ClienteID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, clienteID);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Cliente eliminado exitosamente.");
            } else {
                System.out.println("❌ Cliente no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("Posible causa: El cliente tiene pedidos asociados.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    private static void eliminarIngrediente() {
        System.out.print("\nIngrese IngredienteID a eliminar: ");
        String ingID = scanner.nextLine();
        
        String sql = "DELETE FROM Ingredientes WHERE IngredienteID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ingID);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("\n✓ Ingrediente eliminado exitosamente.");
            } else {
                System.out.println("❌ Ingrediente no encontrado.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("Posible causa: El ingrediente está en recetas.");
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
    
    private static void eliminarMarca() {
        System.out.print("\nIngrese MarcaID a eliminar: ");
        String marcaID = scanner.nextLine();
        
        String sql = "DELETE FROM Marcas WHERE MarcaID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, marcaID);
            
            int rows = pstmt.executeUpdate();