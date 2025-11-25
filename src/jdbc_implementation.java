/**
 * =====================================================
 * IMPLEMENTACIÓN JDBC - PROYECTO FINAL
 * Sistema de Gestión de Base de Datos
 * =====================================================
 */

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
 * Modelo de Cliente
 */
class Cliente {
    private String clienteID;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDate fechaRegistro;
    private String direccion;
    
    // Constructor
    public Cliente(String clienteID, String nombre, String apellido, 
                   String email, String telefono, String direccion) {
        this.clienteID = clienteID;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.fechaRegistro = LocalDate.now();
        this.direccion = direccion;
    }
    
    // Constructor para lectura desde BD
    public Cliente(String clienteID, String nombre, String apellido, 
                   String email, String telefono, LocalDate fechaRegistro, String direccion) {
        this.clienteID = clienteID;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
        this.direccion = direccion;
    }
    
    // Getters y Setters
    public String getClienteID() { return clienteID; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public String getDireccion() { return direccion; }
    
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    @Override
    public String toString() {
        return String.format("Cliente{ID='%s', Nombre='%s %s', Email='%s', Teléfono='%s'}",
                clienteID, nombre, apellido, email, telefono);
    }
}

/**
 * DAO (Data Access Object) para Cliente
 * Implementa las operaciones CRUD
 */
class ClienteDAO {
    
    /**
     * CREATE - Insertar un nuevo cliente
     */
    public boolean insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO Cliente (ClienteID, Nombre, Apellido, Email, Telefono, FechaRegistro, Direccion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, cliente.getClienteID());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getApellido());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setDate(6, Date.valueOf(cliente.getFechaRegistro()));
            pstmt.setString(7, cliente.getDireccion());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Cliente insertado exitosamente: " + cliente.getClienteID());
                return true;
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("✗ Error: El email ya está registrado o el ID ya existe.");
            System.err.println("  Detalles: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Error al insertar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * READ - Obtener un cliente por ID
     */
    public Cliente obtenerClientePorID(String clienteID) {
        String sql = "SELECT * FROM Cliente WHERE ClienteID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, clienteID);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Cliente(
                    rs.getString("ClienteID"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Email"),
                    rs.getString("Telefono"),
                    rs.getDate("FechaRegistro").toLocalDate(),
                    rs.getString("Direccion")
                );
            } else {
                System.out.println("✗ Cliente no encontrado con ID: " + clienteID);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * READ - Obtener todos los clientes
     */
    public List<Cliente> obtenerTodosLosClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente ORDER BY FechaRegistro DESC";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getString("ClienteID"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Email"),
                    rs.getString("Telefono"),
                    rs.getDate("FechaRegistro").toLocalDate(),
                    rs.getString("Direccion")
                );
                clientes.add(cliente);
            }
            
            System.out.println("✓ Se encontraron " + clientes.size() + " clientes.");
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener clientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, stmt, rs);
        }
        
        return clientes;
    }
    
    /**
     * UPDATE - Actualizar información de un cliente
     */
    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE Cliente SET Nombre = ?, Apellido = ?, Email = ?, " +
                     "Telefono = ?, Direccion = ? WHERE ClienteID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getDireccion());
            pstmt.setString(6, cliente.getClienteID());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Cliente actualizado exitosamente: " + cliente.getClienteID());
                return true;
            } else {
                System.out.println("✗ No se encontró el cliente con ID: " + cliente.getClienteID());
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * DELETE - Eliminar un cliente
     */
    public boolean eliminarCliente(String clienteID) {
        String sql = "DELETE FROM Cliente WHERE ClienteID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, clienteID);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Cliente eliminado exitosamente: " + clienteID);
                return true;
            } else {
                System.out.println("✗ No se encontró el cliente con ID: " + clienteID);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al eliminar cliente: " + e.getMessage());
            System.err.println("  Puede que el cliente tenga pedidos asociados.");
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Buscar clientes por nombre o email
     */
    public List<Cliente> buscarClientes(String termino) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente WHERE " +
                     "Nombre LIKE ? OR Apellido LIKE ? OR Email LIKE ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            String busqueda = "%" + termino + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getString("ClienteID"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("Email"),
                    rs.getString("Telefono"),
                    rs.getDate("FechaRegistro").toLocalDate(),
                    rs.getString("Direccion")
                );
                clientes.add(cliente);
            }
            
            System.out.println("✓ Se encontraron " + clientes.size() + 
                             " clientes que coinciden con: " + termino);
            
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar clientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        
        return clientes;
    }
}

/**
 * Clase principal con ejemplos de uso
 */
public class ProyectoFinalJDBC {
    
    public static void main(String[] args) {
        ClienteDAO clienteDAO = new ClienteDAO();
        
        System.out.println("===========================================");
        System.out.println("SISTEMA DE GESTIÓN - PROYECTO FINAL");
        System.out.println("===========================================\n");
        
        // ========== CREATE - Insertar nuevos clientes ==========
        System.out.println("--- 1. CREATE: Insertando nuevos clientes ---");
        
        Cliente cliente1 = new Cliente(
            "CLI001", "Juan", "Pérez", 
            "juan.perez@email.com", "8112345678", 
            "Av. Constitución #123, Monterrey"
        );
        clienteDAO.insertarCliente(cliente1);
        
        Cliente cliente2 = new Cliente(
            "CLI002", "María", "González", 
            "maria.gonzalez@email.com", "8187654321", 
            "Calle Morelos #456, San Pedro"
        );
        clienteDAO.insertarCliente(cliente2);
        
        Cliente cliente3 = new Cliente(
            "CLI003", "Carlos", "Ramírez", 
            "carlos.ramirez@email.com", "8199887766", 
            "Blvd. Díaz Ordaz #789, Santa Catarina"
        );
        clienteDAO.insertarCliente(cliente3);
        
        System.out.println();
        
        // ========== READ - Leer un cliente específico ==========
        System.out.println("--- 2. READ: Obteniendo cliente por ID ---");
        Cliente clienteEncontrado = clienteDAO.obtenerClientePorID("CLI001");
        if (clienteEncontrado != null) {
            System.out.println(clienteEncontrado);
        }
        System.out.println();
        
        // ========== READ - Leer todos los clientes ==========
        System.out.println("--- 3. READ: Obteniendo todos los clientes ---");
        List<Cliente> todosLosClientes = clienteDAO.obtenerTodosLosClientes();
        for (Cliente c : todosLosClientes) {
            System.out.println("  " + c);
        }
        System.out.println();
        
        // ========== UPDATE - Actualizar un cliente ==========
        System.out.println("--- 4. UPDATE: Actualizando información de cliente ---");
        if (clienteEncontrado != null) {
            clienteEncontrado.setTelefono("8111112222");
            clienteEncontrado.setDireccion("Nueva dirección actualizada #999");
            clienteDAO.actualizarCliente(clienteEncontrado);
            
            // Verificar actualización
            Cliente clienteActualizado = clienteDAO.obtenerClientePorID("CLI001");
            System.out.println("  Datos actualizados: " + clienteActualizado);
        }
        System.out.println();
        
        // ========== BÚSQUEDA - Buscar clientes ==========
        System.out.println("--- 5. BÚSQUEDA: Buscando clientes ---");
        List<Cliente> resultados = clienteDAO.buscarClientes("maría");
        for (Cliente c : resultados) {
            System.out.println("  " + c);
        }
        System.out.println();
        
        // ========== DELETE - Eliminar un cliente ==========
        System.out.println("--- 6. DELETE: Eliminando un cliente ---");
        clienteDAO.eliminarCliente("CLI003");
        
        // Verificar eliminación
        System.out.println("\nVerificando clientes restantes:");
        List<Cliente> clientesRestantes = clienteDAO.obtenerTodosLosClientes();
        for (Cliente c : clientesRestantes) {
            System.out.println("  " + c);
        }
        
        System.out.println("\n===========================================");
        System.out.println("PROCESO COMPLETADO");
        System.out.println("===========================================");
    }
}