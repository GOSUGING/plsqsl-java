package db;

import util.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    static {
        // Forzar carga del driver Oracle JDBC (ayuda cuando el driver no se registra automáticamente)
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            System.out.println("Oracle JDBC driver cargado.");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró oracle.jdbc.OracleDriver. Asegura ojdbc en classpath.");
            // No lanzar aquí para que el error sea controlable en tiempo de conexión si prefieres
        }
    }

    /**
     * Obtiene la conexión usando el alias o URL configurado.
     * Si config.properties tiene db.url = jdbc:oracle:thin:@taller_high -> se usa tal cual.
     */
    public static Connection getConnection() throws SQLException {
        String url = Config.get("db.url");      // ej: jdbc:oracle:thin:@taller_high
        String user = Config.get("db.user");
        String pass = Config.get("db.password");

        if (url == null || !url.startsWith("jdbc:")) {
            // Si en alguna parte guardas solo el alias (taller_high) y no "jdbc:..."
            // se construye la URL estándar
            if (url != null) {
                url = "jdbc:oracle:thin:@" + url;
            } else {
                throw new SQLException("db.url no configurado en config.properties");
            }
        }

        return DriverManager.getConnection(url, user, pass);
    }

    public static Connection getConnection(String serviceAlias) throws SQLException {
        String user = Config.get("db.user");
        String pass = Config.get("db.password");
        String url = "jdbc:oracle:thin:@" + serviceAlias;
        return DriverManager.getConnection(url, user, pass);
    }
}
