# Guía de Ejecución Paso a Paso - Proyecto Final

## 📋 Índice Rápido
1. [Preparación del Entorno](#preparación-del-entorno)
2. [Instalación de la Base de Datos](#instalación-de-la-base-de-datos)
3. [Ejecución de Consultas SQL](#ejecución-de-consultas-sql)
4. [Prueba de Procedimientos](#prueba-de-procedimientos)
5. [Verificación de Triggers](#verificación-de-triggers)
6. [Ejecución de Aplicación Java](#ejecución-de-aplicación-java)
7. [Solución de Problemas](#solución-de-problemas)

---

## 1. Preparación del Entorno

### Requisitos Previos
- ✅ MySQL Server 8.0+ instalado y funcionando
- ✅ MySQL Workbench (opcional, recomendado)
- ✅ JDK 11 o superior
- ✅ IDE Java (IntelliJ IDEA, Eclipse, o NetBeans)
- ✅ MySQL Connector/J 8.0.33

### Verificar MySQL está corriendo
```bash
# Windows
net start MySQL80

# Linux/Mac
sudo systemctl status mysql
# o
mysql.server status
```

### Conectar a MySQL
```bash
mysql -u root -p
```

---

## 2. Instalación de la Base de Datos

### Paso 1: Crear el Schema
```sql
CREATE SCHEMA ProyectoFinal;
USE ProyectoFinal;
```

### Paso 2: Ejecutar Script de Creación
Copiar y ejecutar todo el contenido de `ProyectoFinal-1.sql` (el archivo proporcionado):

```sql
-- Ejecutar todas las sentencias CREATE TABLE
-- Ejecutar todas las sentencias ALTER TABLE (Foreign Keys)
```

### Paso 3: Verificar Creación de Tablas
```sql
USE ProyectoFinal;
SHOW TABLES;
```

**Resultado esperado** (9 tablas):
```
+-------------------------+
| Tables_in_ProyectoFinal |
+-------------------------+
| Cliente                 |
| DetallePedido           |
| Empleados               |
| Ingredientes            |
| Marcas                  |
| Pedidos                 |
| Productos               |
| Proveedores             |
| Recetas                 |
+-------------------------+
```

### Paso 4: Insertar Datos de Prueba
Ejecutar el script completo de **"Script de Datos de Prueba"**:

```sql
-- Copiar y ejecutar todas las sentencias INSERT
-- Verificar con:
SELECT 'Empleados' AS Tabla, COUNT(*) AS Total FROM Empleados
UNION ALL
SELECT 'Clientes', COUNT(*) FROM Cliente
UNION ALL
SELECT 'Productos', COUNT(*) FROM Productos
UNION ALL
SELECT 'Pedidos', COUNT(*) FROM Pedidos;
```

**Resultado esperado**:
```
+-----------+-------+
| Tabla     | Total |
+-----------+-------+
| Empleados |     5 |
| Clientes  |    10 |
| Productos |    10 |
| Pedidos   |    20 |
+-----------+-------+
```

---

## 3. Ejecución de Consultas SQL

### Consulta 1: JOIN - Pedidos con información completa
```sql
USE ProyectoFinal;

SELECT 
    p.PedidoID,
    p.FechaPedido,
    CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente,
    CONCAT(e.Nombre, ' ', e.Apellido) AS Empleado,
    p.EstadoPedido,
    p.TotalPedido
FROM Pedidos p
INNER JOIN Cliente c ON p.ClienteID = c.ClienteID
INNER JOIN Empleados e ON p.EmpleadoID = e.EmpleadoID
ORDER BY p.FechaPedido DESC
LIMIT 10;
```

**Resultado esperado**: 10 filas con información completa de pedidos

### Consulta 2: UNION - Contactos unificados
```sql
SELECT 
    'Cliente' AS TipoContacto,
    CONCAT(Nombre, ' ', Apellido) AS NombreCompleto,
    Email
FROM Cliente
UNION
SELECT 
    'Proveedor',
    ContactoNombre,
    Email
FROM Proveedores
ORDER BY TipoContacto, NombreCompleto;
```

**Resultado esperado**: 15 filas (10 clientes + 5 proveedores)

### Consulta 3: GROUP BY - Ventas por cliente
```sql
SELECT 
    CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente,
    COUNT(p.PedidoID) AS TotalPedidos,
    SUM(p.TotalPedido) AS MontoTotal,
    AVG(p.TotalPedido) AS PromedioCompra
FROM Cliente c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID, c.Nombre, c.Apellido
HAVING COUNT(p.PedidoID) > 0
ORDER BY MontoTotal DESC;
```

**Resultado esperado**: Lista de clientes con sus estadísticas de compra

### Consulta 4: ORDER BY - Pedidos más recientes
```sql
SELECT 
    PedidoID,
    ClienteID,
    FechaPedido,
    EstadoPedido,
    TotalPedido
FROM Pedidos
ORDER BY FechaPedido DESC, TotalPedido DESC
LIMIT 5;
```

**Resultado esperado**: 5 pedidos más recientes

### Consulta 5: Fechas - Análisis trimestral
```sql
SELECT 
    YEAR(FechaPedido) AS Año,
    QUARTER(FechaPedido) AS Trimestre,
    COUNT(*) AS TotalPedidos,
    SUM(TotalPedido) AS VentaTotal
FROM Pedidos
GROUP BY YEAR(FechaPedido), QUARTER(FechaPedido)
ORDER BY Año DESC, Trimestre DESC;
```

---

## 4. Prueba de Procedimientos

### Crear Procedimientos Almacenados
Ejecutar todo el script de **"Procedimientos Almacenados"**:

```sql
-- Copiar y ejecutar todos los procedimientos:
-- sp_VentasDiarias
-- sp_ClientesVigentesQ1
-- sp_ReporteMensualVentas
-- sp_InsertarCliente
-- sp_ActualizarEmailCliente
```

### Verificar creación
```sql
SHOW PROCEDURE STATUS WHERE Db = 'ProyectoFinal';
```

### Prueba 1: Ventas Diarias
```sql
-- Ventas del 24 de noviembre
CALL sp_VentasDiarias('2024-11-24');
```

**Resultado esperado**:
- Result Set 1: Detalle de pedidos del día
- Result Set 2: Resumen total (ventas, promedios)
- Result Set 3: Distribución por plataforma

### Prueba 2: Clientes Vigentes Q1
```sql
CALL sp_ClientesVigentesQ1(2024);
```

**Resultado esperado**:
- Result Set 1: Lista de clientes activos en Q1 2024
- Result Set 2: Resumen ejecutivo

### Prueba 3: Inserción con Validación - Caso Exitoso
```sql
CALL sp_InsertarCliente(
    'CLI011', 
    'Roberto', 
    'Sánchez', 
    'roberto.sanchez@email.com', 
    '8199998888', 
    'Calle Nueva #100'
);
```

**Resultado esperado**:
```
Resultado: Cliente "Roberto Sánchez" registrado exitosamente con ID: CLI011
Estado: ÉXITO
```

### Prueba 4: Inserción con Validación - Email Duplicado
```sql
CALL sp_InsertarCliente(
    'CLI012', 
    'Pedro', 
    'López', 
    'roberto.sanchez@email.com',  -- Email ya existe
    '8188887777', 
    'Otra Dirección #200'
);
```

**Resultado esperado**:
```
Resultado: ERROR: El correo electrónico "roberto.sanchez@email.com" ya existe...
Estado: ERROR
```

---

## 5. Verificación de Triggers

### Crear Triggers
Ejecutar todo el script de **"Triggers del Sistema"**:

```sql
-- Copiar y ejecutar:
-- Tabla LogInventario
-- Triggers de control de inventario (5 triggers)
-- Tabla SeguimientoClientesOnline
-- Triggers de seguimiento de clientes (2 triggers)
```

### Verificar creación
```sql
SHOW TRIGGERS FROM ProyectoFinal;
```

**Resultado esperado**: 7 triggers creados

### Prueba 1: Trigger de Inventario - INSERT
```sql
-- Insertar nuevo ingrediente
INSERT INTO Ingredientes VALUES 
('ING013', 'PRV001', 'Vainilla Premium', 'lt', 15.5, 280.00);

-- Verificar log
SELECT * FROM LogInventario 
WHERE IngredienteID = 'ING013'
ORDER BY FechaHora DESC;
```

**Resultado esperado**:
- Ingrediente insertado correctamente
- Registro en LogInventario con acción 'INSERT'

### Prueba 2: Trigger de Inventario - Stock Bajo
```sql
-- Actualizar a stock bajo
UPDATE Ingredientes 
SET StockActual = 8 
WHERE IngredienteID = 'ING013';

-- Verificar alerta
SELECT * FROM LogInventario 
WHERE Accion = 'ALERTA_STOCK_BAJO'
ORDER BY FechaHora DESC
LIMIT 1;
```

**Resultado esperado**:
- Alerta generada en LogInventario
- Mensaje: "¡ALERTA! Stock bajo para: Vainilla Premium..."

### Prueba 3: Trigger Validación - Ingrediente Duplicado
```sql
-- Intentar insertar ingrediente duplicado
INSERT INTO Ingredientes VALUES 
('ING013', 'PRV001', 'Otro Ingrediente', 'kg', 20, 100);
```

**Resultado esperado**:
```
Error Code: 1644
Error: El ingrediente con ID "ING013" ya existe en el sistema...
```

### Prueba 4: Trigger Seguimiento - Compra Online
```sql
-- Simular compra por Web
INSERT INTO Pedidos VALUES 
('PED999', 'CLI001', 'EMP001', CURDATE(), 'Pendiente', 'Web', 1500);

-- Verificar seguimiento
SELECT * FROM SeguimientoClientesOnline 
WHERE PedidoID = 'PED999';
```

**Resultado esperado**:
- Registro en SeguimientoClientesOnline
- EstadoSeguimiento = 'PENDIENTE'
- Información completa del cliente

### Prueba 5: Trigger Seguimiento - Actualización Estado
```sql
-- Actualizar estado del pedido
UPDATE Pedidos 
SET EstadoPedido = 'Entregado' 
WHERE PedidoID = 'PED999';

-- Verificar actualización de seguimiento
SELECT EstadoSeguimiento, NotasAdicionales 
FROM SeguimientoClientesOnline 
WHERE PedidoID = 'PED999';
```

**Resultado esperado**:
- EstadoSeguimiento cambia a 'COMPLETADO'
- NotasAdicionales actualizado con registro del cambio

---

## 6. Ejecución de Aplicación Java

### Paso 1: Configurar Proyecto Java

#### Crear nuevo proyecto en IDE
- **IntelliJ IDEA**: File → New → Project → Java
- **Eclipse**: File → New → Java Project
- **NetBeans**: File → New Project → Java Application

#### Estructura del proyecto
```
ProyectoFinalJDBC/
├── src/
│   ├── DatabaseConnection.java
│   ├── Cliente.java
│   ├── ClienteDAO.java
│   └── ProyectoFinalJDBC.java
└── lib/
    └── mysql-connector-java-8.0.33.jar
```

### Paso 2: Agregar MySQL Connector

#### Opción A: Maven (pom.xml)
```xml
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>
```

#### Opción B: Gradle (build.gradle)
```gradle
dependencies {
    implementation 'mysql:mysql-connector-java:8.0.33'
}
```

#### Opción C: Manual
1. Descargar JAR desde: https://dev.mysql.com/downloads/connector/j/
2. Agregar al Build Path del proyecto

### Paso 3: Configurar Conexión

Editar `DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/ProyectoFinal";
private static final String USER = "root";  // TU USUARIO
private static final String PASSWORD = "tu_password";  // TU PASSWORD
```

### Paso 4: Copiar Código
Copiar todo el código del artifact **"Implementación JDBC - Operaciones CRUD"** en los archivos correspondientes.

### Paso 5: Ejecutar la Aplicación
```bash
# Compilar
javac -cp .:mysql-connector-java-8.0.33.jar *.java

# Ejecutar
java -cp .:mysql-connector-java-8.0.33.jar ProyectoFinalJDBC
```

**Desde IDE**: Click derecho en ProyectoFinalJDBC.java → Run

### Paso 6: Verificar Salida

**Salida esperada**:
```
===========================================
SISTEMA DE GESTIÓN - PROYECTO FINAL
===========================================

--- 1. CREATE: Insertando nuevos clientes ---
✓ Cliente insertado exitosamente: CLI001
✓ Cliente insertado exitosamente: CLI002
✓ Cliente insertado exitosamente: CLI003

--- 2. READ: Obteniendo cliente por ID ---
Cliente{ID='CLI001', Nombre='Juan Pérez', Email='juan.perez@email.com', Teléfono='8112345678'}

--- 3. READ: Obteniendo todos los clientes ---
✓ Se encontraron 13 clientes.
  Cliente{ID='CLI001', Nombre='Juan Pérez'...}
  ...

--- 4. UPDATE: Actualizando información de cliente ---
✓ Cliente actualizado exitosamente: CLI001
  Datos actualizados: Cliente{ID='CLI001', Teléfono='8111112222'...}

--- 5. BÚSQUEDA: Buscando clientes ---
✓ Se encontraron 1 clientes que coinciden con: maría
  Cliente{ID='CLI002', Nombre='María González'...}

--- 6. DELETE: Eliminando un cliente ---
✓ Cliente eliminado exitosamente: CLI003

===========================================
PROCESO COMPLETADO
===========================================
```

### Paso 7: Pruebas Personalizadas

Modificar el método `main()` para probar operaciones específicas:

```java
// Ejemplo: Buscar cliente específico
Cliente cliente = clienteDAO.obtenerClientePorID("CLI005");
if (cliente != null) {
    System.out.println("Cliente encontrado: " + cliente);
}

// Ejemplo: Listar todos y filtrar
List<Cliente> clientes = clienteDAO.obtenerTodosLosClientes();
clientes.stream()
    .filter(c -> c.getEmail().contains("gmail"))
    .forEach(System.out::println);
```

---

## 7. Solución de Problemas

### Problema 1: No se puede conectar a MySQL
**Síntoma**: `SQLException: Access denied for user 'root'@'localhost'`

**Solución**:
```sql
-- Verificar usuario y contraseña
mysql -u root -p

-- Si es necesario, crear nuevo usuario
CREATE USER 'proyecto_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON ProyectoFinal.* TO 'proyecto_user'@'localhost';
FLUSH PRIVILEGES;
```

### Problema 2: Driver JDBC no encontrado
**Síntoma**: `ClassNotFoundException: com.mysql.cj.jdbc.Driver`

**Solución**:
- Verificar que mysql-connector-java-8.0.33.jar está en el classpath
- Rebuild del proyecto
- Limpiar y recompilar: Build → Clean → Build

### Problema 3: Tablas no creadas correctamente
**Síntoma**: `Table 'ProyectoFinal.Cliente' doesn't exist`

**Solución**:
```sql
-- Verificar schema actual
SELECT DATABASE();

-- Cambiar a schema correcto
USE ProyectoFinal;

-- Verificar tablas
SHOW TABLES;

-- Si falta alguna tabla, ejecutar nuevamente el script de creación
```

### Problema 4: Foreign Key constraints fallan
**Síntoma**: `Error Code: 1452. Cannot add or update a child row`

**Solución**:
```sql
-- Verificar que existen los registros padre
SELECT * FROM Cliente WHERE ClienteID = 'CLI001';
SELECT * FROM Empleados WHERE EmpleadoID = 'EMP001';

-- Insertar datos en orden correcto:
-- 1. Empleados
-- 2. Cliente
-- 3. Marcas
-- 4. Proveedores
-- 5. Productos
-- 6. Ingredientes
-- 7. Recetas
-- 8. Pedidos
-- 9. DetallePedido
```

### Problema 5: Procedimientos no se ejecutan
**Síntoma**: `PROCEDURE ProyectoFinal.sp_VentasDiarias does not exist`

**Solución**:
```sql
-- Verificar procedimientos existentes
SHOW PROCEDURE STATUS WHERE Db = 'ProyectoFinal';

-- Si no existe, ejecutar nuevamente el script de procedimientos
-- Verificar delimitador
DELIMITER //
-- código del procedimiento
DELIMITER ;
```

### Problema 6: Triggers no se activan
**Síntoma**: Los triggers no realizan las acciones esperadas

**Solución**:
```sql
-- Verificar triggers
SHOW TRIGGERS FROM ProyectoFinal;

-- Ver definición de un trigger específico
SHOW CREATE TRIGGER trg_SeguimientoClientesOnline;

-- Eliminar trigger problemático y recrear
DROP TRIGGER IF EXISTS trg_SeguimientoClientesOnline;
-- Ejecutar nuevamente el script de creación
```

### Problema 7: Errores de charset/encoding
**Síntoma**: Caracteres extraños en texto con acentos

**Solución**:
```sql
-- Verificar charset de la BD
SHOW CREATE DATABASE ProyectoFinal;

-- Establecer charset correcto
ALTER DATABASE ProyectoFinal 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Para tablas existentes
ALTER TABLE Cliente CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Problema 8: Java heap space error
**Síntoma**: `java.lang.OutOfMemoryError: Java heap space`

**Solución**:
```bash
# Aumentar memoria heap al ejecutar
java -Xmx512m -Xms256m -cp .:mysql-connector-java-8.0.33.jar ProyectoFinalJDBC
```

---

## 🎯 Checklist Final

Antes de entregar el proyecto, verificar:

### Base de Datos
- [ ] Schema 'ProyectoFinal' creado
- [ ] 9 tablas creadas correctamente
- [ ] Relaciones (FK) establecidas
- [ ] Datos de prueba insertados (50+ registros)
- [ ] 3+ procedimientos almacenados funcionando
- [ ] 5+ triggers funcionando
- [ ] Restricción UNIQUE en Email funciona

### Consultas SQL
- [ ] Al menos 1 consulta con JOIN ejecutada
- [ ] Al menos 1 consulta con UNION ejecutada
- [ ] Al menos 1 consulta con GROUP BY ejecutada
- [ ] Al menos 1 consulta con ORDER BY ejecutada
- [ ] Consultas con manipulación de fechas funcionan

### Java JDBC
- [ ] Conexión a BD exitosa
- [ ] CREATE (insertar) funciona
- [ ] READ (leer uno) funciona
- [ ] READ (leer todos) funciona
- [ ] UPDATE (actualizar) funciona
- [ ] DELETE (eliminar) funciona
- [ ] Manejo de excepciones implementado

### Documentación
- [ ] Documentación completa del proyecto
- [ ] Instrucciones de instalación claras
- [ ] Ejemplos de uso incluidos
- [ ] Diagramas de la base de datos
- [ ] Código comentado adecuadamente

---

## 📞 Soporte Adicional

Si encuentras problemas no cubiertos en esta guía:

1. **Revisar logs de MySQL**: 
   - Windows: `C:\ProgramData\MySQL\MySQL Server 8.0\Data\*.err`
   - Linux: `/var/log/mysql/error.log`

2. **Verificar versiones**:
   ```bash
   mysql --version
   java -version
   ```

3. **Consultar documentación oficial**:
   - MySQL: https://dev.mysql.com/doc/
   - JDBC: https://docs.oracle.com/javase/tutorial/jdbc/

---

**¡Proyecto completado exitosamente! 🎉**