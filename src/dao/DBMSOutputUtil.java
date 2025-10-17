package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DBMSOutputUtil {

    /**
     * Habilita el buffer DBMS_OUTPUT con un tamaño determinado.
     */
    public static void enable(Connection conn, int bufferSize) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("BEGIN DBMS_OUTPUT.ENABLE(?); END;")) {
            cs.setInt(1, bufferSize);
            cs.execute();
        }
    }

    /**
     * Lee todas las líneas desde DBMS_OUTPUT usando GET_LINE.
     */
    public static List<String> readAll(Connection conn) throws SQLException {
        List<String> lines = new ArrayList<>();
        try (CallableStatement cs = conn.prepareCall("BEGIN DBMS_OUTPUT.GET_LINE(:1, :2); END;")) {
            cs.registerOutParameter(1, Types.VARCHAR);
            cs.registerOutParameter(2, Types.INTEGER);

            while (true) {
                cs.execute();
                String line = cs.getString(1);
                int status = cs.getInt(2);
                if (status == 1) break; // no hay más líneas
                lines.add(line != null ? line : "");
            }
        }
        return lines;
    }
}
