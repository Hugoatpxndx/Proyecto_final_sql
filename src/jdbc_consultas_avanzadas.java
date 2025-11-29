/**
 * ConsultasAvanzadas.java
 * Clase para ejecutar las consultas SQL avanzadas (Punto 1)
 */

import java.sql.*;

public class ConsultasAvanzadas {
    
    /**
     * Consulta JOIN - Pedidos con información de clientes y empleados
     */
    public static void consultaJoinPedidos() {
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
        
        Utilidades.ejecutarConsulta(sql, "JOIN - Pedidos con información completa");
    }
    
    /**
     * Consulta UNION - Lista unificada de contactos
     */
    public static void consultaUnionContactos() {
        String sql = "SELECT 'Cliente' AS Tipo, CONCAT(Nombre, ' ', Apellido) AS Nombre, Email " +
                    "FROM Cliente " +
                    "UNION " +
                    "SELECT 'Proveedor', ContactoNombre, Email " +
                    "FROM Proveedores " +
                    "ORDER BY Tipo, Nombre";
        
        Utilidades.ejecutarConsulta(sql, "UNION - Lista unificada de contactos");
    }
    
    /**
     * Consulta GROUP BY - Ventas totales por cliente
     */
    public static void consultaGroupByVentas() {
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
        
        Utilidades.ejecutarConsulta(sql, "GROUP BY - Ventas totales por cliente");
    }
    
    /**
     * Consulta ORDER BY - Pedidos más recientes
     */
    public static void consultaOrderByPedidos() {
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
        
        Utilidades.ejecutarConsulta(sql, "ORDER BY - Pedidos más recientes");
    }
    
    /**
     * Consulta con Fechas - Análisis trimestral
     */
    public static void consultaAnalisisTrimestral() {
        String sql = "SELECT " +
                    "    YEAR(FechaPedido) AS Año, " +
                    "    QUARTER(FechaPedido) AS Trimestre, " +
                    "    COUNT(*) AS TotalPedidos, " +
                    "    SUM(TotalPedido) AS VentaTotal, " +
                    "    AVG(TotalPedido) AS PromedioVenta " +
                    "FROM Pedidos " +
                    "GROUP BY YEAR(FechaPedido), QUARTER(FechaPedido) " +
                    "ORDER BY Año DESC, Trimestre DESC";
        
        Utilidades.ejecutarConsulta(sql, "Análisis por Trimestre");
    }
    
    /**
     * Consulta JOIN Múltiple - Detalle completo de pedidos con productos
     */
    public static void consultaJoinMultiple() {
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
        
        Utilidades.ejecutarConsulta(sql, "JOIN Múltiple - Detalle completo de pedidos");
    }
    
    /**
     * Consulta adicional - Productos más vendidos
     */
    public static void consultaProductosMasVendidos() {
        String sql = "SELECT " +
                    "    pr.ProductoID, " +
                    "    pr.NombreProducto, " +
                    "    m.NombreMarca, " +
                    "    COUNT(DISTINCT dp.PedidoID) AS NumeroVentas, " +
                    "    SUM(dp.Cantidad) AS CantidadTotal, " +
                    "    SUM(dp.Subtotal) AS IngresoTotal " +
                    "FROM Productos pr " +
                    "INNER JOIN DetallePedido dp ON pr.ProductoID = dp.ProductoID " +
                    "LEFT JOIN Marcas m ON pr.MarcaID = m.MarcaID " +
                    "GROUP BY pr.ProductoID, pr.NombreProducto, m.NombreMarca " +
                    "ORDER BY IngresoTotal DESC " +
                    "LIMIT 10";
        
        Utilidades.ejecutarConsulta(sql, "Productos más vendidos");
    }
    
    /**
     * Consulta adicional - Ventas por plataforma
     */
    public static void consultaVentasPorPlataforma() {
        String sql = "SELECT " +
                    "    PlataformaOrigen, " +
                    "    COUNT(PedidoID) AS NumeroPedidos, " +
                    "    SUM(TotalPedido) AS VentaTotal, " +
                    "    AVG(TotalPedido) AS PromedioVenta, " +
                    "    MIN(TotalPedido) AS VentaMinima, " +
                    "    MAX(TotalPedido) AS VentaMaxima " +
                    "FROM Pedidos " +
                    "GROUP BY PlataformaOrigen " +
                    "ORDER BY VentaTotal DESC";
        
        Utilidades.ejecutarConsulta(sql, "Ventas por Plataforma");
    }
}