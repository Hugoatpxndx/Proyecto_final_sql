-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS - PROYECTO FINAL
-- =====================================================


-- --------------------------------------------------------
-- 2. PROCEDIMIENTO: Reporte de ventas diarias(Q1)
-- --------------------------------------------------------

DELIMITER //

CREATE PROCEDURE sp_VentasDiarias(
    IN p_fecha_consulta DATE
)
BEGIN
    DECLARE v_total_dia DECIMAL(10,2);
    
    -- Calcular total del día
    SELECT COALESCE(SUM(TotalPedido), 0) INTO v_total_dia
    FROM Pedidos
    WHERE DATE(FechaPedido) = p_fecha_consulta;
    
    -- Reporte detallado de ventas del día
    SELECT 
        p.PedidoID,
        p.FechaPedido,
        TIME(NOW()) AS HoraConsulta,
        CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente,
        c.Email,
        CONCAT(e.Nombre, ' ', e.Apellido) AS EmpleadoAtendio,
        p.PlataformaOrigen,
        p.EstadoPedido,
        p.TotalPedido
    FROM Pedidos p
    INNER JOIN Cliente c ON p.ClienteID = c.ClienteID
    INNER JOIN Empleados e ON p.EmpleadoID = e.EmpleadoID
    WHERE DATE(p.FechaPedido) = p_fecha_consulta
    ORDER BY p.FechaPedido;
    
    -- Resumen total del día
    SELECT 
        p_fecha_consulta AS Fecha,
        COUNT(PedidoID) AS TotalPedidos,
        COALESCE(SUM(TotalPedido), 0) AS VentaTotal,
        COALESCE(AVG(TotalPedido), 0) AS PromedioVenta,
        COALESCE(MIN(TotalPedido), 0) AS VentaMinima,
        COALESCE(MAX(TotalPedido), 0) AS VentaMaxima
    FROM Pedidos
    WHERE DATE(FechaPedido) = p_fecha_consulta;
    
    -- Ventas por plataforma en ese día
    IF v_total_dia > 0 THEN
        SELECT 
            PlataformaOrigen,
            COUNT(PedidoID) AS NumeroPedidos,
            SUM(TotalPedido) AS TotalVentas,
            ROUND((SUM(TotalPedido) / v_total_dia) * 100, 2) AS PorcentajeVentas
        FROM Pedidos
        WHERE DATE(FechaPedido) = p_fecha_consulta
        GROUP BY PlataformaOrigen
        ORDER BY TotalVentas DESC;
    ELSE
        SELECT 'No hay ventas para esta fecha' AS Mensaje;
    END IF;
END //

DELIMITER ;

-- Ejemplo de uso:
CALL sp_VentasDiarias('2025-05-05');


-- --------------------------------------------------------
-- 3. PROCEDIMIENTO: Reporte de clientes vigentes (Q1)
-- --------------------------------------------------------

DELIMITER //

CREATE PROCEDURE sp_ClientesVigentesQ1(
    IN año_consulta INT
)
BEGIN
    DECLARE fecha_inicio DATE;
    DECLARE fecha_fin DATE;
    
    -- Definir el primer trimestre
    SET fecha_inicio = CONCAT(año_consulta, '-01-01');
    SET fecha_fin = CONCAT(año_consulta, '-03-31');
    
    -- Reporte de clientes vigentes con sus estadísticas
    SELECT 
        c.ClienteID,
        CONCAT(c.Nombre, ' ', c.Apellido) AS NombreCompleto,
        c.Email,
        c.Telefono,
        c.Direccion,
        c.FechaRegistro,
        COUNT(p.PedidoID) AS PedidosEnQ1,
        SUM(p.TotalPedido) AS TotalComprado,
        MIN(p.FechaPedido) AS PrimeraCompraQ1,
        MAX(p.FechaPedido) AS UltimaCompraQ1,
        GROUP_CONCAT(DISTINCT p.PlataformaOrigen SEPARATOR ', ') AS PlataformasUsadas
    FROM Cliente c
    INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
    WHERE p.FechaPedido BETWEEN fecha_inicio AND fecha_fin
    GROUP BY c.ClienteID, c.Nombre, c.Apellido, c.Email, 
             c.Telefono, c.Direccion, c.FechaRegistro
    ORDER BY TotalComprado DESC;
    
    -- Resumen ejecutivo
    SELECT 
        'Resumen Q1' AS Reporte,
        COUNT(DISTINCT c.ClienteID) AS ClientesActivos,
        COUNT(p.PedidoID) AS TotalPedidos,
        SUM(p.TotalPedido) AS VentaTotal,
        AVG(p.TotalPedido) AS TicketPromedio,
        fecha_inicio AS FechaInicio,
        fecha_fin AS FechaFin
    FROM Cliente c
    INNER JOIN Pedidos p ON c.ClienteID = p.ClienteID
    WHERE p.FechaPedido BETWEEN fecha_inicio AND fecha_fin;
END //

DELIMITER ;

-- Ejemplo de uso:
CALL sp_ClientesVigentesQ1(2024);


-- --------------------------------------------------------
-- PROCEDIMIENTO ADICIONAL: Reporte mensual de ventas
-- --------------------------------------------------------

DELIMITER //

CREATE PROCEDURE sp_ReporteMensualVentas(
    IN mes_consulta INT,
    IN año_consulta INT
)
BEGIN
    -- Validar parámetros
    IF mes_consulta < 1 OR mes_consulta > 12 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El mes debe estar entre 1 y 12';
    END IF;
    
    -- Ventas del mes
    SELECT 
        DATE(p.FechaPedido) AS Fecha,
        COUNT(p.PedidoID) AS NumeroPedidos,
        SUM(p.TotalPedido) AS VentasDia,
        COUNT(DISTINCT p.ClienteID) AS ClientesUnicos
    FROM Pedidos p
    WHERE MONTH(p.FechaPedido) = mes_consulta
      AND YEAR(p.FechaPedido) = año_consulta
    GROUP BY DATE(p.FechaPedido)
    ORDER BY Fecha;
    
    -- Top 5 clientes del mes
    SELECT 
        CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente,
        COUNT(p.PedidoID) AS Pedidos,
        SUM(p.TotalPedido) AS TotalGastado
    FROM Pedidos p
    INNER JOIN Cliente c ON p.ClienteID = c.ClienteID
    WHERE MONTH(p.FechaPedido) = mes_consulta
      AND YEAR(p.FechaPedido) = año_consulta
    GROUP BY c.ClienteID, c.Nombre, c.Apellido
    ORDER BY TotalGastado DESC
    LIMIT 5;
END //

DELIMITER ;

-- Ejemplo de uso:
-- CALL sp_ReporteMensualVentas(11, 2024);
