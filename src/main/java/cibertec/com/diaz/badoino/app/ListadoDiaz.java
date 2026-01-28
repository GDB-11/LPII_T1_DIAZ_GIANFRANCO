package cibertec.com.diaz.badoino.app;

import cibertec.com.diaz.badoino.controller.InventarioJpaController;
import cibertec.com.diaz.badoino.model.Inventario;
import cibertec.com.diaz.badoino.util.JPAUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GUI para el listado de inventarios con funcionalidad de crear y editar
 * @author Gianfranco Díaz Badoino - I202413435
 */
public class ListadoDiaz extends JFrame {
    
    private JTable tablaInventarios;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnActualizar;
    private JButton btnSalir;
    private JLabel lblTotalRegistros;
    
    private InventarioJpaController inventarioController;
    private List<Inventario> listaInventarios;
    private JFrame ventanaPadre;
    
    /**
     * Constructor sin parámetros (standalone desde el main)
     */
    public ListadoDiaz() {
        this(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Constructor con ventana padre (desde menú principal)
     * @param padre Ventana padre que invoca esta ventana
     */
    public ListadoDiaz(JFrame padre) {
        this.ventanaPadre = padre;
        inventarioController = new InventarioJpaController();
        
        setTitle("Listado de Inventarios - Distribuidora Multinacional");
        setSize(950, 650);
        
        if (padre != null) {
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        
        setLocationRelativeTo(padre);
        setResizable(true);
        
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(240, 240, 240));
        
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(41, 128, 185));
        JLabel lblTitulo = new JLabel("LISTADO DE INVENTARIOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        String[] columnas = {
            "Nro. Inventario",
            "Fecha",
            "Producto",
            "Categoría",
            "Costo Ingreso (S/)",
            "Motivo"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaInventarios = new JTable(modeloTabla);
        tablaInventarios.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaInventarios.setRowHeight(25);
        tablaInventarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaInventarios.getTableHeader().setBackground(new Color(52, 73, 94));
        tablaInventarios.getTableHeader().setForeground(Color.WHITE);
        tablaInventarios.setSelectionBackground(new Color(52, 152, 219));
        tablaInventarios.setSelectionForeground(Color.WHITE);
        tablaInventarios.setGridColor(new Color(189, 195, 199));
        tablaInventarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaInventarios.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tablaInventarios.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        
        tablaInventarios.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaInventarios.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaInventarios.getColumnModel().getColumn(2).setPreferredWidth(200);
        tablaInventarios.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaInventarios.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaInventarios.getColumnModel().getColumn(5).setPreferredWidth(250);
        
        tablaInventarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarRegistro();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaInventarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());
        panelInferior.setBackground(new Color(240, 240, 240));
        
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBackground(new Color(240, 240, 240));
        lblTotalRegistros = new JLabel("Total de registros: 0");
        lblTotalRegistros.setFont(new Font("Arial", Font.BOLD, 13));
        lblTotalRegistros.setForeground(new Color(52, 73, 94));
        panelInfo.add(lblTotalRegistros);
        
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(240, 240, 240));
        
        btnNuevo = new JButton("Nuevo");
        btnNuevo.setPreferredSize(new Dimension(120, 35));
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 12));
        btnNuevo.setBackground(new Color(52, 152, 219));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> nuevoRegistro());
        
        btnEditar = new JButton("Editar");
        btnEditar.setPreferredSize(new Dimension(120, 35));
        btnEditar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEditar.setBackground(new Color(241, 196, 15));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.addActionListener(e -> editarRegistro());
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setPreferredSize(new Dimension(120, 35));
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 12));
        btnActualizar.setBackground(new Color(46, 204, 113));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarDatos());
        
        btnSalir = new JButton("Salir");
        btnSalir.setPreferredSize(new Dimension(120, 35));
        btnSalir.setFont(new Font("Arial", Font.BOLD, 12));
        btnSalir.setBackground(new Color(231, 76, 60));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> salir());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnSalir);
        
        panelInferior.add(panelInfo, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.CENTER);
        
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    /**
     * Método público para que RegistroDiaz pueda actualizar los datos
     */
    public void cargarDatos() {
        try {
            modeloTabla.setRowCount(0);
            
            listaInventarios = inventarioController.findInventarioEntities();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            for (Inventario inv : listaInventarios) {
                Object[] fila = new Object[6];
                fila[0] = inv.getNroInventario();
                fila[1] = inv.getFecha().format(formatter);
                fila[2] = inv.getProducto().getNomProd();
                fila[3] = inv.getProducto().getCategoria().getIdCate().toString();
                fila[4] = String.format("%.2f", inv.getCostoIngreso());
                
                String motivo = inv.getMotivoIngreso();
                if (motivo.length() > 50) {
                    motivo = motivo.substring(0, 47) + "...";
                }
                fila[5] = motivo;
                
                modeloTabla.addRow(fila);
            }
            
            lblTotalRegistros.setText("Total de registros: " + listaInventarios.size());
            
            btnEditar.setEnabled(listaInventarios.size() > 0);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error al cargar los datos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    private void nuevoRegistro() {
        RegistroDiaz ventanaRegistro = new RegistroDiaz(
            this, 
            RegistroDiaz.Modo.INSERTAR, 
            null
        );
        ventanaRegistro.setVisible(true);
    }
    
    private void editarRegistro() {
        int filaSeleccionada = tablaInventarios.getSelectedRow();
        
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor, seleccione un registro de la tabla para editar.",
                "Seleccione un registro",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        Inventario inventarioSeleccionado = listaInventarios.get(filaSeleccionada);
        
        Integer nroInventario = inventarioSeleccionado.getNroInventario();
        Inventario inventarioCompleto = inventarioController.findInventario(nroInventario);
        
        if (inventarioCompleto == null) {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo cargar el registro seleccionado.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        RegistroDiaz ventanaEdicion = new RegistroDiaz(
            this, 
            RegistroDiaz.Modo.ACTUALIZAR, 
            inventarioCompleto
        );
        ventanaEdicion.setVisible(true);
    }
    
    private void salir() {
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
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ListadoDiaz ventana = new ListadoDiaz();
            ventana.setVisible(true);
        });
    }
}