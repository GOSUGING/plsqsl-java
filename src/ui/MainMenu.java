package ui;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Menu Principal - Corredora Inmobiliaria");
        setSize(520, 240);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.add(new JLabel("<html><h2>Corredora - Panel de Control</h2></html>"));
        root.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));

        // Panel de Procedimientos
        JPanel pProcs = new JPanel(new BorderLayout(6,6));
        pProcs.setBorder(BorderFactory.createTitledBorder("Operaciones / Procedimientos"));
        JButton btnProcs = new JButton("Abrir Procedimientos");
        btnProcs.addActionListener(e -> {
            ProceduresWindow pw = new ProceduresWindow(this);
            pw.setVisible(true);
            this.setVisible(false);
        });
        pProcs.add(new JLabel("<html><p>Ejecutar procesos: generar haberes, detectar solapamientos, ver salida DBMS_OUTPUT.</p></html>"), BorderLayout.CENTER);
        pProcs.add(btnProcs, BorderLayout.SOUTH);

        // Panel de Ingreso de Datos
        JPanel pData = new JPanel(new BorderLayout(6,6));
        pData.setBorder(BorderFactory.createTitledBorder("Ingreso de Datos"));
        JButton btnData = new JButton("Abrir Formulario de Datos");
        btnData.addActionListener(e -> {
            DataEntryWindow de = new DataEntryWindow(this);
            de.setVisible(true);
            this.setVisible(false);
        });
        pData.add(new JLabel("<html><p>Crear/editar registros de prueba (pendiente de implementar inserciones en BD).</p></html>"), BorderLayout.CENTER);
        pData.add(btnData, BorderLayout.SOUTH);

        center.add(pProcs);
        center.add(pData);

        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExit = new JButton("Salir");
        btnExit.addActionListener(e -> System.exit(0));
        bottom.add(btnExit);
        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }
}
