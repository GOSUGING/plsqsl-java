# 🏢 Corredora Inmobiliaria - Gestión PL/SQL + Java Swing

Este proyecto es una **aplicación de escritorio en Java Swing** que se conecta a una base de datos **Oracle Autonomous Database** usando **Wallet** y permite:

- 📜 Ejecutar procedimientos almacenados (`P_GENERAR_HABERES_MENSUALES`, `P_DETECTAR_SOLAPAMIENTOS`).
- 🏠 Gestionar registros de arriendos (CRUD).
- 📊 Visualizar el historial de auditoría generado automáticamente por triggers.
- 🧾 Ver mensajes `DBMS_OUTPUT` directamente en la interfaz gráfica.
- 🔐 Configurar credenciales y wallet en un archivo externo (`config.properties`).

---

## 🧰 Tecnologías utilizadas

- ☕ **Java 17+**
- 🖼️ **Swing** (interfaz gráfica)
- 🗃️ **Oracle Database (Autonomous)** con Wallet
- 📡 **JDBC** (ojdbc11.jar)
- 🧰 IntelliJ IDEA (recomendado)

---

## 📁 Estructura del proyecto
````
ProyectoPLSQLSwing/
├── src/
│ ├── dao/ # Acceso a datos y procedimientos PL/SQL
│ ├── db/ # Clase de conexión a la BD
│ ├── model/ # Clases modelo (POJOs)
│ └── ui/ # Interfaz Swing (ventanas)
│
├── Wallet_TALLER/ # Carpeta Wallet (tnsnames.ora y certificados)
├── config.properties # Archivo de configuración de conexión
├── lib/ # Librerías externas (ojdbc11.jar, etc.)
└── README.md # Este archivo
````

---

## ⚙️ Configuración de `config.properties`

Este archivo se debe ubicar en el **classpath** (por ejemplo, `src/main/resources` o junto al JAR generado):

```properties
db.url=jdbc:oracle:thin:@taller_high
db.user=USUARIO
db.password=CONTRASEÑA
wallet.dir=./Wallet_TALLER
```
## 📌 wallet.dir debe apuntar al directorio real donde está tu wallet Oracle (el que contiene tnsnames.ora).

## 🔑 Requisitos previos

Tener instalado Java 17 o superior

Tener acceso a Oracle Autonomous Database con Wallet descargado.

Incluir el driver JDBC de Oracle en lib/ (por ejemplo: ojdbc11.jar).

Agregar el JAR al classpath al compilar/ejecutar.

## 🚀 Ejecución
## 🧪 Desde IntelliJ IDEA

Abre el proyecto.

Configura el SDK de Java 17+.

Asegúrate de tener el Wallet y config.properties correctamente configurados.

Ejecuta la clase:

ui.MainMenu

## 📦 Compilado a .jar

Construye el proyecto (Build → Build Artifacts → JAR en IntelliJ).

Copia junto al .jar:

la carpeta Wallet_TALLER

el archivo config.properties

las librerías necesarias (ojdbc11.jar si no está embebido).

Ejecuta:

java -jar ProyectoPLSQLSwing.jar

## 🪄 Características principales
 1. Procedimientos

Ejecutar procedimientos PL/SQL desde interfaz.

Ver mensajes DBMS_OUTPUT.

Log de ejecución en tabla de errores PL/SQL.

2. CRUD de Arriendos

Crear, listar, actualizar y eliminar arriendos usando el PACKAGE_CRUD_ARRIENDOS.

Validaciones y mensajes amigables.

3. Auditoría automática

Trigger TRG_AUDIT_ARRIENDO_AFTER_UPDATE registra cambios en fechas.

Visualización en la ventana de Auditoría.

## 🧪 Base de datos: objetos requeridos

PACKAGE_CRUD_ARRIENDOS

Procedimientos: P_GENERAR_HABERES_MENSUALES, P_DETECTAR_SOLAPAMIENTOS

Trigger: TRG_AUDIT_ARRIENDO_AFTER_UPDATE

Tabla: AUDIT_ARRIENDO

Tabla de logs de errores: LOG_ERRORES_PLSQL

Secuencia: SEQ_AUDIT_ARRIENDO


Este proyecto es de uso educativo.
Puedes modificarlo y adaptarlo según tus necesidades respetando las credenciales y datos sensibles.