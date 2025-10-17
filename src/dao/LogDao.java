package dao;
import db.DbConnection;
import model.LogEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDao {
    public static List<LogEntry> getLogs(int limit) throws SQLException {
        String q = "SELECT id_error, subprograma, gravedad, mensaje, fecha_proceso FROM LOG_ERRORES_PLSQL ORDER BY fecha_proceso DESC";
        if (limit > 0) q = "SELECT * FROM (" + q + ") WHERE ROWNUM <= " + limit;
        List<LogEntry> res = new ArrayList<>();
        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                res.add(new LogEntry(
                        rs.getLong("id_error"),
                        rs.getString("subprograma"),
                        rs.getString("gravedad"),
                        rs.getString("mensaje"),
                        rs.getTimestamp("fecha_proceso")
                ));
            }
        }
        return res;
    }
}