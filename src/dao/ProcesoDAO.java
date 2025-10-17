package dao;

import db.DbConnection;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcesoDAO {

    private static final Logger LOGGER = Logger.getLogger(ProcesoDAO.class.getName());

    /**
     * Genera los haberes mensuales llamando al procedimiento P_GENERAR_HABERES_MENSUALES.
     *
     * @param mes  Mes (1-12)
     * @param anno Año (ej: 2025)
     * @throws SQLException si ocurre un error en la base de datos
     */
    public static void generarHaberesMensuales(int mes, int anno) throws SQLException {
        validateMesAnno(mes, anno);
        String sql = "{ call P_GENERAR_HABERES_MENSUALES(?, ?) }";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, mes);
                cs.setInt(2, anno);
                cs.execute();
            }

            // Leer DBMS_OUTPUT en la misma sesión y guardar en LOG_ERRORES_PLSQL
            try {
                DBMSOutputUtil.enable(conn, 1000000);
                List<String> lines = DBMSOutputUtil.readAll(conn);
                for (String line : lines) {
                    insertLog(conn, "P_GENERAR_HABERES_MENSUALES", "INFO", line);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "No se pudo leer DBMS_OUTPUT: " + ex.getMessage());
            }

            conn.commit();
            LOGGER.info(() -> "P_GENERAR_HABERES_MENSUALES ejecutado correctamente para " + mes + "/" + anno);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando P_GENERAR_HABERES_MENSUALES: " + e.getMessage(), e);
            insertLogIndependent("P_GENERAR_HABERES_MENSUALES", "ERROR", getRootCauseMessage(e));
            throw e;
        }
    }

    /**
     * Detecta solapamientos llamando al procedimiento P_DETECTAR_SOLAPAMIENTOS.
     *
     * @throws SQLException si ocurre un error en la base de datos
     */
    public static void detectarSolapamientos() throws SQLException {
        String sql = "{ call P_DETECTAR_SOLAPAMIENTOS() }";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.execute();
            }

            // Leer DBMS_OUTPUT en la misma sesión
            try {
                DBMSOutputUtil.enable(conn, 1000000);
                List<String> lines = DBMSOutputUtil.readAll(conn);
                for (String line : lines) {
                    insertLog(conn, "P_DETECTAR_SOLAPAMIENTOS", "INFO", line);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "No se pudo leer DBMS_OUTPUT: " + ex.getMessage());
            }

            conn.commit();
            LOGGER.info("P_DETECTAR_SOLAPAMIENTOS ejecutado correctamente.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando P_DETECTAR_SOLAPAMIENTOS: " + e.getMessage(), e);
            insertLogIndependent("P_DETECTAR_SOLAPAMIENTOS", "ERROR", getRootCauseMessage(e));
            throw e;
        }
    }

    // ---------------- helpers ----------------

    private static void insertLog(Connection conn, String subprograma, String gravedad, String mensaje) throws SQLException {
        String ins = "INSERT INTO LOG_ERRORES_PLSQL (id_error, subprograma, gravedad, mensaje, fecha_proceso) " +
                "VALUES (seq_error.NEXTVAL, ?, ?, ?, SYSDATE)";
        try (PreparedStatement ps = conn.prepareStatement(ins)) {
            ps.setString(1, subprograma);
            ps.setString(2, gravedad);
            ps.setString(3, truncate(mensaje, 4000));
            ps.executeUpdate();
        }
    }

    private static void insertLogIndependent(String subprograma, String gravedad, String mensaje) {
        try (Connection c = DbConnection.getConnection()) {
            c.setAutoCommit(true);
            insertLog(c, subprograma, gravedad, mensaje);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fallo registrando LOG_ERRORES_PLSQL independiente: " + e.getMessage(), e);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    private static String getRootCauseMessage(Throwable t) {
        Throwable curr = t;
        while (curr.getCause() != null) curr = curr.getCause();
        return curr.getMessage() != null ? curr.getMessage() : t.toString();
    }

    private static void validateMesAnno(int mes, int anno) {
        if (mes < 1 || mes > 12)
            throw new IllegalArgumentException("El parámetro 'mes' debe estar entre 1 y 12.");
        if (anno < 1900 || anno > 9999)
            throw new IllegalArgumentException("El parámetro 'anno' parece inválido.");
    }
}
