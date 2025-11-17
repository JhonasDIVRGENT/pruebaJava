package com.jhonas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class VideojuegoGUI extends JFrame {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRefrescar, btnAgregar, btnEliminar, btnActualizar, btnLimpiar;
    private JTextField txtTitulo, txtGenero, txtPlataforma, txtStock, txtPrecio;
    private JTextField txtBuscar;
    private JLabel lblContador, lblEstado;
    
    // Colores personalizados
    private static final Color PRIMARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color LIGHT_BG = new Color(236, 240, 241);
    private static final Color WHITE = Color.WHITE;
    
    public VideojuegoGUI() {
        setTitle("Sistema de Inventario de Videojuegos");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(LIGHT_BG);
        
        // Panel superior con título y gradiente
        JPanel panelTitulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 73, 94), 
                                                     getWidth(), 0, new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelTitulo.setPreferredSize(new Dimension(0, 80));
        panelTitulo.setLayout(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("🎮 INVENTARIO DE VIDEOJUEGOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(WHITE);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión Completo", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(230, 230, 230));
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelTitulo.add(lblSubtitulo, BorderLayout.SOUTH);
        
        mainPanel.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de búsqueda mejorado
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelBusqueda.setBackground(WHITE);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblBuscar = new JLabel("🔍 Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscar.setForeground(PRIMARY_COLOR);
        
        txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setPreferredSize(new Dimension(250, 35));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Búsqueda en tiempo real
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtBuscar.getText().length() >= 2 || txtBuscar.getText().isEmpty()) {
                    buscarVideojuegos();
                }
            }
        });
        
        JButton btnBuscar = crearBoton("Buscar", ACCENT_COLOR, "🔍");
        btnBuscar.addActionListener(e -> buscarVideojuegos());
        
        JButton btnMostrarTodos = crearBoton("Mostrar Todo", new Color(149, 165, 166), "📋");
        btnMostrarTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cargarDatos();
        });
        
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);
        
        // Panel de tabla mejorado
        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setBackground(LIGHT_BG);
        panelTabla.add(panelBusqueda, BorderLayout.NORTH);
        
        // Crear tabla con diseño mejorado
        String[] columnas = {"ID", "Título", "Género", "Plataforma", "Stock", "Precio"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4) return Integer.class;
                return String.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(189, 195, 199));
        table.setSelectionBackground(new Color(52, 152, 219, 100));
        table.setSelectionForeground(PRIMARY_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Encabezado de tabla mejorado
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        
        // Renderizador para celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // Ajustar anchos de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Efecto hover en filas
        table.addMouseMotionListener(new MouseMotionAdapter() {
            private int lastRow = -1;
            
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != lastRow) {
                    lastRow = row;
                    table.repaint();
                }
            }
        });
        
        // Listener para selección
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                cargarDatosEnFormulario();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(WHITE);
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(panelTabla, BorderLayout.CENTER);
        
        // Panel lateral derecho mejorado
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 2, 0, 0, ACCENT_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panelFormulario.setPreferredSize(new Dimension(400, 0));
        
        // Título del panel
        JLabel lblTituloForm = new JLabel("📝 GESTIÓN DE VIDEOJUEGOS");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloForm.setForeground(PRIMARY_COLOR);
        lblTituloForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloForm.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panelFormulario.add(lblTituloForm);
        
        // Campos del formulario con estilo mejorado
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setBackground(WHITE);
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        txtTitulo = agregarCampoFormulario(panelCampos, "Título del Videojuego:", "Ej: The Legend of Zelda");
        txtGenero = agregarCampoFormulario(panelCampos, "Género:", "Ej: Aventura, RPG, Acción");
        txtPlataforma = agregarCampoFormulario(panelCampos, "Plataforma:", "Ej: PC, PS5, Xbox, Switch");
        txtStock = agregarCampoFormulario(panelCampos, "Stock Disponible:", "Cantidad en inventario");
        txtPrecio = agregarCampoFormulario(panelCampos, "Precio (USD):", "Ej: 59.99");
        
        panelFormulario.add(panelCampos);
        
        // Panel de botones mejorado
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBackground(WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        btnRefrescar = crearBotonGrande("🔄  Refrescar Datos", ACCENT_COLOR);
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        btnAgregar = crearBotonGrande("➕  Agregar Nuevo", SUCCESS_COLOR);
        btnAgregar.addActionListener(e -> agregarVideojuego());
        
        btnActualizar = crearBotonGrande("✏️  Actualizar Selección", WARNING_COLOR);
        btnActualizar.addActionListener(e -> actualizarVideojuego());
        
        btnEliminar = crearBotonGrande("🗑️  Eliminar Selección", DANGER_COLOR);
        btnEliminar.addActionListener(e -> eliminarVideojuego());
        
        btnLimpiar = crearBotonGrande("🧹  Limpiar Formulario", new Color(149, 165, 166));
        btnLimpiar.addActionListener(e -> limpiarCampos());
        
        panelBotones.add(btnRefrescar);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelBotones.add(btnAgregar);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelBotones.add(btnActualizar);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelBotones.add(btnEliminar);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 10)));
        panelBotones.add(btnLimpiar);
        
        panelFormulario.add(panelBotones);
        panelFormulario.add(Box.createVerticalGlue());
        
        mainPanel.add(panelFormulario, BorderLayout.EAST);
        
        // Panel inferior mejorado con información
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(PRIMARY_COLOR);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelInfo.setPreferredSize(new Dimension(0, 50));
        
        lblContador = new JLabel("📊 Total de registros: 0");
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblContador.setForeground(WHITE);
        panelInfo.add(lblContador, BorderLayout.WEST);
        
        lblEstado = new JLabel("Listo");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(new Color(230, 230, 230));
        lblEstado.setHorizontalAlignment(SwingConstants.RIGHT);
        panelInfo.add(lblEstado, BorderLayout.EAST);
        
        mainPanel.add(panelInfo, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Cargar datos iniciales
        cargarDatos();
    }
    
    // Método auxiliar para crear campos de formulario
    private JTextField agregarCampoFormulario(JPanel panel, String etiqueta, String placeholder) {
        JPanel campoPanel = new JPanel();
        campoPanel.setLayout(new BoxLayout(campoPanel, BoxLayout.Y_AXIS));
        campoPanel.setBackground(WHITE);
        campoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        campoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(PRIMARY_COLOR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(340, 35));
        txt.setMaximumSize(new Dimension(340, 35));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Agregar tooltip
        txt.setToolTipText(placeholder);
        
        // Efecto focus
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
        
        campoPanel.add(lbl);
        campoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        campoPanel.add(txt);
        
        panel.add(campoPanel);
        return txt;
    }
    
    // Método auxiliar para crear botones pequeños
    private JButton crearBoton(String texto, Color color, String emoji) {
        JButton btn = new JButton(emoji + " " + texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 35));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    // Método auxiliar para crear botones grandes del formulario
    private JButton crearBotonGrande(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(340, 45));
        btn.setPreferredSize(new Dimension(340, 45));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    private void cargarDatos() {
        tableModel.setRowCount(0);
        lblEstado.setText("⏳ Cargando datos...");
        
        String query = "SELECT VideojuegoID, Titulo, Genero, Plataforma, Stock, Precio FROM Videojuegos ORDER BY VideojuegoID";
        
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("VideojuegoID"),
                    rs.getString("Titulo"),
                    rs.getString("Genero"),
                    rs.getString("Plataforma"),
                    rs.getInt("Stock"),
                    String.format("$%.2f", rs.getDouble("Precio"))
                };
                tableModel.addRow(fila);
                count++;
            }
            
            actualizarContador(count);
            lblEstado.setText("✓ Datos cargados exitosamente");
            
            mostrarNotificacion("Datos cargados: " + count + " registros", "success");
            
        } catch (SQLException e) {
            lblEstado.setText("✗ Error al cargar datos");
            mostrarNotificacion("Error al cargar datos: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }
    
    private void buscarVideojuegos() {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarDatos();
            return;
        }
        
        tableModel.setRowCount(0);
        
        String query = "SELECT VideojuegoID, Titulo, Genero, Plataforma, Stock, Precio " +
                      "FROM Videojuegos WHERE Titulo LIKE ? OR Genero LIKE ? OR Plataforma LIKE ?";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String patron = "%" + busqueda + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            pstmt.setString(3, patron);
            
            ResultSet rs = pstmt.executeQuery();
            int count = 0;
            
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("VideojuegoID"),
                    rs.getString("Titulo"),
                    rs.getString("Genero"),
                    rs.getString("Plataforma"),
                    rs.getInt("Stock"),
                    String.format("$%.2f", rs.getDouble("Precio"))
                };
                tableModel.addRow(fila);
                count++;
            }
            
            actualizarContador(count);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarVideojuego() {
        if (!validarCampos()) return;
        
        String query = "{CALL sp_InsertarVideojuego(?, ?, ?, ?, ?)}";
        
        try (Connection conn = ConexionDB.getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {
            
            cstmt.setString(1, txtTitulo.getText().trim());
            cstmt.setString(2, txtGenero.getText().trim());
            cstmt.setString(3, txtPlataforma.getText().trim());
            cstmt.setInt(4, Integer.parseInt(txtStock.getText().trim()));
            cstmt.setDouble(5, Double.parseDouble(txtPrecio.getText().trim()));
            
            cstmt.execute();
            
            mostrarNotificacion("Videojuego agregado exitosamente", "success");
            lblEstado.setText("✓ Nuevo videojuego agregado");
            limpiarCampos();
            cargarDatos();
            
        } catch (SQLException e) {
            mostrarNotificacion("Error al agregar: " + e.getMessage(), "error");
            lblEstado.setText("✗ Error en la operación");
        }
    }
    
    private void actualizarVideojuego() {
        int filaSeleccionada = table.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarNotificacion("Selecciona un videojuego para actualizar", "warning");
            return;
        }
        
        if (!validarCampos()) return;
        
        int id = (int) tableModel.getValueAt(filaSeleccionada, 0);
        String query = "{CALL sp_ActualizarVideojuego(?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = ConexionDB.getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {
            
            cstmt.setInt(1, id);
            cstmt.setString(2, txtTitulo.getText().trim());
            cstmt.setString(3, txtGenero.getText().trim());
            cstmt.setString(4, txtPlataforma.getText().trim());
            cstmt.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            cstmt.setDouble(6, Double.parseDouble(txtPrecio.getText().trim()));
            
            cstmt.execute();
            
            mostrarNotificacion("Videojuego actualizado exitosamente", "success");
            lblEstado.setText("✓ Registro actualizado");
            limpiarCampos();
            cargarDatos();
            
        } catch (SQLException e) {
            mostrarNotificacion("Error al actualizar: " + e.getMessage(), "error");
            lblEstado.setText("✗ Error en la operación");
        }
    }
    
    private void eliminarVideojuego() {
        int filaSeleccionada = table.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarNotificacion("Selecciona un videojuego para eliminar", "warning");
            return;
        }
        
        int id = (int) tableModel.getValueAt(filaSeleccionada, 0);
        String titulo = (String) tableModel.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "<html><body style='width: 250px'><b>¿Estás seguro de eliminar este videojuego?</b><br><br>" + 
                "Título: " + titulo + "<br><br>" +
                "Esta acción no se puede deshacer.</body></html>", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) return;
        
        String query = "{CALL sp_EliminarVideojuego(?)}";
        
        try (Connection conn = ConexionDB.getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {
            
            cstmt.setInt(1, id);
            cstmt.execute();
            
            mostrarNotificacion("Videojuego eliminado exitosamente", "success");
            lblEstado.setText("✓ Registro eliminado");
            limpiarCampos();
            cargarDatos();
            
        } catch (SQLException e) {
            mostrarNotificacion("Error al eliminar: " + e.getMessage(), "error");
            lblEstado.setText("✗ Error en la operación");
        }
    }
    
    private void cargarDatosEnFormulario() {
        int filaSeleccionada = table.getSelectedRow();
        if (filaSeleccionada != -1) {
            txtTitulo.setText(tableModel.getValueAt(filaSeleccionada, 1).toString());
            txtGenero.setText(tableModel.getValueAt(filaSeleccionada, 2).toString());
            txtPlataforma.setText(tableModel.getValueAt(filaSeleccionada, 3).toString());
            txtStock.setText(tableModel.getValueAt(filaSeleccionada, 4).toString());
            // Remover el símbolo $ del precio
            String precio = tableModel.getValueAt(filaSeleccionada, 5).toString().replace("$", "");
            txtPrecio.setText(precio);
        }
    }
    
    private void limpiarCampos() {
        txtTitulo.setText("");
        txtGenero.setText("");
        txtPlataforma.setText("");
        txtStock.setText("");
        txtPrecio.setText("");
        txtBuscar.setText("");
        table.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título es obligatorio", 
                                        "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero", 
                                        "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Double.parseDouble(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido", 
                                        "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void actualizarContador(int count) {
        lblContador.setText("📊 Total de registros: " + count);
    }
    
    // Nuevo método para mostrar notificaciones visuales
    private void mostrarNotificacion(String mensaje, String tipo) {
        int tipoMensaje;
        String titulo;
        
        switch (tipo.toLowerCase()) {
            case "success":
                tipoMensaje = JOptionPane.INFORMATION_MESSAGE;
                titulo = "✓ Operación Exitosa";
                break;
            case "error":
                tipoMensaje = JOptionPane.ERROR_MESSAGE;
                titulo = "✗ Error";
                break;
            case "warning":
                tipoMensaje = JOptionPane.WARNING_MESSAGE;
                titulo = "⚠ Advertencia";
                break;
            default:
                tipoMensaje = JOptionPane.INFORMATION_MESSAGE;
                titulo = "Información";
        }
        
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }
    
    public static void main(String[] args) {
        // Usar el look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            VideojuegoGUI gui = new VideojuegoGUI();
            gui.setVisible(true);
        });
    }
}
