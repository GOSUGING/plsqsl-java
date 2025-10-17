package dao;

import db.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AuditDao {
    public static List<Map<String,Object>> getAudit(int limit) throws SQLException {
        String q = "SELECT id_audit, nro_propiedad, numrut_cli, fecini_arriendo_old, fecini_arriendo_new, fecha_modificacion FROM AUDIT_ARRIENDO ORDER BY fecha_modificacion DESC";
        if (limit > 0) q = "SELECT * FROM (" + q + ") WHERE ROWNUM <= " + limit;
        List<Map<String,Object>> out = new ArrayList<>();
        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("id_audit", rs.getLong("id_audit"));
                m.put("nro_propiedad", rs.getInt("nro_propiedad"));
                m.put("numrut_cli", rs.getLong("numrut_cli"));
                m.put("fecini_old", rs.getTimestamp("fecini_arriendo_old"));
                m.put("fecini_new", rs.getTimestamp("fecini_arriendo_new"));
                m.put("fecha_mod", rs.getTimestamp("fecha_modificacion"));
                out.add(m);
            }
        }
        return out;
    }
}