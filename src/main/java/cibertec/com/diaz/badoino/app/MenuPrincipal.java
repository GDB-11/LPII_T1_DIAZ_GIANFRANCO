package cibertec.com.diaz.badoino.app;

import cibertec.com.diaz.badoino.util.JPAUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Menú principal del sistema de inventarios
 * @author Gianfranco Díaz Badoino - I202413435
 */
public class MenuPrincipal extends JFrame {
    
    private JButton btnRegistro;
    private JButton btnListado;
    private JButton btnSalir;
    
    public MenuPrincipal() {
        setTitle("Sistema de Gestión de Inventarios - Distribuidora Multinacional");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(240, 240, 240));
        
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(new Color(41, 128, 185));
        panelTitulo.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JLabel lblTitulo = new JLabel("SISTEMA DE INVENTARIOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Distribuidora Multinacional");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(236, 240, 241));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelTitulo.add(lblSubtitulo);
        
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new GridBagLayout());
        panelCentral.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        btnRegistro = new JButton("Registro de Inventarios");
        btnRegistro.setPreferredSize(new Dimension(300, 50));
        btnRegistro.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegistro.setBackground(new Color(46, 204, 113));
        btnRegistro.setForeground(Color.WHITE);
        btnRegistro.setFocusPainted(false);
        btnRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnRegistro.addActionListener(e -> abrirRegistro());
        
        btnListado = new JButton("Listado de Inventarios");
        btnListado.setPreferredSize(new Dimension(300, 50));
        btnListado.setFont(new Font("Arial", Font.BOLD, 14));
        btnListado.setBackground(new Color(52, 152, 219));
        btnListado.setForeground(Color.WHITE);
        btnListado.setFocusPainted(false);
        btnListado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnListado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnListado.addActionListener(e -> abrirListado());
        
        btnSalir = new JButton("Salir");
        btnSalir.setPreferredSize(new Dimension(300, 50));
        btnSalir.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalir.setBackground(new Color(231, 76, 60));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnSalir.addActionListener(e -> salir());
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCentral.add(btnRegistro, gbc);
        
        gbc.gridy = 1;
        panelCentral.add(btnListado, gbc);
        
        gbc.gridy = 2;
        panelCentral.add(btnSalir, gbc);
        
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(new Color(240, 240, 240));
        JLabel lblAutor = new JLabel("Gianfranco Díaz Badoino - I202413435");
        lblAutor.setFont(new Font("Arial", Font.ITALIC, 11));
        lblAutor.setForeground(new Color(127, 140, 141));
        panelInferior.add(lblAutor);
        
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void abrirRegistro() {
        RegistroDiaz ventanaRegistro = new RegistroDiaz(this, RegistroDiaz.Modo.INSERTAR, null);
        ventanaRegistro.setVisible(true);
    }
    
    private void abrirListado() {
        ListadoDiaz ventanaListado = new ListadoDiaz(this);
        ventanaListado.setVisible(true);
    }
    
    private void salir() {
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
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}