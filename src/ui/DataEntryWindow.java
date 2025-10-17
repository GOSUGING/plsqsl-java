package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana simple para ingresar datos de prueba.
 * Actualmente guarda en memoria en una lista. Más adelante se implementará persistencia en BD.
 */
public class DataEntryWindow extends JFrame {

    private final JFrame parent;
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtRut = new JTextField(12);
    private final JTextArea taNotes = new JTextArea(6, 30);
    private final JButton btnSave = new JButton("Guardar (temporal)");
    private final List<MockRegistro> registros = new ArrayList<>();

    public DataEntryWindow(JFrame parent) {
        this.parent = parent;
        setTitle("Ingreso de Datos - Corredora");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Nombre:"), c);
        c.gridx = 1; form.add(txtNombre, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("RUT/ID:"), c);
        c.gridx = 1; form.add(txtRut, c);

        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Notas:"), c);
        c.gridx = 1; form.add(new JScrollPane(taNotes), c);

        c.gridx = 1; c.gridy = 3; form.add(btnSave, c);

        JButton btnBack = new JButton("Volver");
        btnBack.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBack);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> doSave());
    }

    private void doSave() {
        String nombre = txtNombre.getText().trim();
        String rut = txtRut.getText().trim();
        String notas = taNotes.getText().trim();

        if (nombre.isEmpty() || rut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y RUT son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Guardado temporal
        MockRegistro r = new MockRegistro(nombre, rut, notas);
        registros.add(r);

        JOptionPane.showMessageDialog(this, "Registro guardado (temporal). Total guardados: " + registros.size());
        // limpiar campos
        txtNombre.setText("");
        txtRut.setText("");
        taNotes.setText("");
    }

    // Clase interna simple para mantener datos en memoria
    private static class MockRegistro {
        String nombre;
        String rut;
        String notas;
        public MockRegistro(String n, String r, String no) { nombre=n; rut=r; notas=no; }
    }
}
