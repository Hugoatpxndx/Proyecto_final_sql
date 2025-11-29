-- =====================================================
-- CONSULTAS AVANZADAS SQL - PROYECTO FINAL
-- =====================================================

-- 1. JOIN: Obtener pedidos con información de clientes y empleados
SELECT 
    p.PedidoID,
    p.FechaPedido,
    CONCAT(c.Nombre, ' ', c.Apellido) AS NombreCliente,
    c.Email AS EmailCliente,
    CONCAT(e.Nombre, ' ', e.Apellido) AS NombreEmpleado,
    p.EstadoPedido,
    p.PlataformaOrigen,
    p.TotalPedido
FROM Pedidos p
INNER JOIN Cliente c ON p.ClienteID = c.ClienteID
INNER JOIN Empleados e ON p.EmpleadoID = e.EmpleadoID
WHERE p.FechaPedido >= '2024-01-01'
ORDER BY p.FechaPedido DESC;

-- 2. JOIN MÚLTIPLE: Detalle completo de pedidos con productos y marcas
SELECT 
    p.PedidoID,
    p.FechaPedido,
    CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente,
    pr.NombreProducto,
    m.NombreMarca,
    dp.Cantidad,
    dp.PrecioUnitarioVenta,
    dp.Subtotal
FROM Pedidos p
INNER JOIN Cliente c ON p.ClienteID = c.ClienteID
INNER JOIN DetallePedido dp ON p.PedidoID = dp.PedidoID
INNER JOIN Productos pr ON dp.ProductoID = pr.ProductoID
LEFT JOIN Marcas m ON pr.MarcaID = m.MarcaID
ORDER BY p.FechaPedido DESC, p.PedidoID;

-- 3. UNION: Lista completa de contactos (Clientes y Proveedores)
SELECT 
    'Cliente' AS TipoContacto,
    ClienteID AS ID,
    CONCAT(Nombre, ' ', Apellido) AS NombreCompleto,
    Email,
    Telefono,
    FechaRegistro AS FechaAlta
FROM Cliente
UNION
SELECT 
    'Proveedor' AS TipoContacto,
    ProveedorID AS ID,
    ContactoNombre AS NombreCompleto,
    Email,
    ContactoTelefono AS Telefono,
    NULL AS FechaAlta
FROM Proveedores
ORDER BY TipoContacto, NombreCompleto;

-- 4. GROUP BY: Ventas totales por cliente
SELECT 
    c.ClienteID,
    CONCAT(c.Nombre, ' ', c.Apellido) AS NombreCliente,
    c.Email,
    COUNT(p.PedidoID) AS TotalPedidos,
    SUM(p.TotalPedido) AS MontoTotalComprado,
    AVG(p.TotalPedido) AS PromedioCompra,
    MAX(p.FechaPedido) AS UltimaCompra
FROM Cliente c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nombre, c.Apellido, c.Email
HAVING COUNT(p.PedidoID) > 0
ORDER BY MontoTotalComprado DESC;

-- 5. GROUP BY: Ventas por plataforma y mes
SELECT 
    DATE_FORMAT(FechaPedido, '%Y-%m') AS Mes,
    PlataformaOrigen,
    COUNT(PedidoID) AS NumeroPedidos,
    SUM(TotalPedido) AS VentaTotal,
    AVG(TotalPedido) AS PromedioVenta
FROM Pedidos
WHERE FechaPedido >= DATE_SUB(CURDATE(), INTERVAL 8 MONTH)
GROUP BY DATE_FORMAT(FechaPedido, '%Y-%m'), PlataformaOrigen
ORDER BY Mes DESC, VentaTotal DESC;

-- 6. GROUP BY: Productos más vendidos
SELECT 
    pr.ProductoID,
    pr.NombreProducto,
    m.NombreMarca,
    COUNT(dp.PedidoID) AS NumeroVentas,
    SUM(dp.Cantidad) AS CantidadTotal,
    SUM(dp.Subtotal) AS IngresoTotal
FROM Productos pr
INNER JOIN DetallePedido dp ON pr.ProductoID = dp.ProductoID
LEFT JOIN Marcas m ON pr.MarcaID = m.MarcaID
GROUP BY pr.ProductoID, pr.NombreProducto, m.NombreMarca
ORDER BY IngresoTotal DESC
LIMIT 10;

-- 7. ORDER BY: Pedidos ordenados por fecha (más recientes primero)
SELECT 
    PedidoID,
    ClienteID,
    FechaPedido,
    EstadoPedido,
    TotalPedido,
    DATEDIFF(CURDATE(), FechaPedido) AS DiasDesdeCompra
FROM Pedidos
ORDER BY FechaPedido DESC, TotalPedido DESC;

-- 8. ORDER BY: Clientes más antiguos
SELECT 
    ClienteID,
    CONCAT(Nombre, ' ', Apellido) AS NombreCompleto,
    Email,
    FechaRegistro,
    TIMESTAMPDIFF(YEAR, FechaRegistro, CURDATE()) AS AñosComoCliente,
    TIMESTAMPDIFF(MONTH, FechaRegistro, CURDATE()) AS MesesComoCliente
FROM Cliente
ORDER BY FechaRegistro ASC
LIMIT 20;

-- 9. Consulta avanzada: Análisis de ventas por trimestre
SELECT 
    YEAR(FechaPedido) AS Año,
    QUARTER(FechaPedido) AS Trimestre,
    CONCAT('Q', QUARTER(FechaPedido), '-', YEAR(FechaPedido)) AS PeriodoTrimestral,
    COUNT(DISTINCT ClienteID) AS ClientesUnicos,
    COUNT(PedidoID) AS TotalPedidos,
    SUM(TotalPedido) AS VentaTotal,
    AVG(TotalPedido) AS TicketPromedio
FROM Pedidos
GROUP BY YEAR(FechaPedido), QUARTER(FechaPedido), CONCAT('Q', QUARTER(FechaPedido), '-', YEAR(FechaPedido))
ORDER BY Año DESC, Trimestre DESC;

-- 10. Consulta con fechas: Pedidos del mes actual
SELECT 
    p.PedidoID,
    p.FechaPedido,
    DAYNAME(p.FechaPedido) AS DiaSemana,
    CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente,
    p.TotalPedido,
    p.EstadoPedido
FROM Pedidos p
INNER JOIN Cliente c ON p.ClienteID = c.ClienteID
WHERE MONTH(p.FechaPedido) = MONTH(CURDATE())
  AND YEAR(p.FechaPedido) = YEAR(CURDATE())
ORDER BY p.FechaPedido DESC;