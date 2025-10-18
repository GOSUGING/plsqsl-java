package ui;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Menú Principal - Corredora Inmobiliaria");
        setSize(960, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));

        // === Título superior ===
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.add(new JLabel("<html><h2>🏢 Corredora - Panel de Control</h2></html>"));
        root.add(top, BorderLayout.NORTH);

        // === Panel central con 3 columnas ===
        JPanel center = new JPanel(new GridLayout(1, 3, 12, 12));

        // === Panel Procedimientos ===
        JPanel pProcs = new JPanel(new BorderLayout(6, 6));
        pProcs.setBorder(BorderFactory.createTitledBorder("Operaciones / Procedimientos"));
        JButton btnProcs = new JButton("📜 Abrir Procedimientos");
        btnProcs.addActionListener(e -> {
            ProceduresWindow pw = new ProceduresWindow(this);
            pw.setVisible(true);
            this.setVisible(false);
        });
        pProcs.add(new JLabel("<html><p>Ejecutar procesos:<br>- Generar haberes<br>- Detectar solapamientos<br>- Ver salida DBMS_OUTPUT</p></html>"), BorderLayout.CENTER);
        pProcs.add(btnProcs, BorderLayout.SOUTH);

        // === Panel CRUD Arriendos ===
        JPanel pCrud = new JPanel(new BorderLayout(6, 6));
        pCrud.setBorder(BorderFactory.createTitledBorder("CRUD Arriendos"));
        JButton btnCrud = new JButton("🏠 Abrir CRUD Arriendos");
        btnCrud.addActionListener(e -> {
            CrudArriendosWindow crud = new CrudArriendosWindow(this);
            crud.setVisible(true);
            this.setVisible(false);
        });
        pCrud.add(new JLabel("<html><p>Gestionar arriendos:<br>- Crear registros<br>- Listar y consultar<br>- Actualizar<br>- Eliminar</p></html>"), BorderLayout.CENTER);
        pCrud.add(btnCrud, BorderLayout.SOUTH);

        // === Panel Auditoría ===
        JPanel pAudit = new JPanel(new BorderLayout(6, 6));
        pAudit.setBorder(BorderFactory.createTitledBorder("Auditoría de Arriendos"));
        JButton btnAudit = new JButton("📊 Ver Auditoría");
        btnAudit.addActionListener(e -> {
            AuditArriendoWindow audit = new AuditArriendoWindow(this);
            audit.setVisible(true);
            this.setVisible(false);
        });
        pAudit.add(new JLabel("<html><p>Ver historial de cambios<br>de fechas de arriendos<br>registrados por el trigger.</p></html>"), BorderLayout.CENTER);
        pAudit.add(btnAudit, BorderLayout.SOUTH);

        // Agregamos los tres paneles al centro
        center.add(pProcs);
        center.add(pCrud);
        center.add(pAudit);
        root.add(center, BorderLayout.CENTER);

        // === Pie de página ===
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExit = new JButton("Salir");
        btnExit.addActionListener(e -> System.exit(0));
        bottom.add(btnExit);
        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }
}
