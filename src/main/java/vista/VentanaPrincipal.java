package vista;

import conexion.ConexionBD;
import dao.ClienteDAO;
import dao.ProductoDAO;
import modelo.Cliente;
import modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ventana principal de la aplicación.
 *
 * Se utiliza Swing porque forma parte del JDK y no requiere
 * dependencias gráficas adicionales. La ventana permite practicar
 * el CRUD JDBC viendo inmediatamente el resultado en tablas.
 */
public class VentanaPrincipal extends JFrame {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private final DefaultTableModel modeloClientes =
            new DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Email", "Activo"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final DefaultTableModel modeloProductos =
            new DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Precio", "Stock"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable tablaClientes = new JTable(modeloClientes);
    private final JTable tablaProductos = new JTable(modeloProductos);

    private final JLabel lblEstado =
            new JLabel("BBDD: sin comprobar");

    public VentanaPrincipal() {

        setTitle("UD2 - JDBC · Tienda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        construirInterfaz();
    }

    private void construirInterfaz() {

        setLayout(new BorderLayout(8, 8));

        add(crearCabecera(), BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Clientes", crearPanelClientes());
        pestanas.addTab("Productos", crearPanelProductos());

        add(pestanas, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));
        pie.add(lblEstado, BorderLayout.WEST);

        add(pie, BorderLayout.SOUTH);
    }

    private JPanel crearCabecera() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnProbar = new JButton("Probar conexión");
        JButton btnRecargar = new JButton("Recargar datos");

        btnProbar.addActionListener(e -> probarConexion());

        btnRecargar.addActionListener(e -> {
            cargarClientes();
            cargarProductos();
        });

        panel.add(new JLabel("PostgreSQL / JDBC"));
        panel.add(btnProbar);
        panel.add(btnRecargar);

        return panel;
    }

    private JPanel crearPanelClientes() {

        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar tabla");

        //btnNuevo.addActionListener(e -> nuevoCliente());
        //btnEditar.addActionListener(e -> editarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnActualizar.addActionListener(e -> cargarClientes());

        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnActualizar);

        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelProductos() {

        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar tabla");

        btnNuevo.addActionListener(e -> nuevoProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnActualizar.addActionListener(e -> cargarProductos());

        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnActualizar);

        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private void probarConexion() {


    }

    // =========================================================
    // CLIENTES
    // =========================================================

    private void cargarClientes() {


    }

    private void eliminarCliente() {

        int fila = tablaClientes.getSelectedRow();

        if (fila == -1) {
            mostrarError("Selecciona primero un cliente.");
            return;
        }

        Integer id = (Integer) modeloClientes.getValueAt(fila, 0);

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el cliente con ID " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }


    }

    // =========================================================
    // PRODUCTOS
    // =========================================================

    private void cargarProductos() {


    }

    private void nuevoProducto() {

        JTextField txtNombre = new JTextField();
        JTextField txtPrecio = new JTextField();
        JTextField txtStock = new JTextField();

        Object[] formulario = {
                "Nombre:", txtNombre,
                "Precio:", txtPrecio,
                "Stock:", txtStock
        };

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Nuevo producto",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }


    }

    private void editarProducto() {

        int fila = tablaProductos.getSelectedRow();

        if (fila == -1) {
            mostrarError("Selecciona primero un producto.");
            return;
        }

        Integer id = (Integer) modeloProductos.getValueAt(fila, 0);

        JTextField txtNombre = new JTextField(
                modeloProductos.getValueAt(fila, 1).toString()
        );

        JTextField txtPrecio = new JTextField(
                modeloProductos.getValueAt(fila, 2).toString()
        );

        JTextField txtStock = new JTextField(
                modeloProductos.getValueAt(fila, 3).toString()
        );

        Object[] formulario = {
                "Nombre:", txtNombre,
                "Precio:", txtPrecio,
                "Stock:", txtStock
        };

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Editar producto",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }


    }

    private void eliminarProducto() {

        int fila = tablaProductos.getSelectedRow();

        if (fila == -1) {
            mostrarError("Selecciona primero un producto.");
            return;
        }

        Integer id = (Integer) modeloProductos.getValueAt(fila, 0);

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el producto con ID " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }


    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
