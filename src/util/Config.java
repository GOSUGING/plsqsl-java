package util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();
    private static String walletDir;

    static {
        try (InputStream in = Config.class.getResourceAsStream("/config.properties")) {
            if (in == null) throw new RuntimeException("No se encuentra config.properties");
            props.load(in);

            String rawWallet = props.getProperty("wallet.dir");
            if (rawWallet != null && !rawWallet.isBlank()) {
                walletDir = resolveWalletDir(rawWallet);
                System.out.println("Usando wallet.dir resuelto a: " + walletDir);

                // Configura Oracle para que use Wallet
                System.setProperty("oracle.net.tns_admin", walletDir);
                System.setProperty("oracle.net.ssl_server_dn_match", "false");

                // Debug: verificar existencia de archivos importantes
                Path p = Paths.get(walletDir);
                System.out.println("Existe sqlnet.ora? -> " + Files.exists(p.resolve("sqlnet.ora")));
                System.out.println("Existe cwallet.sso? -> " + Files.exists(p.resolve("cwallet.sso")));
            } else {
                throw new RuntimeException("wallet.dir no está definido en config.properties");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando config.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    private static String resolveWalletDir(String configured) {
        Path p = Paths.get(configured);
        if (p.isAbsolute()) return p.toAbsolutePath().toString();
        // Relativo al proyecto
        try {
            Path jarPath = Paths.get(Config.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            Path resolved = jarPath.getParent().resolve(configured);
            return resolved.toAbsolutePath().toString();
        } catch (Exception e) {
            return p.toAbsolutePath().toString();
        }
    }

    public static String getWalletDir() {
        return walletDir;
    }
}
