package ui;

import dao.DBMSOutputUtil;
import dao.ProcedureDao;
import dao.ProcesoDAO;
import db.DbConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProceduresWindow extends JFrame {

    private final JFrame parent;
    private final JTextField txtMes = new JTextField("7", 3);
    private final JTextField txtAnno = new JTextField("2025", 5);
    private final JButton btnGenerar = new JButton("Generar Haberes");
    private final JButton btnSolap = new JButton("Detectar Solapamientos");
    private final JButton btnLeerDbms = new JButton("Leer DBMS_OUTPUT");
    private final JButton btnVerDbmsRaw = new JButton("Ver DBMS_RAW");
    private final JButton btnExportCsv = new JButton("Exportar CSV");

    private final JLabel lblCountTotal = new JLabel("Total: 0");
    private final JLabel lblCountInfo = new JLabel("Info: 0");
    private final JLabel lblCountWarn = new JLabel("Warn: 0");
    private final JLabel lblCountError = new JLabel("Error: 0");

    private final DefaultListModel<String> procListModel = new DefaultListModel<>();
    private final JList<String> procList = new JList<>(procListModel);
    private final JTextArea taSource = new JTextArea();

    private JTable tblResults;
    private DefaultTableModel resultsModel;
    private final List<String> lastDbmsLines = new ArrayList<>();

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ProceduresWindow(JFrame parent) {
        this.parent = parent;
        setTitle("Procedimientos - Corredora");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadProcedures();
    }

    private void initUI() {
        // Top panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Mes:"));
        top.add(txtMes);
        top.add(new JLabel("Año:"));
        top.add(txtAnno);
        top.add(btnGenerar);
        top.add(btnSolap);
        top.add(btnLeerDbms);
        top.add(btnVerDbmsRaw);
        JButton btnBack = new JButton("Volver");
        btnBack.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        top.add(btnBack);

        // Left list
        procList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spList = new JScrollPane(procList);
        spList.setPreferredSize(new Dimension(320, 420));
        procList.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                String sel = procList.getSelectedValue();
                if (sel != null) {
                    String[] parts = sel.split(":", 2);
                    String name = parts.length == 2 ? parts[1] : sel;
                    loadSource(name);
                    // Cargar logs históricos para este procedimiento
                    cargarLogsHistoricos(name);
                }
            }
        });

        // Source area
        taSource.setEditable(false);
        taSource.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane spSource = new JScrollPane(taSource);

        // Table
        initResultsTable();
        JScrollPane spResults = new JScrollPane(tblResults);
        spResults.setPreferredSize(new Dimension(800, 260));

        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spSource, spResults);
        centerSplit.setResizeWeight(0.6);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spList, centerSplit);
        mainSplit.setResizeWeight(0.27);

        add(top, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);

        // Bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(lblCountTotal);
        bottom.add(new JLabel("  |  "));
        bottom.add(lblCountInfo);
        bottom.add(new JLabel("  |  "));
        bottom.add(lblCountWarn);
        bottom.add(new JLabel("  |  "));
        bottom.add(lblCountError);
        bottom.add(Box.createHorizontalStrut(20));
        bottom.add(btnExportCsv);
        add(bottom, BorderLayout.SOUTH);

        // Listeners
        btnGenerar.addActionListener(e -> ejecutarGenerarInSameSession());
        btnSolap.addActionListener(e -> detectarSolapamientosInSameSession());
        btnLeerDbms.addActionListener(e -> leerDbmsExplicit());
        btnVerDbmsRaw.addActionListener(e -> showDbmsRawDialog());
        btnExportCsv.addActionListener(e -> {
            try {
                exportResultsToCsv();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exportando CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void initResultsTable() {
        String[] cols = new String[]{"Fecha/Hora", "Nivel", "Mensaje"};
        resultsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblResults = new JTable(resultsModel);
        tblResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tblResults.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblResults.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblResults.getColumnModel().getColumn(2).setPreferredWidth(860);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value,
                                                                     boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String lvl = table.getValueAt(row, 1).toString().toUpperCase();
                switch (lvl) {
                    case "ERROR" -> c.setForeground(Color.RED);
                    case "WARN", "WARNING" -> c.setForeground(new Color(200, 100, 0));
                    case "INFO" -> c.setForeground(new Color(0, 128, 0));
                    default -> c.setForeground(Color.BLACK);
                }
                if (isSelected) c.setBackground(new Color(220, 240, 255));
                else c.setBackground(Color.WHITE);
                return c;
            }
        };
        tblResults.setDefaultRenderer(Object.class, renderer);
    }

    private void loadProcedures() {
        procListModel.clear();
        new SwingWorker<List<String>, Void>() {
            @Override protected List<String> doInBackground() throws Exception {
                return ProcedureDao.listProcedures();
            }
            @Override protected void done() {
                try {
                    List<String> list = get();
                    for (String s : list) procListModel.addElement(s);
                    taSource.setText("-- Selecciona un procedimiento o paquete para ver su código aquí --");
                } catch (Exception e) {
                    taSource.setText("-- Error cargando lista de procedimientos: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void loadSource(String name) {
        taSource.setText("-- Cargando código de " + name + " ...");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception {
                return ProcedureDao.getSource(name);
            }
            @Override protected void done() {
                try {
                    String src = get();
                    taSource.setText(src);
                    taSource.setCaretPosition(0);
                } catch (Exception e) {
                    taSource.setText("-- Error cargando source: " + e.getMessage());
                }
            }
        }.execute();
    }

    // ---------------- Ejecución ----------------

    private void ejecutarGenerarInSameSession() {
        int mes, anno;
        try {
            mes = Integer.parseInt(txtMes.getText().trim());
            anno = Integer.parseInt(txtAnno.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mes o año inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultsModel.setRowCount(0);
        lastDbmsLines.clear();

        new SwingWorker<Void, String>() {
            @Override protected Void doInBackground() throws Exception {
                try {
                    ProcesoDAO.generarHaberesMensuales(mes, anno);
                } catch (Exception ex) {
                    publish("ERROR|Error ejecutando Generar Haberes: " + ex.getMessage());
                }
                // Leer DBMS_OUTPUT de la sesión y publicar
                try (Connection c = DbConnection.getConnection()) {
                    DBMSOutputUtil.enable(c, 1000000);
                    List<String> lines = DBMSOutputUtil.readAll(c);
                    for (String line : lines) publish("INFO|" + line);
                } catch (Exception ex) {
                    publish("WARN|No se pudo leer DBMS_OUTPUT: " + ex.getMessage());
                }
                return null;
            }

            @Override protected void process(List<String> chunks) {
                for (String msg : chunks) addLineToTable(msg);
            }
        }.execute();
    }

    private void detectarSolapamientosInSameSession() {
        resultsModel.setRowCount(0);
        lastDbmsLines.clear();

        new SwingWorker<Void, String>() {
            @Override protected Void doInBackground() throws Exception {
                try {
                    ProcesoDAO.detectarSolapamientos();
                } catch (Exception ex) {
                    publish("ERROR|Error ejecutando Detectar Solapamientos: " + ex.getMessage());
                }
                try (Connection c = DbConnection.getConnection()) {
                    DBMSOutputUtil.enable(c, 1000000);
                    List<String> lines = DBMSOutputUtil.readAll(c);
                    for (String line : lines) publish("INFO|" + line);
                } catch (Exception ex) {
                    publish("WARN|No se pudo leer DBMS_OUTPUT: " + ex.getMessage());
                }
                return null;
            }

            @Override protected void process(List<String> chunks) {
                for (String msg : chunks) addLineToTable(msg);
            }
        }.execute();
    }

    private void leerDbmsExplicit() {
        resultsModel.setRowCount(0);
        lastDbmsLines.clear();
        try (Connection c = DbConnection.getConnection()) {
            DBMSOutputUtil.enable(c, 1000000);
            List<String> lines = DBMSOutputUtil.readAll(c);
            for (String line : lines) addLineToTable("INFO|" + line);
        } catch (Exception ex) {
            addLineToTable("ERROR|No se pudo leer DBMS_OUTPUT: " + ex.getMessage());
        }
    }

    private void showDbmsRawDialog() {
        JTextArea ta = new JTextArea(String.join("\n", lastDbmsLines));
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(800, 400));
        JOptionPane.showMessageDialog(this, sp, "DBMS_RAW", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addLineToTable(String msg) {
        String[] parts = msg.split("\\|", 2);
        String nivel = parts.length > 0 ? parts[0] : "INFO";
        String texto = parts.length > 1 ? parts[1] : msg;
        String fecha = LocalDateTime.now().format(DF);
        resultsModel.addRow(new String[]{fecha, nivel, texto});
        lastDbmsLines.add(nivel + "|" + texto);
        actualizarResumen();
    }

    private void actualizarResumen() {
        int total = resultsModel.getRowCount();
        int info = 0, warn = 0, error = 0;
        for (int i = 0; i < resultsModel.getRowCount(); i++) {
            String lvl = resultsModel.getValueAt(i, 1).toString().toUpperCase();
            if (lvl.contains("INFO")) info++;
            else if (lvl.contains("WARN")) warn++;
            else if (lvl.contains("ERROR")) error++;
        }
        lblCountTotal.setText("Total: " + total);
        lblCountInfo.setText("Info: " + info);
        lblCountWarn.setText("Warn: " + warn);
        lblCountError.setText("Error: " + error);
    }

    private void exportResultsToCsv() throws Exception {
        if (resultsModel.getRowCount() == 0) throw new Exception("No hay datos para exportar");
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                // Header
                for (int c = 0; c < resultsModel.getColumnCount(); c++) {
                    fw.append(resultsModel.getColumnName(c));
                    if (c < resultsModel.getColumnCount() - 1) fw.append(",");
                }
                fw.append("\n");
                // Rows
                for (int r = 0; r < resultsModel.getRowCount(); r++) {
                    for (int c = 0; c < resultsModel.getColumnCount(); c++) {
                        fw.append(resultsModel.getValueAt(r, c).toString().replaceAll(",", " "));
                        if (c < resultsModel.getColumnCount() - 1) fw.append(",");
                    }
                    fw.append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, "CSV exportado correctamente");
        }
    }

    // ---------------- Carga de logs históricos ----------------

    private void cargarLogsHistoricos(String subprograma) {
        resultsModel.setRowCount(0);
        lastDbmsLines.clear();
        new SwingWorker<List<String[]>, String>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                List<String[]> rows = new ArrayList<>();
                String sql = "SELECT fecha_proceso, gravedad, mensaje " +
                        "FROM LOG_ERRORES_PLSQL " +
                        "WHERE subprograma = ? " +
                        "ORDER BY fecha_proceso ASC";
                try (Connection c = DbConnection.getConnection();
                     PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, subprograma);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String fecha = rs.getTimestamp("fecha_proceso").toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            String nivel = rs.getString("gravedad");
                            String msg = rs.getString("mensaje");
                            rows.add(new String[]{fecha, nivel, msg});
                        }
                    }
                }
                return rows;
            }

            @Override
            protected void done() {
                try {
                    List<String[]> rows = get();
                    for (String[] r : rows) {
                        resultsModel.addRow(r);
                        lastDbmsLines.add(r[1] + "|" + r[2]);
                    }
                    actualizarResumen();
                } catch (Exception e) {
                    addLineToTable("ERROR|No se pudieron cargar logs históricos: " + e.getMessage());
                }
            }
        }.execute();
    }
}
