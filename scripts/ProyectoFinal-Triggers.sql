-- =====================================================
-- TRIGGERS - PROYECTO FINAL
-- =====================================================

-- --------------------------------------------------------
-- 5. TRIGGER: Control de inventario de ingredientes
-- --------------------------------------------------------

-- Crear tabla de log para el inventario
CREATE TABLE IF NOT EXISTS LogInventario (
    LogID INT AUTO_INCREMENT PRIMARY KEY,
    IngredienteID VARCHAR(255),
    Accion VARCHAR(50),
    StockAnterior DECIMAL(10,2),
    StockNuevo DECIMAL(10,2),
    Usuario VARCHAR(100),
    FechaHora DATETIME,
    Mensaje TEXT
);

DELIMITER //

-- Trigger BEFORE INSERT: Validar que no exista ingrediente duplicado
CREATE TRIGGER trg_ValidarIngredienteAntes_Insert
BEFORE INSERT ON Ingredientes
FOR EACH ROW
BEGIN
    DECLARE existe_ingrediente INT;
    DECLARE mensaje_error VARCHAR(500);
    
    -- Verificar si ya existe un ingrediente con el mismo ID
    SELECT COUNT(*) INTO existe_ingrediente
    FROM Ingredientes
    WHERE IngredienteID = NEW.IngredienteID;
    
    IF existe_ingrediente > 0 THEN
        SET mensaje_error = CONCAT(
            'ERROR: El ingrediente con ID "', NEW.IngredienteID, 
            '" ya existe en el sistema. No se permiten duplicados.'
        );
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = mensaje_error;
    END IF;
    
    -- Validar que el stock no sea negativo
    IF NEW.StockActual < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'ERROR: El stock actual no puede ser negativo.';
    END IF;
    
    -- Validar que el costo unitario sea positivo
    IF NEW.CostoUnitario <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'ERROR: El costo unitario debe ser mayor a cero.';
    END IF;
END //

-- Funciona
INSERT INTO Ingredientes (IngredienteID, NombreIngrediente, StockActual, UnidadMedida, CostoUnitario)
VALUES ('I9', 'Tomate', 50, 'kg', 12.50);

-- Produce Error *Exisetente
INSERT INTO Ingredientes (IngredienteID, NombreIngrediente, StockActual, UnidadMedida, CostoUnitario)
VALUES ('I9', 'Tomate fresco', 20, 'kg', 14.00);

-- Produce Error *Stock Negativo
INSERT INTO Ingredientes (IngredienteID, NombreIngrediente, StockActual, UnidadMedida, CostoUnitario)
VALUES ('I10', 'Cebolla', -5, 'kg', 8.00);

-- Produce Error *Costo no valido
INSERT INTO Ingredientes (IngredienteID, NombreIngrediente, StockActual, UnidadMedida, CostoUnitario)
VALUES ('I11', 'Ajo', 10, 'kg', 0);


-- Trigger AFTER INSERT: Registrar en log
CREATE TRIGGER trg_RegistrarInventario_Insert
AFTER INSERT ON Ingredientes
FOR EACH ROW
BEGIN
    INSERT INTO LogInventario (
        IngredienteID, 
        Accion, 
        StockAnterior, 
        StockNuevo, 
        Usuario, 
        FechaHora, 
        Mensaje
    )
    VALUES (
        NEW.IngredienteID,
        'INSERT',
        0,
        NEW.StockActual,
        USER(),
        NOW(),
        CONCAT('Nuevo ingrediente agregado: ', NEW.NombreIngrediente, 
               ' - Stock inicial: ', NEW.StockActual, ' ', NEW.UnidadMedida)
    );
END //

-- Funciona
INSERT INTO Ingredientes (IngredienteID, NombreIngrediente, StockActual, UnidadMedida, CostoUnitario)
VALUES ('I10', 'Zanahoria', 30, 'kg', 7.00);

-- Produce Error *Ya existe en el sistema
INSERT INTO Ingredientes (IngredienteID, NombreIngrediente, StockActual, UnidadMedida, CostoUnitario)
VALUES ('I10', 'Zanahoria Premium', 10, 'kg', 6.00);



-- Trigger BEFORE UPDATE: Validar actualizaciones de stock
CREATE TRIGGER trg_ValidarInventario_Update
BEFORE UPDATE ON Ingredientes
FOR EACH ROW
BEGIN
    -- Validar que el stock no sea negativo
    IF NEW.StockActual < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'ERROR: El stock no puede ser negativo después de la actualización.';
    END IF;
    
    -- Alerta de stock bajo (menos de 10 unidades)
    IF NEW.StockActual < 10 AND OLD.StockActual >= 10 THEN
        INSERT INTO LogInventario (
            IngredienteID, 
            Accion, 
            StockAnterior, 
            StockNuevo, 
            Usuario, 
            FechaHora, 
            Mensaje
        )
        VALUES (
            NEW.IngredienteID,
            'ALERTA_STOCK_BAJO',
            OLD.StockActual,
            NEW.StockActual,
            USER(),
            NOW(),
            CONCAT('¡ALERTA! Stock bajo para: ', NEW.NombreIngrediente, 
                   ' - Stock actual: ', NEW.StockActual, ' ', NEW.UnidadMedida)
        );
    END IF;
END //

-- Funciona
UPDATE Ingredientes
SET StockActual = 8
WHERE IngredienteID = 'I1';

-- Produce Error *Stock Negativo
UPDATE Ingredientes
SET StockActual = -3
WHERE IngredienteID = 'I1';


-- Trigger AFTER UPDATE: Registrar cambios en inventario
CREATE TRIGGER trg_RegistrarInventario_Update
AFTER UPDATE ON Ingredientes
FOR EACH ROW
BEGIN
    -- Solo registrar si hubo cambio en el stock
    IF OLD.StockActual != NEW.StockActual THEN
        INSERT INTO LogInventario (
            IngredienteID, 
            Accion, 
            StockAnterior, 
            StockNuevo, 
            Usuario, 
            FechaHora, 
            Mensaje
        )
        VALUES (
            NEW.IngredienteID,
            'UPDATE',
            OLD.StockActual,
            NEW.StockActual,
            USER(),
            NOW(),
            CONCAT('Stock actualizado para: ', NEW.NombreIngrediente, 
                   ' - Cambio: ', (NEW.StockActual - OLD.StockActual), 
                   ' ', NEW.UnidadMedida)
        );
    END IF;
END //

DELIMITER ;

-- Funciona
UPDATE Ingredientes
SET StockActual = StockActual + 20
WHERE IngredienteID = 'I1';

-- No hace ningun cambio
UPDATE Ingredientes
SET StockActual = StockActual
WHERE IngredienteID = 'I1';

-- Produce Error *Stock no puede ser negativo
UPDATE Ingredientes
SET StockActual = -100
WHERE IngredienteID = 'I4';


-- --------------------------------------------------------
-- 6. TRIGGER: Seguimiento de clientes en compras online
-- --------------------------------------------------------

-- Crear tabla de seguimiento
CREATE TABLE IF NOT EXISTS SeguimientoClientesOnline (
    SeguimientoID INT AUTO_INCREMENT PRIMARY KEY,
    ClienteID VARCHAR(255),
    NombreCliente VARCHAR(100),
    EmailCliente VARCHAR(255),
    TelefonoCliente VARCHAR(10),
    PedidoID VARCHAR(255),
    PlataformaOrigen VARCHAR(255),
    TotalPedido INT,
    FechaHoraCompra DATETIME,
    EstadoSeguimiento VARCHAR(50) DEFAULT 'PENDIENTE',
    NotasAdicionales TEXT,
    FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID),
    FOREIGN KEY (PedidoID) REFERENCES Pedidos(PedidoID)
);

DELIMITER //

-- Trigger para compras online (Rappi, Uber Eats, Didi Food)
CREATE TRIGGER trg_SeguimientoClientesOnline
AFTER INSERT ON Pedidos
FOR EACH ROW
BEGIN
    DECLARE nombre_completo VARCHAR(100);
    DECLARE email_cliente VARCHAR(255);
    DECLARE telefono_cliente VARCHAR(10);

    IF NEW.PlataformaOrigen IN ('Rappi', 'Uber Eats', 'Didi Food') THEN

        SELECT CONCAT(Nombre, ' ', Apellido), Email, Telefono
        INTO nombre_completo, email_cliente, telefono_cliente
        FROM Cliente
        WHERE ClienteID = NEW.ClienteID;

        INSERT INTO SeguimientoClientesOnline (
            ClienteID,
            NombreCliente,
            EmailCliente,
            TelefonoCliente,
            PedidoID,
            PlataformaOrigen,
            TotalPedido,
            FechaHoraCompra,
            EstadoSeguimiento,
            NotasAdicionales
        )
        VALUES (
            NEW.ClienteID,
            nombre_completo,
            email_cliente,
            telefono_cliente,
            NEW.PedidoID,
            NEW.PlataformaOrigen,
            NEW.TotalPedido,
            NOW(),
            'PENDIENTE',
            CONCAT(
                'Cliente realizó compra por ', NEW.PlataformaOrigen,
                '. Monto: ', NEW.TotalPedido,
                '. Requiere seguimiento inmediato.'
            )
        );

    END IF;
END //

-- Funciona
INSERT INTO Cliente (ClienteID, Nombre, Apellido, Email, Telefono)
VALUES ('C9', 'Juan', 'Pérez', 'juanperez@example.com', '5511223344');

INSERT INTO Pedidos (PedidoID, ClienteID, PlataformaOrigen, TotalPedido, EstadoPedido)
VALUES ('P9', 'C9', 'Rappi', 250, 'En Proceso');



-- Trigger para actualizar seguimiento cuando cambia el estado del pedido
CREATE TRIGGER trg_ActualizarSeguimiento_EstadoPedido
AFTER UPDATE ON Pedidos
FOR EACH ROW
BEGIN
    IF OLD.EstadoPedido != NEW.EstadoPedido THEN

        UPDATE SeguimientoClientesOnline
        SET 
            EstadoSeguimiento = CASE 
                WHEN NEW.EstadoPedido = 'Entregado' THEN 'COMPLETADO'
                WHEN NEW.EstadoPedido = 'Cancelado' THEN 'CANCELADO'
                WHEN NEW.EstadoPedido = 'En Proceso' THEN 'EN_SEGUIMIENTO'
                WHEN NEW.EstadoPedido IS NULL THEN 'SIN_ESTADO'
                ELSE 'PENDIENTE'
            END,
            NotasAdicionales = CONCAT(
                NotasAdicionales,
                ' | Actualización: ', NOW(), 
                ' - Estado cambió de "', OLD.EstadoPedido,
                '" a "', NEW.EstadoPedido, '"'
            )
        WHERE PedidoID = NEW.PedidoID;

    END IF;
END //

DELIMITER ;

-- Funciona
UPDATE Pedidos
SET EstadoPedido = 'Entregado'
WHERE PedidoID = 'P1';

-- Error no actualiza nada
UPDATE Pedidos
SET EstadoPedido = 'Cancelado'
WHERE PedidoID = 'P99';

-- Error EstadoPedido no puede ser null
UPDATE Pedidos
SET EstadoPedido = NULL
WHERE PedidoID = 'P1';


