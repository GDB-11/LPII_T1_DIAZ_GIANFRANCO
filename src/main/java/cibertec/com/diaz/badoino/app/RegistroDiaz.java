package cibertec.com.diaz.badoino.app;

import cibertec.com.diaz.badoino.controller.InventarioJpaController;
import cibertec.com.diaz.badoino.controller.ProductoJpaController;
import cibertec.com.diaz.badoino.controller.exceptions.NonexistentEntityException;
import cibertec.com.diaz.badoino.model.Inventario;
import cibertec.com.diaz.badoino.model.Producto;
import cibertec.com.diaz.badoino.util.JPAUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GUI para el registro y actualización de inventarios
 * @author Gianfranco Díaz Badoino - I202413435
 */
public class RegistroDiaz extends JFrame {
    
    // Enum para definir el modo de operación
    public enum Modo {
        INSERTAR, ACTUALIZAR
    }
    
    private JComboBox<String> cboProducto;
    private JTextField txtCostoIngreso;
    private JTextArea txtMotivoIngreso;
    private JLabel lblFechaActual;
    private JPanel panelMensaje;
    private JLabel lblMensaje;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnCancelar;
    
    private InventarioJpaController inventarioController;
    private ProductoJpaController productoController;
    
    private List<Producto> productos;
    
    private Modo modoActual;
    private Inventario inventarioEditando;
    private JFrame ventanaPadre;
    
    /**
     * Constructor para modo INSERTAR (desde el main - standalone)
     */
    public RegistroDiaz() {
        this(null, Modo.INSERTAR, null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Constructor para modo INSERTAR o ACTUALIZAR (desde otra ventana)
     * @param padre Ventana padre que invoca esta ventana
     * @param modo Modo de operación (INSERTAR o ACTUALIZAR)
     * @param inventario Inventario a editar (solo si modo es ACTUALIZAR)
     */
    public RegistroDiaz(JFrame padre, Modo modo, Inventario inventario) {
        this.ventanaPadre = padre;
        this.modoActual = modo;
        this.inventarioEditando = inventario;
        
        inventarioController = new InventarioJpaController();
        productoController = new ProductoJpaController();
        
        String tituloModo = (modo == Modo.ACTUALIZAR) ? "Actualización" : "Registro";
        setTitle("Sistema de " + tituloModo + " de Inventario - Distribuidora Multinacional");
        setSize(600, 550);
        
        if (padre != null) {
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        
        setLocationRelativeTo(padre);
        setResizable(false);
        
        initComponents();        
        cargarProductos();
        
        if (modo == Modo.ACTUALIZAR && inventario != null) {
            cargarDatosInventario(inventario);
        } else {
            actualizarFecha();
        }
    }
    
    private void initComponents() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(240, 240, 240));
        
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(41, 128, 185));
        String tituloTexto = (modoActual == Modo.ACTUALIZAR) ? 
            "ACTUALIZACIÓN DE INVENTARIO" : "REGISTRO DE INVENTARIO";
        JLabel lblTitulo = new JLabel(tituloTexto);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridBagLayout());
        panelFormulario.setBorder(new TitledBorder("Datos del Ingreso"));
        panelFormulario.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblFecha = new JLabel("Fecha de Ingreso:");
        lblFecha.setFont(new Font("Arial", Font.BOLD, 12));
        panelFormulario.add(lblFecha, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        lblFechaActual = new JLabel();
        lblFechaActual.setFont(new Font("Arial", Font.PLAIN, 12));
        lblFechaActual.setForeground(new Color(41, 128, 185));
        panelFormulario.add(lblFechaActual, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblProducto = new JLabel("Producto: *");
        lblProducto.setFont(new Font("Arial", Font.BOLD, 12));
        panelFormulario.add(lblProducto, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cboProducto = new JComboBox<>();
        cboProducto.setPreferredSize(new Dimension(350, 25));
        cboProducto.setFont(new Font("Arial", Font.PLAIN, 12));
        panelFormulario.add(cboProducto, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblCosto = new JLabel("Costo de Ingreso (S/): *");
        lblCosto.setFont(new Font("Arial", Font.BOLD, 12));
        panelFormulario.add(lblCosto, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtCostoIngreso = new JTextField();
        txtCostoIngreso.setPreferredSize(new Dimension(150, 25));
        txtCostoIngreso.setFont(new Font("Arial", Font.PLAIN, 12));
        panelFormulario.add(txtCostoIngreso, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        JLabel lblMotivo = new JLabel("Motivo de Ingreso: *");
        lblMotivo.setFont(new Font("Arial", Font.BOLD, 12));
        panelFormulario.add(lblMotivo, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtMotivoIngreso = new JTextArea(4, 30);
        txtMotivoIngreso.setFont(new Font("Arial", Font.PLAIN, 12));
        txtMotivoIngreso.setLineWrap(true);
        txtMotivoIngreso.setWrapStyleWord(true);
        txtMotivoIngreso.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollMotivo = new JScrollPane(txtMotivoIngreso);
        scrollMotivo.setPreferredSize(new Dimension(350, 80));
        panelFormulario.add(scrollMotivo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 10));
        lblNota.setForeground(Color.RED);
        panelFormulario.add(lblNota, gbc);
        
        panelMensaje = new JPanel();
        panelMensaje.setLayout(new BorderLayout());
        panelMensaje.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelMensaje.setVisible(false);
        
        lblMensaje = new JLabel();
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 13));
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        lblMensaje.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelMensaje.add(lblMensaje, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(240, 240, 240));
        
        String textoBotonGuardar = (modoActual == Modo.ACTUALIZAR) ? "Actualizar" : "Guardar";
        btnGuardar = new JButton(textoBotonGuardar);
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarInventario());
        
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setPreferredSize(new Dimension(120, 35));
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 12));
        btnLimpiar.setBackground(new Color(241, 196, 15));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> cancelar());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCancelar);
        
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BorderLayout());
        panelCentral.setBackground(new Color(240, 240, 240));
        panelCentral.add(panelFormulario, BorderLayout.CENTER);
        panelCentral.add(panelMensaje, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void cargarProductos() {
        try {
            productos = productoController.findProductoEntities();
            cboProducto.removeAllItems();
            cboProducto.addItem("-- Seleccione un producto --");
            
            for (Producto producto : productos) {
                String item = producto.getIdProd() + " - " + producto.getNomProd() + 
                             " (Stock: " + producto.getStockActual() + ")";
                cboProducto.addItem(item);
            }
            
        } catch (Exception e) {
            mostrarMensajeError("✗ ERROR AL CARGAR PRODUCTOS: " + e.getMessage());
        }
    }
    
    private void cargarDatosInventario(Inventario inventario) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            lblFechaActual.setText(inventario.getFecha().format(formatter));
            
            Integer idProducto = inventario.getProducto().getIdProd();
            for (int i = 0; i < productos.size(); i++) {
                if (productos.get(i).getIdProd().equals(idProducto)) {
                    cboProducto.setSelectedIndex(i + 1);
                    break;
                }
            }
            
            txtCostoIngreso.setText(inventario.getCostoIngreso().toString());
            txtMotivoIngreso.setText(inventario.getMotivoIngreso());
            
        } catch (Exception e) {
            mostrarMensajeError("✗ ERROR AL CARGAR DATOS: " + e.getMessage());
        }
    }
    
    private void actualizarFecha() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        lblFechaActual.setText(LocalDateTime.now().format(formatter));
    }
    
    private void guardarInventario() {
        try {
            ocultarMensaje();
            
            if (!validarDatos()) {
                return;
            }
            
            int indiceProducto = cboProducto.getSelectedIndex() - 1;
            if (indiceProducto < 0) {
                mostrarMensajeError("Debe seleccionar un producto");
                return;
            }
            
            Producto productoSeleccionado = productos.get(indiceProducto);
            BigDecimal costoIngreso = new BigDecimal(txtCostoIngreso.getText().trim());
            String motivoIngreso = txtMotivoIngreso.getText().trim();
            
            if (modoActual == Modo.INSERTAR) {
                // MODO INSERTAR
                Inventario inventario = new Inventario();
                inventario.setProducto(productoSeleccionado);
                inventario.setCostoIngreso(costoIngreso);
                inventario.setMotivoIngreso(motivoIngreso);
                inventario.setFecha(LocalDateTime.now());
                
                inventarioController.create(inventario);
                
                String mensajeExito = String.format(
                    "✓ REGISTRO GUARDADO EXITOSAMENTE - Producto: %s | Costo: S/ %.2f | Fecha: %s",
                    productoSeleccionado.getNomProd(),
                    costoIngreso,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                );
                mostrarMensajeExito(mensajeExito);
                
                limpiarFormulario();
                
            } else {
                // MODO ACTUALIZAR
                inventarioEditando.setProducto(productoSeleccionado);
                inventarioEditando.setCostoIngreso(costoIngreso);
                inventarioEditando.setMotivoIngreso(motivoIngreso);
                
                inventarioController.edit(inventarioEditando);
                
                String mensajeExito = String.format(
                    "✓ REGISTRO ACTUALIZADO EXITOSAMENTE - Nro: %d | Producto: %s | Costo: S/ %.2f",
                    inventarioEditando.getNroInventario(),
                    productoSeleccionado.getNomProd(),
                    costoIngreso
                );
                mostrarMensajeExito(mensajeExito);
                
                Timer timer = new Timer(2000, event -> {
                    cerrarVentana();
                });
                timer.setRepeats(false);
                timer.start();
            }
            
        } catch (NumberFormatException e) {
            mostrarMensajeError("✗ ERROR: El costo de ingreso debe ser un número válido");
        } catch (NonexistentEntityException e) {
            mostrarMensajeError("✗ ERROR: El inventario no existe en la base de datos");
        } catch (Exception e) {
            mostrarMensajeError("✗ ERROR AL GUARDAR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validarDatos() {
        if (cboProducto.getSelectedIndex() == 0) {
            mostrarMensajeError("✗ ERROR: Debe seleccionar un producto");
            cboProducto.requestFocus();
            return false;
        }
        
        String costoTexto = txtCostoIngreso.getText().trim();
        if (costoTexto.isEmpty()) {
            mostrarMensajeError("✗ ERROR: Debe ingresar el costo de ingreso");
            txtCostoIngreso.requestFocus();
            return false;
        }
        
        try {
            BigDecimal costo = new BigDecimal(costoTexto);
            if (costo.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensajeError("✗ ERROR: El costo de ingreso debe ser mayor a 0");
                txtCostoIngreso.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensajeError("✗ ERROR: El costo de ingreso debe ser un número válido (ejemplo: 2500.00)");
            txtCostoIngreso.requestFocus();
            return false;
        }
        
        String motivo = txtMotivoIngreso.getText().trim();
        if (motivo.isEmpty()) {
            mostrarMensajeError("✗ ERROR: Debe ingresar el motivo de ingreso");
            txtMotivoIngreso.requestFocus();
            return false;
        }
        
        if (motivo.length() < 10) {
            mostrarMensajeError("✗ ERROR: El motivo de ingreso debe tener al menos 10 caracteres");
            txtMotivoIngreso.requestFocus();
            return false;
        }
        
        if (motivo.length() > 200) {
            mostrarMensajeError("✗ ERROR: El motivo de ingreso no puede exceder 200 caracteres");
            txtMotivoIngreso.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void limpiarFormulario() {
        if (modoActual == Modo.INSERTAR) {
            cboProducto.setSelectedIndex(0);
            txtCostoIngreso.setText("");
            txtMotivoIngreso.setText("");
            actualizarFecha();
            cboProducto.requestFocus();
        } else {
            if (inventarioEditando != null) {
                cargarDatosInventario(inventarioEditando);
            }
        }
    }
    
    private void cancelar() {
        if (ventanaPadre != null) {
            dispose();
        } else {
            int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (opcion == JOptionPane.YES_OPTION) {
                JPAUtil.closeEntityManagerFactory();
                System.exit(0);
            }
        }
    }
    
    private void cerrarVentana() {
        if (ventanaPadre != null && ventanaPadre instanceof ListadoDiaz) {
            ((ListadoDiaz) ventanaPadre).cargarDatos();
        }
        dispose();
    }
    
    private void mostrarMensajeExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(Color.WHITE);
        panelMensaje.setBackground(new Color(46, 204, 113));
        panelMensaje.setVisible(true);
        panelMensaje.revalidate();
        panelMensaje.repaint();
    }
    
    private void mostrarMensajeError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(Color.WHITE);
        panelMensaje.setBackground(new Color(231, 76, 60));
        panelMensaje.setVisible(true);
        panelMensaje.revalidate();
        panelMensaje.repaint();
    }
    
    private void ocultarMensaje() {
        panelMensaje.setVisible(false);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            RegistroDiaz ventana = new RegistroDiaz();
            ventana.setVisible(true);
        });
    }
}