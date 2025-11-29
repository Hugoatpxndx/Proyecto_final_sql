/**
 * MenuPrincipal.java
 * Clase principal con el menú del sistema
 */

import java.util.Scanner;

public class MenuPrincipal {
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        // Mostrar bienvenida y probar conexión
        Utilidades.mostrarBienvenida();
        
        boolean continuar = true;
        
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    menuConsultasAvanzadas();
                    break;
                case 2:
                    Procedimientos.ejecutarVentasDiarias(scanner);
                    Utilidades.pausar(scanner);
                    break;
                case 3:
                    Procedimientos.ejecutarClientesVigentesQ1(scanner);
                    Utilidades.pausar(scanner);
                    break;
                case 4:
                    Procedimientos.ejecutarInsertarCliente(scanner);
                    Utilidades.pausar(scanner);
                    break;
                case 5:
                    menuTriggers();
                    break;
                case 6:
                    OperacionesCRUD.eliminarOtrosRegistros(scanner);
                    Utilidades.pausar(scanner);
                    break;
                case 7:
                    menuCRUD();
                    break;
                case 8:
                    Utilidades.mostrarEstadisticas();
                    Utilidades.pausar(scanner);
                    break;
                case 0:
                    System.out.println("\n╔════════════════════════════════════════════════════════╗");
                    System.out.println("║          ¡Gracias por usar el sistema!               ║");
                    System.out.println("║                  Hasta pronto                        ║");
                    System.out.println("╚════════════════════════════════════════════════════════╝\n");
                    continuar = false;
                    break;
                default:
                    System.out.println("\n Opción inválida. Por favor, intente nuevamente.");
                    Utilidades.pausar(scanner);
            }
        }
        
        scanner.close();
    }
    
    /**
     * Muestra el menú principal del sistema
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              MENÚ PRINCIPAL                          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("\n OPCIONES DISPONIBLES:");
        System.out.println("\n  1. Consultas Avanzadas SQL (Punto 1)");
        System.out.println("  2. Reporte de Ventas Diarias (Punto 2)");
        System.out.println("  3. Reporte Clientes Vigentes Q1 (Punto 3)");
        System.out.println("  4. Insertar Cliente con Validación (Punto 4)");
        System.out.println("  5. Probar Triggers (Puntos 5 y 6)");
        System.out.println("  6. Eliminar Registro");
        System.out.println("  7. Operaciones CRUD");
        System.out.println("  8. Ver Estadísticas del Sistema");
        System.out.println("  0. Salir del Sistema");
        System.out.print("\n▶ Seleccione una opción [0-8]: ");
    }
    
    /**
     * Lee una opción del menú
     * @return Número de opción seleccionada
     */
    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Menú de consultas avanzadas SQL (Punto 1)
     */
    private static void menuConsultasAvanzadas() {
        boolean volver = false;
        
        while (!volver) {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║        CONSULTAS AVANZADAS SQL (Punto 1)             ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println("\n  1. JOIN - Pedidos con clientes y empleados");
            System.out.println("  2. UNION - Lista de contactos unificada");
            System.out.println("  3. GROUP BY - Ventas totales por cliente");
            System.out.println("  4. ORDER BY - Pedidos más recientes");
            System.out.println("  5. Fechas - Análisis trimestral");
            System.out.println("  6. JOIN Múltiple - Detalle completo de pedidos");
            System.out.println("  7. Productos más vendidos");
            System.out.println("  8. Ventas por plataforma");
            System.out.println("  0. ⬅ Volver al menú principal");
            System.out.print("\n▶ Seleccione consulta [0-8]: ");
            
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    ConsultasAvanzadas.consultaJoinPedidos();
                    break;
                case 2:
                    ConsultasAvanzadas.consultaUnionContactos();
                    break;
                case 3:
                    ConsultasAvanzadas.consultaGroupByVentas();
                    break;
                case 4:
                    ConsultasAvanzadas.consultaOrderByPedidos();
                    break;
                case 5:
                    ConsultasAvanzadas.consultaAnalisisTrimestral();
                    break;
                case 6:
                    ConsultasAvanzadas.consultaJoinMultiple();
                    break;
                case 7:
                    ConsultasAvanzadas.consultaProductosMasVendidos();
                    break;
                case 8:
                    ConsultasAvanzadas.consultaVentasPorPlataforma();
                    break;
                case 0:
                    volver = true;
                    continue;
                default:
                    System.out.println("\n Opción inválida.");
            }
            
            if (!volver) {
                Utilidades.pausar(scanner);
            }
        }
    }
    
    /**
     * Menú de triggers (Puntos 5 y 6)
     */
    private static void menuTriggers() {
        boolean volver = false;
        
        while (!volver) {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║           PROBAR TRIGGERS (Puntos 5 y 6)            ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println("\n  PUNTO 5 - Control de Inventario:");
            System.out.println("    1. Insertar ingrediente (activa trigger)");
            System.out.println("    2. Actualizar stock (activa trigger y alerta)");
            System.out.println("    3. Ver Log de Inventario");
            System.out.println("\n  PUNTO 6 - Seguimiento de Clientes:");
            System.out.println("    4. Insertar pedido online (activa trigger)");
            System.out.println("    5. Ver Seguimiento de Clientes Online");
            System.out.println("\n  0. Volver al menú principal");
            System.out.print("\n▶ Seleccione opción [0-5]: ");
            
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    Triggers.insertarIngrediente(scanner);
                    break;
                case 2:
                    Triggers.actualizarStockIngrediente(scanner);
                    break;
                case 3:
                    Triggers.verLogInventario();
                    break;
                case 4:
                    Triggers.insertarPedidoOnline(scanner);
                    break;
                case 5:
                    Triggers.verSeguimientoClientesOnline();
                    break;
                case 0:
                    volver = true;
                    continue;
                default:
                    System.out.println("\n Opción inválida.");
            }
            
            if (!volver) {
                Utilidades.pausar(scanner);
            }
        }
    }
    
    /**
     * Menú de operaciones CRUD
     */
    private static void menuCRUD() {
        boolean volver = false;
        
        while (!volver) {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║            OPERACIONES CRUD                          ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println("\n  1. CREATE - Crear nuevo producto");
            System.out.println("  2. READ - Leer/Consultar productos");
            System.out.println("  3. UPDATE - Actualizar producto");
            System.out.println("  4. DELETE - Eliminar producto");
            System.out.println("  0. Volver al menú principal");
            System.out.print("\n▶ Seleccione operación [0-4]: ");
            
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    OperacionesCRUD.crearProducto(scanner);
                    break;
                case 2:
                    OperacionesCRUD.leerProductos(scanner);
                    break;
                case 3:
                    OperacionesCRUD.actualizarProducto(scanner);
                    break;
                case 4:
                    OperacionesCRUD.eliminarProducto(scanner);
                    break;
                case 0:
                    volver = true;
                    continue;
                default:
                    System.out.println("\n Opción inválida.");
            }
            
            if (!volver) {
                Utilidades.pausar(scanner);
            }
        }
    }
}