package ui;

import dao.AuditArriendoDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AuditArriendoWindow extends JFrame {

    private final JFrame parent;
    private JTable table;

    public AuditArriendoWindow(JFrame parent) {
        this.parent = parent;
        setTitle("📜 Auditoría de Arriendos");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        cargarDatos();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("🔄 Actualizar");
        btnRefresh.addActionListener(e -> cargarDatos());
        JButton btnBack = new JButton("⬅ Volver");
        btnBack.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });
        bottom.add(btnRefresh);
        bottom.add(btnBack);
        add(bottom, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return AuditArriendoDao.listarAuditoria();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> data = get();
                    String[] cols = {"ID", "Propiedad", "Cliente", "FecIni Old", "FecIni New", "FecTer Old", "FecTer New", "Usuario", "Fecha Modificación"};
                    Object[][] rows = new Object[data.size()][cols.length];

                    for (int i = 0; i < data.size(); i++) {
                        Map<String, Object> row = data.get(i);
                        rows[i][0] = row.get("id_audit");
                        rows[i][1] = row.get("nro_propiedad");
                        rows[i][2] = row.get("numrut_cli");
                        rows[i][3] = row.get("fecini_arriendo_old");
                        rows[i][4] = row.get("fecini_arriendo_new");
                        rows[i][5] = row.get("fecter_arriendo_old");
                        rows[i][6] = row.get("fecter_arriendo_new");
                        rows[i][7] = row.get("usuario");
                        rows[i][8] = row.get("fecha_modificacion");
                    }

                    table.setModel(new DefaultTableModel(rows, cols));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AuditArriendoWindow.this, "Error al cargar auditoría: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }
}
