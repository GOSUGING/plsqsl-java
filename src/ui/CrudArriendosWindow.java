package ui;

import db.DbConnection;
import oracle.jdbc.internal.OracleTypes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class CrudArriendosWindow extends JFrame {

    private final JFrame parent;

    private final JTextField txtNroPropiedad = new JTextField(10);
    private final JTextField txtRutCliente = new JTextField(10);
    private final JTextField txtFechaInicio = new JTextField(10);
    private final JTextField txtFechaTermino = new JTextField(10);

    private final JButton btnCrear = new JButton("➕ Crear");
    private final JButton btnActualizarFechas = new JButton("📝 Actualizar Fechas");
    private final JButton btnActualizarTermino = new JButton("📝 Actualizar Término");
    private final JButton btnEliminar = new JButton("🗑 Eliminar");
    private final JButton btnBuscarPorPropiedad = new JButton("🔍 Buscar Propiedad");
    private final JButton btnBuscarPorCliente = new JButton("🔍 Buscar Cliente");
    private final JButton btnVolver = new JButton("↩ Volver");

    private final JTable tblArriendos = new JTable();
    private final DefaultTableModel tableModel = new DefaultTableModel();

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public CrudArriendosWindow(JFrame parent) {
        this.parent = parent;
        setTitle("CRUD Arriendos de Propiedades");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        initListeners();
    }

    private void initUI() {
        // Panel formulario
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Arriendo"));
        formPanel.add(new JLabel("N° Propiedad:"));
        formPanel.add(txtNroPropiedad);
        formPanel.add(new JLabel("RUT Cliente:"));
        formPanel.add(txtRutCliente);
        formPanel.add(new JLabel("Fecha Inicio (YYYY-MM-DD):"));
        formPanel.add(txtFechaInicio);
        formPanel.add(new JLabel("Fecha Término (YYYY-MM-DD):"));
        formPanel.add(txtFechaTermino);

        // Panel botones CRUD
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        crudPanel.add(btnCrear);
        crudPanel.add(btnActualizarFechas);
        crudPanel.add(btnActualizarTermino);
        crudPanel.add(btnEliminar);

        // Panel búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(btnBuscarPorPropiedad);
        searchPanel.add(btnBuscarPorCliente);
        searchPanel.add(btnVolver);

        // Tabla resultados
        tableModel.setColumnIdentifiers(new String[]{"N° Propiedad", "RUT Cliente", "Fecha Inicio", "Fecha Término"});
        tblArriendos.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblArriendos);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(crudPanel, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.EAST);
    }

    private void initListeners() {
        btnCrear.addActionListener(e -> crearArriendo());
        btnActualizarFechas.addActionListener(e -> actualizarFechas());
        btnActualizarTermino.addActionListener(e -> actualizarTermino());
        btnEliminar.addActionListener(e -> eliminarArriendo());
        btnBuscarPorPropiedad.addActionListener(e -> listarPorPropiedad());
        btnBuscarPorCliente.addActionListener(e -> listarPorCliente());
        btnVolver.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
    }

    private void crearArriendo() {
        try (Connection conn = DbConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{ call PACKAGE_CRUD_ARRIENDOS.crear_arriendo_propiedad(?, ?, ?, ?) }");
            cs.setInt(1, Integer.parseInt(txtNroPropiedad.getText().trim()));
            cs.setString(2, txtRutCliente.getText().trim());
            cs.setDate(3, java.sql.Date.valueOf(txtFechaInicio.getText().trim()));

            if (txtFechaTermino.getText().trim().isEmpty()) {
                cs.setNull(4, Types.DATE);
            } else {
                cs.setDate(4, java.sql.Date.valueOf(txtFechaTermino.getText().trim()));
            }

            cs.execute();
            JOptionPane.showMessageDialog(this, "✅ Arriendo creado correctamente.");
            listarPorPropiedad();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void actualizarFechas() {
        try (Connection conn = DbConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{ call PACKAGE_CRUD_ARRIENDOS.actualizar_fechas_arriendo(?, ?, ?, ?) }");
            cs.setInt(1, Integer.parseInt(txtNroPropiedad.getText().trim()));
            cs.setString(2, txtRutCliente.getText().trim());
            cs.setDate(3, java.sql.Date.valueOf(txtFechaInicio.getText().trim()));
            cs.setDate(4, java.sql.Date.valueOf(txtFechaTermino.getText().trim()));
            cs.execute();
            JOptionPane.showMessageDialog(this, "✅ Fechas actualizadas correctamente.");
            listarPorPropiedad();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void actualizarTermino() {
        try (Connection conn = DbConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{ call PACKAGE_CRUD_ARRIENDOS.actualizar_termino_arriendo(?, ?, ?) }");
            cs.setInt(1, Integer.parseInt(txtNroPropiedad.getText().trim()));
            cs.setString(2, txtRutCliente.getText().trim());

            if (txtFechaTermino.getText().trim().isEmpty()) {
                cs.setNull(3, Types.DATE);
            } else {
                cs.setDate(3, java.sql.Date.valueOf(txtFechaTermino.getText().trim()));
            }

            cs.execute();
            JOptionPane.showMessageDialog(this, "✅ Fecha término actualizada correctamente.");
            listarPorPropiedad();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void eliminarArriendo() {
        try (Connection conn = DbConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{ call PACKAGE_CRUD_ARRIENDOS.eliminar_arriendo_propiedad(?, ?) }");
            cs.setInt(1, Integer.parseInt(txtNroPropiedad.getText().trim()));
            cs.setString(2, txtRutCliente.getText().trim());
            cs.execute();
            JOptionPane.showMessageDialog(this, "🗑 Arriendo eliminado.");
            listarPorPropiedad();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void listarPorPropiedad() {
        limpiarTabla();
        try (Connection conn = DbConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{ ? = call PACKAGE_CRUD_ARRIENDOS.listar_arriendos_por_propiedad(?) }");
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, Integer.parseInt(txtNroPropiedad.getText().trim()));
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("NRO_PROPIEDAD"),
                        rs.getString("NUMRUT_CLI"),
                        rs.getDate("FECINI_ARRIENDO"),
                        rs.getDate("FECTER_ARRIENDO")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error al listar: " + ex.getMessage());
        }
    }

    private void listarPorCliente() {
        limpiarTabla();
        try (Connection conn = DbConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{ ? = call PACKAGE_CRUD_ARRIENDOS.listar_arriendos_por_cliente(?) }");
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, txtRutCliente.getText().trim());
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("NRO_PROPIEDAD"),
                        rs.getString("NUMRUT_CLI"),
                        rs.getDate("FECINI_ARRIENDO"),
                        rs.getDate("FECTER_ARRIENDO")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error al listar: " + ex.getMessage());
        }
    }

    private void limpiarTabla() {
        tableModel.setRowCount(0);
    }
}
