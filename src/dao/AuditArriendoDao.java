package dao;

import db.DbConnection;

import java.sql.*;
import java.util.*;

public class AuditArriendoDao {

    public static List<Map<String, Object>> listarAuditoria() throws SQLException {
        String sql = "SELECT id_audit, nro_propiedad, numrut_cli, " +
                "fecini_arriendo_old, fecini_arriendo_new, " +
                "fecter_arriendo_old, fecter_arriendo_new, " +
                "usuario, fecha_modificacion " +
                "FROM AUDIT_ARRIENDO ORDER BY id_audit DESC";

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id_audit", rs.getLong("id_audit"));
                row.put("nro_propiedad", rs.getLong("nro_propiedad"));
                row.put("numrut_cli", rs.getString("numrut_cli"));
                row.put("fecini_arriendo_old", rs.getDate("fecini_arriendo_old"));
                row.put("fecini_arriendo_new", rs.getDate("fecini_arriendo_new"));
                row.put("fecter_arriendo_old", rs.getDate("fecter_arriendo_old"));
                row.put("fecter_arriendo_new", rs.getDate("fecter_arriendo_new"));
                row.put("usuario", rs.getString("usuario"));
                row.put("fecha_modificacion", rs.getTimestamp("fecha_modificacion"));
                list.add(row);
            }
        }

        return list;
    }
}
