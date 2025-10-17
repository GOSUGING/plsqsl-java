package dao;

import db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para listar procedimientos/paquetes y obtener su código fuente (USER_SOURCE).
 */
public class ProcedureDao {

    /**
     * Devuelve una lista de nombres de objetos (PACKAGE o PROCEDURE) del esquema actual.
     * Filtra objetos propios (USER_OBJECTS).
     * @return Lista de strings con formato "TYPE:NAME" (ej: "PACKAGE:PKG_UTILS")
     * @throws Exception si hay error en la conexión o consulta
     */
    public static List<String> listProcedures() throws Exception {
        List<String> out = new ArrayList<>();
        String sql = "SELECT OBJECT_NAME, OBJECT_TYPE " +
                "FROM USER_OBJECTS " +
                "WHERE OBJECT_TYPE IN ('PROCEDURE','PACKAGE') " +
                "ORDER BY OBJECT_TYPE, OBJECT_NAME";

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("OBJECT_NAME");
                String type = rs.getString("OBJECT_TYPE");
                out.add(type + ":" + name); // ej. "PACKAGE:PKG_UTILS"
            }
        } catch (Exception e) {
            System.err.println("ERROR listando procedimientos: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        if (out.isEmpty()) {
            System.out.println("DEBUG: No se encontraron PROCEDURE ni PACKAGE en el esquema.");
        }

        return out;
    }

    /**
     * Obtiene el source (texto) de un objeto (PACKAGE o PROCEDURE) dado su nombre.
     * Si es PACKAGE devuelve todo el package body + spec (orden por LINE).
     *
     * @param name nombre del objeto (ej: "P_GENERAR_HABERES_MENSUALES" o "PKG_MI")
     * @return Código fuente completo como string
     * @throws Exception si hay error en la conexión o consulta
     */
    public static String getSource(String name) throws Exception {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT TEXT FROM USER_SOURCE WHERE NAME = ? ORDER BY LINE";

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasLines = false;
                while (rs.next()) {
                    hasLines = true;
                    String line = rs.getString("TEXT");
                    sb.append(line == null ? "" : line).append("\n"); // saltos de línea para legibilidad
                }

                if (!hasLines) {
                    return "-- No se encontró código fuente para: " + name;
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR obteniendo código fuente de " + name + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return sb.toString();
    }
}
