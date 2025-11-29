-- =====================================================
-- PROCEDIMIENTO CON MANEJO DE EXCEPCIONES
-- =====================================================

-- Primero, agregar constraint UNIQUE al email si no existe
ALTER TABLE Cliente 
ADD CONSTRAINT UK_Cliente_Email UNIQUE (Email);

-- --------------------------------------------------------
-- 4. PROCEDIMIENTO: Insertar cliente con validación de email único
-- --------------------------------------------------------

DELIMITER //

CREATE PROCEDURE sp_InsertarCliente(
    IN p_ClienteID VARCHAR(255),
    IN p_Nombre VARCHAR(40),
    IN p_Apellido VARCHAR(40),
    IN p_Email VARCHAR(255),
    IN p_Telefono VARCHAR(10),
    IN p_Direccion VARCHAR(255)
)
sp_InsertarCliente: BEGIN
    DECLARE existe_email INT DEFAULT 0;
    DECLARE mensaje_salida VARCHAR(500);
    DECLARE codigo_error INT;
    
    -- Declarar handler para errores de duplicación
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            codigo_error = MYSQL_ERRNO;
        
        IF codigo_error = 1062 THEN
            -- Error de clave duplicada
            SET mensaje_salida = CONCAT(
                'ERROR: El correo electrónico "', p_Email, 
                '" ya está registrado en el sistema. Por favor, use un correo diferente.'
            );
            SELECT mensaje_salida AS Resultado, 'ERROR' AS Estado;
        ELSE
            SET mensaje_salida = CONCAT(
                'ERROR: Ocurrió un error al insertar el cliente. Código: ', 
                codigo_error
            );
            SELECT mensaje_salida AS Resultado, 'ERROR' AS Estado;
        END IF;
    END;
    
    -- Validaciones previas
    IF p_Email IS NULL OR p_Email = '' THEN
        SELECT 'ERROR: El correo electrónico es obligatorio.' AS Resultado, 
               'ERROR' AS Estado;
        LEAVE sp_InsertarCliente;
    END IF;
    
    IF p_ClienteID IS NULL OR p_ClienteID = '' THEN
        SELECT 'ERROR: El ID del cliente es obligatorio.' AS Resultado, 
               'ERROR' AS Estado;
        LEAVE sp_InsertarCliente;
    END IF;
    
    -- Verificar si el email ya existe (validación previa)
    SELECT COUNT(*) INTO existe_email
    FROM Cliente
    WHERE Email = p_Email;
    
    IF existe_email > 0 THEN
        SELECT CONCAT(
            'ERROR: El correo electrónico "', p_Email, 
            '" ya existe en la base de datos. ',
            'No se pueden registrar dos clientes con el mismo correo.'
        ) AS Resultado, 'ERROR' AS Estado;
        LEAVE sp_InsertarCliente;
    END IF;
    
    -- Intentar insertar el cliente
    INSERT INTO Cliente (
        ClienteID, 
        Nombre, 
        Apellido, 
        Email, 
        Telefono, 
        FechaRegistro, 
        Direccion
    )
    VALUES (
        p_ClienteID,
        p_Nombre,
        p_Apellido,
        p_Email,
        p_Telefono,
        CURDATE(),
        p_Direccion
    );
    
    -- Si llegamos aquí, la inserción fue exitosa
    SELECT CONCAT(
        'Cliente "', p_Nombre, ' ', p_Apellido, 
        '" registrado exitosamente con ID: ', p_ClienteID
    ) AS Resultado, 'ÉXITO' AS Estado;
    
    -- Mostrar los datos insertados
    SELECT * FROM Cliente WHERE ClienteID = p_ClienteID;
    
END //

DELIMITER ;

-- --------------------------------------------------------
-- Ejemplos de uso:
-- --------------------------------------------------------

-- Caso 1: Inserción exitosa
CALL sp_InsertarCliente(
    'CLI001', 
    'Juan', 
    'Pérez', 
    'juan.perez@email.com', 
    '8112345678', 
    'Av. Principal #123'
);

-- Caso 2: Intento de duplicar email (debe generar error)
CALL sp_InsertarCliente(
    'CLI002', 
    'María', 
    'González', 
    'juan.perez@email.com',  -- Email duplicado
    '8187654321', 
    'Calle Secundaria #456'
);

-- Caso 3: Email vacío (debe generar error de validación)
CALL sp_InsertarCliente(
    'CLI003', 
    'Pedro', 
    'Ramírez', 
    '',  -- Email vacío
    '8199887766', 
    'Boulevard Norte #789'
);


-- --------------------------------------------------------
-- PROCEDIMIENTO ALTERNATIVO: Actualizar cliente con validación
-- --------------------------------------------------------

DELIMITER //

CREATE PROCEDURE sp_ActualizarEmailCliente(
    IN p_ClienteID VARCHAR(255),
    IN p_NuevoEmail VARCHAR(255)
)
BEGIN
    DECLARE existe_cliente INT DEFAULT 0;
    DECLARE email_duplicado INT DEFAULT 0;
    DECLARE email_actual VARCHAR(255);
    
    -- Handler para errores de duplicación
    DECLARE CONTINUE HANDLER FOR 1062
    BEGIN
        SELECT CONCAT(
            'ERROR: El correo "', p_NuevoEmail, 
            '" ya está siendo usado por otro cliente.'
        ) AS Resultado, 'ERROR' AS Estado;
    END;
    
    -- Verificar que el cliente existe
    SELECT COUNT(*), Email INTO existe_cliente, email_actual
    FROM Cliente
    WHERE ClienteID = p_ClienteID;
    
    IF existe_cliente = 0 THEN
        SELECT 'ERROR: Cliente no encontrado.' AS Resultado, 
               'ERROR' AS Estado;
        LEAVE sp_ActualizarEmailCliente;
    END IF;
    
    -- Verificar si el nuevo email ya existe en otro cliente
    SELECT COUNT(*) INTO email_duplicado
    FROM Cliente
    WHERE Email = p_NuevoEmail AND ClienteID != p_ClienteID;
    
    IF email_duplicado > 0 THEN
        SELECT CONCAT(
            'ERROR: El correo "', p_NuevoEmail, 
            '" ya está registrado por otro cliente.'
        ) AS Resultado, 'ERROR' AS Estado;
        LEAVE sp_ActualizarEmailCliente;
    END IF;
    
    -- Actualizar el email
    UPDATE Cliente
    SET Email = p_NuevoEmail
    WHERE ClienteID = p_ClienteID;
    
    SELECT CONCAT(
        'Email actualizado exitosamente de "', email_actual, 
        '" a "', p_NuevoEmail, '"'
    ) AS Resultado, 'ÉXITO' AS Estado;
    
END //

DELIMITER ;

-- Ejemplo de uso:
-- CALL sp_ActualizarEmailCliente('CLI001', 'juan.nuevo@email.com');