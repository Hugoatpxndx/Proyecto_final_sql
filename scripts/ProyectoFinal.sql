CREATE SCHEMA `ProyectoFinal` ;

CREATE TABLE Empleados (
	EmpleadoID varchar(255) primary key not null,
	Nombre varchar(40),
    Apellido varchar(40),
    FechaContratacion Date,
    Email varchar(255)
);

CREATE TABLE Cliente (
	ClienteID varchar(255) primary key not null,
    Nombre varchar(40),
    Apellido varchar(40),
    Email varchar(255),
    Telefono varchar(10),
    FechaRegistro Date,
    Direccion varchar(255)
);

CREATE TABLE Marcas (
	MarcaID varchar(255) primary key not null,
    NombreMarca varchar(255)
);

CREATE TABLE Proveedores (
	ProveedorID varchar(255) primary key not null,
    NombreEmpresa varchar(255),
    ContactoNombre varchar(255),
    ContactoTelefono varchar(10),
	Email varchar(255)
);

CREATE TABLE Pedidos (
	PedidoID varchar(255) primary key not null,
    ClienteID varchar(255),
    EmpleadoID varchar(255),
    FechaPedido Date,
    EstadoPedido varchar(255),
    PlataformaOrigen varchar(255),
    TotalPedido int
);

CREATE TABLE DetallePedido (
    PedidoID varchar(255) not null,
    ProductoID varchar(255) not null,
    Cantidad int,
    PrecioUnitarioVenta int,
    Subtotal int,
    PRIMARY KEY ( PedidoID, ProductoID)
);

CREATE TABLE Productos (
	ProductoID varchar(255) primary key not null,
    MarcaID varchar(255),
    NombreProducto varchar(255),
    Descripcion text,
    PrecioBase Decimal(10,2)
);

CREATE TABLE Ingredientes (
	IngredienteID varchar(255) primary key not null,
    ProveedorID varchar(255),
    NombreIngrediente varchar(255),
    UnidadMedida varchar(3),
    StockActual Decimal(10,2),
    CostoUnitario Decimal(10,2)
);

CREATE TABLE Recetas (
    ProductoID varchar(255) not null,
    IngredienteID varchar(255) not null,
    CantidadRequerida int,
    PRIMARY KEY (ProductoID, IngredienteID)
);

-- Pedido

ALTER TABLE `ProyectoFinal`.`Pedidos` 
ADD INDEX `EmpleadoID_idx` (`EmpleadoID` ASC) VISIBLE;
;
ALTER TABLE `ProyectoFinal`.`Pedidos` 
ADD CONSTRAINT `EmpleadoID`
  FOREIGN KEY (`EmpleadoID`)
  REFERENCES `ProyectoFinal`.`Empleados` (`EmpleadoID`)
  ON DELETE SET NULL
  ON UPDATE SET NULL;
  
ALTER TABLE `ProyectoFinal`.`Pedidos` 
ADD INDEX `ClienteID_idx` (`ClienteID` ASC) VISIBLE;
;
ALTER TABLE `ProyectoFinal`.`Pedidos` 
ADD CONSTRAINT `ClienteID`
  FOREIGN KEY (`ClienteID`)
  REFERENCES `ProyectoFinal`.`Cliente` (`ClienteID`)
  ON DELETE SET NULL
  ON UPDATE SET NULL;

-- Detalle Pedido

ALTER TABLE `ProyectoFinal`.`DetallePedido` 
ADD INDEX `PedidoID_idx` (`PedidoID` ASC) VISIBLE;
;
ALTER TABLE `ProyectoFinal`.`DetallePedido` 
ADD CONSTRAINT `PedidoID`
  FOREIGN KEY (`PedidoID`)
  REFERENCES `ProyectoFinal`.`Pedidos` (`PedidoID`);
  
ALTER TABLE `ProyectoFinal`.`DetallePedido` 
ADD INDEX `ProductoID_idx` (`ProductoID` ASC) VISIBLE;
;
ALTER TABLE `ProyectoFinal`.`DetallePedido` 
ADD CONSTRAINT `ProductoID`
  FOREIGN KEY (`ProductoID`)
  REFERENCES `ProyectoFinal`.`Productos` (`ProductoID`);
  
-- Productos
  
ALTER TABLE `ProyectoFinal`.`Productos` 
ADD INDEX `MarcaID_idx` (`MarcaID` ASC) VISIBLE;
;
ALTER TABLE `ProyectoFinal`.`Productos` 
ADD CONSTRAINT `MarcaID`
  FOREIGN KEY (`MarcaID`)
  REFERENCES `ProyectoFinal`.`Marcas` (`MarcaID`)
  ON DELETE SET NULL
  ON UPDATE SET NULL;
  
-- Ingredientes

ALTER TABLE `ProyectoFinal`.`Ingredientes` 
ADD INDEX `ProveedorID_idx` (`ProveedorID` ASC) VISIBLE;
;
ALTER TABLE `ProyectoFinal`.`Ingredientes` 
ADD CONSTRAINT `ProveedorID`
  FOREIGN KEY (`ProveedorID`)
  REFERENCES `ProyectoFinal`.`Proveedores` (`ProveedorID`)
  ON DELETE SET NULL
  ON UPDATE SET NULL;
  
-- Recetas
  
ALTER TABLE `ProyectoFinal`.`Recetas` 
ADD INDEX `ProductoID_idx` (`ProductoID` ASC) VISIBLE;
;
ALTER TABLE `ProyectoFinal`.`Recetas` 
ADD CONSTRAINT `ProductoIDfk`
  FOREIGN KEY (`ProductoID`)
  REFERENCES `ProyectoFinal`.`Productos` (`ProductoID`);
  
ALTER TABLE `ProyectoFinal`.`Recetas` 
ADD INDEX `IngredienteID_idx` (`IngredienteID` ASC) VISIBLE;
;
ALTER TABLE `ProyectoFinal`.`Recetas` 
ADD CONSTRAINT `IngredienteID`
  FOREIGN KEY (`IngredienteID`)
  REFERENCES `ProyectoFinal`.`Ingredientes` (`IngredienteID`);
  
ALTER TABLE `ProyectoFinal`.`DetallePedido`
ADD CONSTRAINT `submayor` CHECK (PrecioUnitarioVenta <= Subtotal);

ALTER TABLE `ProyectoFinal`.`Ingredientes`
ADD CONSTRAINT `menorstock` CHECK (StockActual >= 0);

ALTER TABLE `ProyectoFinal`.`Recetas`
ADD CONSTRAINT `cantmenor` CHECK (CantidadRequerida > 0);
