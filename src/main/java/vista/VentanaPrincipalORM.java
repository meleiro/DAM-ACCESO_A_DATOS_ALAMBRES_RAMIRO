package vista;

import conexion.JpaUtil;
import jakarta.persistence.EntityManager;
import dao.orm.ClienteDAOOrm;
import dao.orm.ProductoDAOOrm;
import modelo.orm.ClienteEntidad;
import modelo.orm.ProductoEntidad;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Ventana principal de la aplicación.
 *
 * Se utiliza Swing porque forma parte del JDK y no requiere
 * dependencias gráficas adicionales. La ventana permite practicar
 * el CRUD JDBC viendo inmediatamente el resultado en tablas.
 */
public class VentanaPrincipalORM extends JFrame {

    private final ClienteDAOOrm clienteDAO = new ClienteDAOOrm();
    private final ProductoDAOOrm productoDAO = new ProductoDAOOrm();

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

    public VentanaPrincipalORM() {

        setTitle("UD3 - JPA / Hibernate · Tienda");
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
            //cargarProductos();
        });

        panel.add(new JLabel("PostgreSQL / Hibernate ORM"));
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

        btnNuevo.addActionListener(e -> nuevoCliente());
        btnEditar.addActionListener(e -> editarCliente());
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

       /* btnNuevo.addActionListener(e -> nuevoProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnActualizar.addActionListener(e -> cargarProductos());*/

        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnActualizar);

        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private void probarConexion() {

        try (EntityManager em = JpaUtil.abrir()){

            lblEstado.setText("BBDD ORM: " + (em.isOpen() ? "conexión preparada" : "cerrada"));

            JOptionPane.showMessageDialog(
                    this,
                    "Conexión realizada correctamente",
                    "JPA / Hibernate",
                    JOptionPane.INFORMATION_MESSAGE
            );


        } catch (RuntimeException ex){
            lblEstado.setText("BBDDD: error");

        }

        cargarClientes();

    }

    // =========================================================
    // CLIENTES
    // =========================================================

    private void cargarClientes() {

        try {
            modeloClientes.setRowCount(0);

            for (ClienteEntidad c : clienteDAO.listar()) {
                modeloClientes.addRow(
                        new Object[]{
                                c.getId(),
                                c.getNombre(),
                                c.getEmail(),
                                c.isActivo()
                        }

                );
            }
        } catch (RuntimeException e) {
            mostrarError("Error consultando clientes: \n" + e.getMessage());
        }

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

        try {
            clienteDAO.eliminar(id);
            cargarClientes();
        } catch (RuntimeException e){
            mostrarError("Erro eliminando cliente : \n" + e.getMessage());
        }

    }

    // =========================================================
    // PRODUCTOS
    // =========================================================

   /* private void cargarProductos() {
        try {
            modeloProductos.setRowCount(0);
            for (ProductoEntidad p : productoDAO.listar()) {
                modeloProductos.addRow(new Object[]{p.getId(),p.getNombre(),p.getPrecio(),p.getStock()});
            }
        } catch (RuntimeException e) {
            mostrarError("Error consultando productos: " + e.getMessage());
        }
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
        try {
            ProductoEntidad p = new ProductoEntidad(null, txtNombre.getText().trim(),
                    new BigDecimal(txtPrecio.getText().trim()), Integer.parseInt(txtStock.getText().trim()));
            productoDAO.insertar(p);
            cargarProductos();
        } catch (RuntimeException ex) {
            mostrarError("Error insertando producto: " + ex.getMessage());
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
        try {
            ProductoEntidad p = new ProductoEntidad(id, txtNombre.getText().trim(),
                    new BigDecimal(txtPrecio.getText().trim()), Integer.parseInt(txtStock.getText().trim()));
            productoDAO.actualizar(p);
            cargarProductos();
        } catch (RuntimeException ex) {
            mostrarError("Error actualizando producto: " + ex.getMessage());
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
        try {
            productoDAO.eliminar(id);
            cargarProductos();
        } catch (RuntimeException ex) {
            mostrarError("Error eliminando producto: " + ex.getMessage());
        }
    }
*/
    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void nuevoCliente(){

        JTextField txtNombre = new JTextField();
        JTextField txtEmail = new JTextField();
        JCheckBox chkActivo = new JCheckBox();

        Object[] formulario = {
                "Nombre: ", txtNombre,
                "Email: ", txtEmail,
                chkActivo
        };

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Nuevo cliente",
                JOptionPane.OK_CANCEL_OPTION

        );

        if (opcion != JOptionPane.OK_OPTION){
            return;
        }

        if (txtNombre.getText().isBlank() || txtEmail.getText().isBlank()){
            mostrarError("Nombre y mail son obligatorios");
            return;
        }

        ClienteEntidad cliente = new ClienteEntidad (
                null,
                txtNombre.getText().trim(),
                txtEmail.getText().trim(),
                chkActivo.isSelected()
        );

        try {

            clienteDAO.insertar(cliente);
            cargarClientes();

        } catch (RuntimeException e){
            mostrarError("Error insertando cliente \n " + e.getMessage());
        }

    }

    public void editarCliente() {

        int fila = tablaClientes.getSelectedRow();

        if (fila == -1){
            mostrarError("Selecciona algún cliente");
            return;
        }

        Integer id = (Integer) modeloClientes.getValueAt(fila, 0);

        JTextField txtNombre = new JTextField(
                modeloClientes.getValueAt(fila, 1).toString()
        );
        JTextField txtEmail = new JTextField(
                modeloClientes.getValueAt(fila, 2).toString()
        );

        JCheckBox chkActivo = new JCheckBox("Activo",
                (Boolean) modeloClientes.getValueAt(fila, 3));

        Object[] formulario = {
                "Nombre: ", txtNombre,
                "Email: ", txtEmail,
                chkActivo
        };

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Editar cliente",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION){
            return;
        }

        ClienteEntidad cliente = new ClienteEntidad(
                id,
                txtNombre.getText().trim(),
                txtEmail.getText().trim(),
                chkActivo.isSelected()
        );

        try {
            clienteDAO.actualizar(cliente);
            cargarClientes();
        } catch (RuntimeException e) {
            mostrarError("Error actualizando cliente " + e.getMessage());
        }
    }
}
