package util;

public class Test {
    public static void main(String[] args) throws Exception {
        // Lee wallet.dir desde config.properties si lo usas,
        // pero aquí prueba con varias alternativas para asegurarnos.
        String[] tries = new String[] {
                "./Wallet_TALLER",
                ".\\Wallet_TALLER",
                "Wallet_TALLER",
                "C:\\Users\\aroon\\IdeaProjects\\ProyectoPLSQLSwing\\Wallet_TALLER" // ajusta si hace falta
        };

        for (String w : tries) {
            java.io.File dir = new java.io.File(w);
            System.out.println("Probando: " + w);
            System.out.println("  exists(): " + dir.exists());
            System.out.println("  isDirectory(): " + dir.isDirectory());
            System.out.println("  absolutePath: " + dir.getAbsolutePath());
            java.io.File tn = new java.io.File(dir, "tnsnames.ora");
            System.out.println("  tnsnames.ora existe? " + tn.exists());
            if (dir.exists() && dir.isDirectory()) {
                System.out.println("  Contenido de la carpeta:");
                for (java.io.File f : dir.listFiles()) {
                    System.out.println("    - " + f.getName() + (f.isDirectory() ? " [DIR]" : ""));
                }
            }
            System.out.println("----------------------------------------------------");
        }
    }
}
