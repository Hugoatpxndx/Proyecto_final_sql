# Documentación del Proyecto Final - Sistema de Gestión de Base de Datos

## Índice
1. [Introducción](#introducción)
2. [Diseño Conceptual](#diseño-conceptual)
3. [Diseño Lógico](#diseño-lógico)
4. [Implementación SQL](#implementación-sql)
5. [Procedimientos Almacenados](#procedimientos-almacenados)
6. [Triggers](#triggers)
7. [Implementación JDBC](#implementación-jdbc)
8. [Guía de Instalación](#guía-de-instalación)
9. [Pruebas y Casos de Uso](#pruebas-y-casos-de-uso)

---

## Introducción

### Descripción del Sistema
Sistema de gestión integral para una empresa de productos alimenticios que permite administrar clientes, productos, inventarios, pedidos y generar reportes analíticos.

### Objetivos
- Implementar operaciones CRUD completas
- Utilizar consultas SQL avanzadas
- Crear procedimientos almacenados para reportes
- Implementar triggers para control automático
- Desarrollar interfaz Java con JDBC

### Tecnologías Utilizadas
- **Base de Datos**: MySQL 8.0+
- **Lenguaje**: SQL, PL/SQL, Java
- **Conectividad**: JDBC (MySQL Connector)
- **IDE Recomendado**: IntelliJ IDEA, Eclipse, NetBeans

---

## Diseño Conceptual

### Modelo Entidad-Relación

#### Entidades Principales
1. **Cliente**: Almacena información de los clientes
2. **Empleados**: Registro de empleados que atienden pedidos
3. **Productos**: Catálogo de productos disponibles
4. **Marcas**: Marcas de los productos
5. **Pedidos**: Órdenes de compra realizadas
6. **DetallePedido**: Productos específicos de cada pedido
7. **Ingredientes**: Materias primas para productos
8. **Proveedores**: Proveedores de ingredientes
9. **Recetas**: Relación entre productos e ingredientes

#### Relaciones
- Cliente (1) → (N) Pedidos
- Empleados (1) → (N) Pedidos
- Pedidos (1) → (N) DetallePedido
- Productos (1) → (N) DetallePedido
- Marcas (1) → (N) Productos
- Proveedores (1) → (N) Ingredientes
- Productos (N) → (M) Ingredientes (a través de Recetas)

---

## Diseño Lógico

### Estructura de Tablas

#### Tabla: Cliente
```
ClienteID (PK) VARCHAR(255)
Nombre VARCHAR(40)
Apellido VARCHAR(40)
Email VARCHAR(255) UNIQUE
Telefono VARCHAR(10)
FechaRegistro DATE
Direccion VARCHAR(255)
```

#### Tabla: Empleados
```
EmpleadoID (PK) VARCHAR(255)
Nombre VARCHAR(40)
Apellido VARCHAR(40)
FechaContratacion DATE
Email VARCHAR(255)
```

#### Tabla: Productos
```
ProductoID (PK) VARCHAR(255)
MarcaID (FK) VARCHAR(255)
NombreProducto VARCHAR(255)
Descripcion TEXT
PrecioBase DECIMAL(10,2)
```

#### Tabla: Pedidos
```
PedidoID (PK) VARCHAR(255)
ClienteID (FK) VARCHAR(255)
EmpleadoID (FK) VARCHAR(255)
FechaPedido DATE
EstadoPedido VARCHAR(255)
PlataformaOrigen VARCHAR(255)
TotalPedido INT
```

### Restricciones de Integridad
- **Claves Primarias**: Todas las tablas tienen PK definidas
- **Claves Foráneas**: Relaciones con ON DELETE/UPDATE SET NULL
- **Restricción UNIQUE**: Email en tabla Cliente
- **Validaciones**: Stock no negativo, precios positivos

---

## Implementación SQL

### 1. Consultas Avanzadas Implementadas

#### JOIN
```sql
-- Pedidos con información completa de clientes y empleados
SELECT p.PedidoID, p.FechaPedido, 
       CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente,
       e.Nombre AS Empleado, p.TotalPedido
FROM Pedidos p
INNER JOIN Cliente c ON p.ClienteID = c.ClienteID
INNER JOIN Empleados e ON p.EmpleadoID = e.EmpleadoID;
```

#### UNION
```sql
-- Lista unificada de contactos (Clientes y Proveedores)
SELECT 'Cliente' AS Tipo, CONCAT(Nombre, ' ', Apellido) AS Nombre, Email
FROM Cliente
UNION
SELECT 'Proveedor', ContactoNombre, Email
FROM Proveedores;
```

#### GROUP BY
```sql
-- Ventas totales por cliente
SELECT c.ClienteID, CONCAT(c.Nombre, ' ', c.Apellido) AS Cliente,
       COUNT(p.PedidoID) AS TotalPedidos,
       SUM(p.TotalPedido) AS MontoTotal
FROM Cliente c
LEFT JOIN Pedidos p ON c.ClienteID = p.ClienteID
GROUP BY c.ClienteID
HAVING COUNT(p.PedidoID) > 0;
```

#### ORDER BY
```sql
-- Pedidos más recientes
SELECT PedidoID, FechaPedido, TotalPedido
FROM Pedidos
ORDER BY FechaPedido DESC, TotalPedido DESC;
```

### 2. Manipulación de Fechas
```sql
-- Análisis por trimestre
SELECT YEAR(FechaPedido) AS Año,
       QUARTER(FechaPedido) AS Trimestre,
       COUNT(*) AS TotalPedidos
FROM Pedidos
GROUP BY YEAR(FechaPedido), QUARTER(FechaPedido);
```

---

## Procedimientos Almacenados

### 1. sp_VentasDiarias
**Propósito**: Calcular ventas de una fecha específica

**Parámetros**:
- `fecha_consulta` (DATE): Fecha a consultar

**Resultados**:
- Detalle de pedidos del día
- Resumen con totales y promedios
- Distribución por plataforma

**Ejemplo de uso**:
```sql
CALL sp_VentasDiarias('2024-11-20');
```

### 2. sp_ClientesVigentesQ1
**Propósito**: Reporte de clientes activos en el primer trimestre

**Parámetros**:
- `año_consulta` (INT): Año a analizar

**Resultados**:
- Lista de clientes con actividad en Q1
- Estadísticas de compra por cliente
- Resumen ejecutivo del trimestre

**Ejemplo de uso**:
```sql
CALL sp_ClientesVigentesQ1(2024);
```

### 3. sp_InsertarCliente
**Propósito**: Insertar cliente con validación de email único

**Características**:
- Validación de campos obligatorios
- Manejo de excepciones (TRY/CATCH)
- Mensajes descriptivos de error
- Verificación de email duplicado

**Ejemplo de uso**:
```sql
CALL sp_InsertarCliente('CLI011', 'Pedro', 'Gómez', 
                        'pedro@email.com', '8112223344', 'Dirección 123');
```

---

## Triggers

### 1. Control de Inventario

#### trg_ValidarIngredienteAntes_Insert
**Tipo**: BEFORE INSERT
**Propósito**: Validar datos antes de insertar ingredientes
**Validaciones**:
- Ingrediente no duplicado
- Stock no negativo
- Costo unitario positivo

#### trg_RegistrarInventario_Insert
**Tipo**: AFTER INSERT
**Propósito**: Registrar nuevos ingredientes en log

#### trg_ValidarInventario_Update
**Tipo**: BEFORE UPDATE
**Propósito**: Validar actualizaciones de stock
**Alertas**: Stock bajo (< 10 unidades)

#### trg_RegistrarInventario_Update
**Tipo**: AFTER UPDATE
**Propósito**: Auditoría de cambios en inventario

**Tabla de Soporte**: LogInventario
```sql
CREATE TABLE LogInventario (
    LogID INT AUTO_INCREMENT PRIMARY KEY,
    IngredienteID VARCHAR(255),
    Accion VARCHAR(50),
    StockAnterior DECIMAL(10,2),
    StockNuevo DECIMAL(10,2),
    Usuario VARCHAR(100),
    FechaHora DATETIME,
    Mensaje TEXT
);
```

### 2. Seguimiento de Clientes Online

#### trg_SeguimientoClientesOnline
**Tipo**: AFTER INSERT en Pedidos
**Propósito**: Registrar compras realizadas por web o app
**Condición**: PlataformaOrigen IN ('Web', 'App Móvil', 'App', 'Online')

**Tabla de Soporte**: SeguimientoClientesOnline
```sql
CREATE TABLE SeguimientoClientesOnline (
    SeguimientoID INT AUTO_INCREMENT PRIMARY KEY,
    ClienteID VARCHAR(255),
    NombreCliente VARCHAR(100),
    EmailCliente VARCHAR(255),
    PedidoID VARCHAR(255),
    PlataformaOrigen VARCHAR(255),
    FechaHoraCompra DATETIME,
    EstadoSeguimiento VARCHAR(50)
);
```

#### trg_ActualizarSeguimiento_EstadoPedido
**Tipo**: AFTER UPDATE en Pedidos
**Propósito**: Actualizar estado de seguimiento cuando cambia estado del pedido

---

## Implementación JDBC

### Arquitectura de la Aplicación

```
ProyectoFinalJDBC/
│
├── DatabaseConnection.java     (Conexión a BD)
├── Cliente.java                (Modelo de datos)
├── ClienteDAO.java             (Operaciones CRUD)
└── ProyectoFinalJDBC.java     (Clase principal)
```

### Operaciones CRUD Implementadas

#### CREATE - Insertar Cliente
```java
public boolean insertarCliente(Cliente cliente) {
    String sql = "INSERT INTO Cliente (...) VALUES (?, ?, ?, ...)";
    // Implementación con PreparedStatement
}
```

#### READ - Obtener Cliente por ID
```java
public Cliente obtenerClientePorID(String clienteID) {
    String sql = "SELECT * FROM Cliente WHERE ClienteID = ?";
    // Retorna objeto Cliente
}
```

#### READ - Obtener Todos los Clientes
```java
public List<Cliente> obtenerTodosLosClientes() {
    // Retorna lista de todos los clientes
}
```

#### UPDATE - Actualizar Cliente
```java
public boolean actualizarCliente(Cliente cliente) {
    String sql = "UPDATE Cliente SET ... WHERE ClienteID = ?";
    // Actualiza información del cliente
}
```

#### DELETE - Eliminar Cliente
```java
public boolean eliminarCliente(String clienteID) {
    String sql = "DELETE FROM Cliente WHERE ClienteID = ?";
    // Elimina cliente de la BD
}
```

### Características Adicionales
- **Búsqueda**: Método para buscar clientes por nombre o email
- **Manejo de Excepciones**: Try-catch en todas las operaciones
- **Gestión de Recursos**: Cierre automático de conexiones
- **Mensajes Informativos**: Feedback claro en consola

---

## Guía de Instalación

### Requisitos Previos
1. MySQL Server 8.0 o superior instalado
2. JDK 11 o superior
3. MySQL Connector/J (Driver JDBC)
4. IDE Java (opcional pero recomendado)

### Pasos de Instalación

#### 1. Crear la Base de Datos
```sql
-- Ejecutar: ProyectoFinal-1.sql
CREATE SCHEMA ProyectoFinal;
USE ProyectoFinal;
-- Ejecutar todas las tablas y relaciones
```

#### 2. Insertar Datos de Prueba
```sql
-- Ejecutar: data_insertion.sql
-- Inserta empleados, clientes, productos, etc.
```

#### 3. Crear Procedimientos y Triggers
```sql
-- Ejecutar: stored_procedures.sql
-- Ejecutar: triggers.sql
```

#### 4. Configurar Proyecto Java

**Agregar MySQL Connector al proyecto**:

Maven (pom.xml):
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

Gradle (build.gradle):
```gradle
implementation 'mysql:mysql-connector-java:8.0.33'
```

Manual: Descargar JAR y agregar a Build Path

#### 5. Configurar Conexión
Editar en `DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/ProyectoFinal";
private static final String USER = "tu_usuario";
private static final String PASSWORD = "tu_contraseña";
```

### Verificación de Instalación

```sql
-- Verificar tablas creadas
SHOW TABLES;

-- Verificar procedimientos
SHOW PROCEDURE STATUS WHERE Db = 'ProyectoFinal';

-- Verificar triggers
SHOW TRIGGERS;

-- Verificar datos
SELECT COUNT(*) FROM Cliente;
```

---

## Pruebas y Casos de Uso

### Caso de Uso 1: Consulta de Ventas Diarias
```sql
-- Ejecutar reporte de ventas del 24 de noviembre
CALL sp_VentasDiarias('2024-11-24');

-- Resultado esperado:
-- 1. Lista de pedidos del día
-- 2. Total de ventas: $3,545
-- 3. Promedio de venta
-- 4. Distribución por plataforma
```

### Caso de Uso 2: Reporte Trimestral de Clientes
```sql
-- Clientes activos en Q1 2024
CALL sp_ClientesVigentesQ1(2024);

-- Resultado esperado:
-- Clientes: CLI001, CLI002, CLI003, CLI004, CLI005, CLI006, CLI007
-- Total pedidos: 10
-- Venta total Q1: $8,950
```

### Caso de Uso 3: Inserción con Validación
```sql
-- Intento 1: Inserción exitosa
CALL sp_InsertarCliente('CLI020', 'Ana', 'López', 
                        'ana.lopez@email.com', '8155551111', 'Dirección 555');
-- ✓ Resultado: Cliente insertado exitosamente

-- Intento 2: Email duplicado
CALL sp_InsertarCliente('CLI021', 'Pedro', 'Martín', 
                        'ana.lopez@email.com', '8166662222', 'Dirección 666');
-- ✗ Resultado: ERROR - Email ya registrado
```

### Caso de Uso 4: Control de Inventario
```sql
-- Insertar nuevo ingrediente
INSERT INTO Ingredientes VALUES 
('ING013', 'PRV001', 'Canela Molida', 'kg', 5.5, 120.00);
-- ✓ Trigger activa: Registro en LogInventario

-- Actualizar stock (llevar a nivel bajo)
UPDATE Ingredientes SET StockActual = 8 WHERE IngredienteID = 'ING013';
-- ✓ Trigger activa: Alerta de stock bajo

-- Ver log de inventario
SELECT * FROM LogInventario ORDER BY FechaHora DESC LIMIT 5;
```

### Caso de Uso 5: Seguimiento de Compras Online
```sql
-- Simular nueva compra por Web
INSERT INTO Pedidos VALUES 
('PED025', 'CLI005', 'EMP001', '2024-11-24', 'Pendiente', 'Web', 890);
-- ✓ Trigger activa: Registro en SeguimientoClientesOnline

-- Ver seguimientos pendientes
SELECT * FROM SeguimientoClientesOnline 
WHERE EstadoSeguimiento = 'PENDIENTE'
ORDER BY FechaHoraCompra DESC;
```

### Caso de Uso 6: Operaciones CRUD desde Java
```java
// CREATE
Cliente nuevoCliente = new Cliente("CLI030", "Laura", "Díaz", 
                                   "laura.diaz@email.com", "8177778888", "Av. Principal 100");
clienteDAO.insertarCliente(nuevoCliente);

// READ
Cliente cliente = clienteDAO.obtenerClientePorID("CLI030");
System.out.println(cliente);

// UPDATE
cliente.setTelefono("8188889999");
clienteDAO.actualizarCliente(cliente);

// DELETE
clienteDAO.eliminarCliente("CLI030");
```

### Pruebas de Consultas Avanzadas

#### Prueba JOIN
```sql
-- Obtener detalle completo de pedidos
SELECT p.PedidoID, c.Nombre AS Cliente, pr.NombreProducto, dp.Cantidad
FROM Pedidos p
JOIN Cliente c ON p.ClienteID = c.ClienteID
JOIN DetallePedido dp ON p.PedidoID = dp.PedidoID
JOIN Productos pr ON dp.ProductoID = pr.ProductoID
WHERE p.FechaPedido = '2024-11-24';
```

#### Prueba GROUP BY
```sql
-- Top 5 productos más vendidos
SELECT pr.NombreProducto, 
       SUM(dp.Cantidad) AS TotalVendido,
       SUM(dp.Subtotal) AS Ingresos
FROM DetallePedido dp
JOIN Productos pr ON dp.ProductoID = pr.ProductoID
GROUP BY pr.ProductoID
ORDER BY TotalVendido DESC
LIMIT 5;
```

### Resultados Esperados

| Consulta | Tiempo Esperado | Estado |
|----------|----------------|--------|
| Ventas Diarias | < 1 segundo | ✓ Óptimo |
| Reporte Q1 | < 2 segundos | ✓ Óptimo |
| INSERT con validación | < 1 segundo | ✓ Óptimo |
| Activación de Triggers | Instantáneo | ✓ Óptimo |
| Operaciones JDBC | < 1 segundo | ✓ Óptimo |

---

## Conclusiones

### Funcionalidades Implementadas
✅ Base de datos relacional completa con 9 tablas
✅ 10+ consultas SQL avanzadas (JOIN, UNION, GROUP BY, ORDER BY)
✅ 3 procedimientos almacenados con lógica de negocio
✅ 1 procedimiento con manejo de excepciones (TRY/CATCH)
✅ 5 triggers para control automático de inventario y seguimiento
✅ Implementación JDBC completa con operaciones CRUD
✅ Interfaz Java funcional para gestión de clientes

### Características Destacadas
- **Integridad Referencial**: Todas las relaciones definidas con FK
- **Validaciones**: Restricciones UNIQUE, NOT NULL, validaciones de negocio
- **Auditoría**: Tablas de log para seguimiento de cambios
- **Manejo de Errores**: Excepciones controladas en SQL y Java
- **Escalabilidad**: Diseño preparado para crecimiento

### Mejoras Futuras
- Implementar autenticación y autorización
- Crear interfaz gráfica (GUI) con JavaFX o Swing
- Agregar más reportes analíticos
- Implementar sistema de notificaciones
- Crear API REST para integración con otras aplicaciones

---

## Apéndices

### Apéndice A: Diccionario de Datos Completo
[Ver estructura detallada de cada tabla con tipos de datos, restricciones y descripciones]

### Apéndice B: Diagramas
- Diagrama Entidad-Relación (ERD)
- Diagrama de Clases Java
- Diagrama de Flujo de Procesos

### Apéndice C: Scripts Completos
- Script de creación de BD
- Script de datos de prueba
- Script de procedimientos
- Script de triggers

### Apéndice D: Referencias
- Documentación MySQL: https://dev.mysql.com/doc/
- JDBC Tutorial: https://docs.oracle.com/javase/tutorial/jdbc/
- SQL Standards: ISO/IEC 9075

---

**Proyecto Final - Sistema de Gestión de Base de Datos**  
**Fecha**: Noviembre 2024  
**Versión**: 1.0