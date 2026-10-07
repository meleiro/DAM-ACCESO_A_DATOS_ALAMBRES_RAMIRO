package conexion;


/*
 * ================================================================
 * IMPORTACIONES JDBC
 * ================================================================
 *
 * Las clases que utilizaremos aquí pertenecen al paquete:
 *
 *      java.sql
 *
 * Este paquete forma parte de Java y contiene las principales
 * interfaces y clases de JDBC.
 *
 *
 * JDBC significa:
 *
 *      Java Database Connectivity
 *
 * y es la API estándar de Java para comunicarnos con bases
 * de datos relacionales.
 */


/*
 * Connection representa una CONEXIÓN abierta entre nuestra
 * aplicación Java y la base de datos.
 *
 * Podemos imaginarla como un "canal de comunicación":
 *
 *
 *      APLICACIÓN JAVA
 *            |
 *            |
 *       Connection
 *            |
 *            |
 *            v
 *       PostgreSQL
 *
 *
 * A través de una Connection podremos posteriormente:
 *
 *      - preparar sentencias SQL;
 *      - ejecutar consultas;
 *      - realizar INSERT;
 *      - realizar UPDATE;
 *      - realizar DELETE;
 *      - trabajar con transacciones;
 *      - hacer commit;
 *      - hacer rollback;
 *      - etc.
 */
import java.sql.Connection;


/*
 * DriverManager es una clase de JDBC encargada de gestionar
 * los drivers JDBC y ayudarnos a establecer conexiones.
 *
 * Nosotros le proporcionaremos:
 *
 *      URL
 *      usuario
 *      contraseña
 *
 * y DriverManager intentará establecer la conexión
 * correspondiente.
 *
 *
 * Conceptualmente:
 *
 *      DriverManager
 *            |
 *            | getConnection(...)
 *            v
 *      Driver PostgreSQL
 *            |
 *            v
 *       PostgreSQL
 *
 *
 * IMPORTANTE:
 *
 * El driver específico de PostgreSQL no forma parte de Java.
 *
 * Lo hemos incorporado al proyecto mediante Maven,
 * utilizando la dependencia de PostgreSQL que tenemos
 * declarada en el pom.xml.
 */
import java.sql.DriverManager;


/*
 * SQLException representa errores relacionados con JDBC
 * y con las operaciones realizadas contra la base de datos.
 *
 * Por ejemplo, puede producirse una SQLException si:
 *
 *      - PostgreSQL no está arrancado;
 *      - el puerto es incorrecto;
 *      - la base de datos no existe;
 *      - el usuario no existe;
 *      - la contraseña es incorrecta;
 *      - no tenemos permisos;
 *      - existe un problema durante una operación SQL;
 *      - se pierde la conexión;
 *      - etc.
 *
 *
 * SQLException es una CHECKED EXCEPTION.
 *
 * Eso significa que Java nos obliga a:
 *
 *      1. capturarla con try-catch
 *
 * o:
 *
 *      2. propagarla mediante throws
 *
 * En nuestro método obtenerConexion() hemos elegido
 * inicialmente la segunda posibilidad.
 */
import java.sql.SQLException;



/**
 * ================================================================
 * CLASE ConexionBD
 * ================================================================
 *
 * Esta clase centraliza la creación de conexiones JDBC.
 *
 *
 * La idea es evitar tener que repetir en todas las clases:
 *
 *      URL
 *      usuario
 *      contraseña
 *      DriverManager.getConnection(...)
 *
 *
 * Sin esta clase podríamos acabar escribiendo en ClienteDAO:
 *
 *      DriverManager.getConnection(...)
 *
 * después en ProductoDAO:
 *
 *      DriverManager.getConnection(...)
 *
 * y posteriormente en cualquier otro DAO:
 *
 *      DriverManager.getConnection(...)
 *
 *
 * Estaríamos repitiendo la configuración de la conexión
 * por toda nuestra aplicación.
 *
 *
 * En lugar de eso tendremos:
 *
 *
 *                    ConexionBD
 *                        |
 *               obtenerConexion()
 *                        |
 *           +------------+-------------+
 *           |                          |
 *           v                          v
 *      ClienteDAO                 ProductoDAO
 *
 *
 * Tanto ClienteDAO como ProductoDAO pedirán una conexión
 * utilizando:
 *
 *      ConexionBD.obtenerConexion()
 *
 *
 * ================================================================
 * CONFIGURACIÓN UTILIZADA EN CLASE
 * ================================================================
 *
 * Los valores coinciden con la BBDD que estamos preparando
 * en clase:
 *
 *      servidor:      localhost
 *      puerto:        5432
 *      base de datos: tienda_ramiro
 *      usuario:       tienda_app
 *      contraseña:    tienda124
 *
 *
 * IMPORTANTE:
 *
 * En un proyecto profesional las credenciales NO deberían
 * estar escritas directamente en el código fuente.
 *
 * Es decir, esto es apropiado para nuestro ejercicio didáctico:
 *
 *      PASSWORD = "tienda124";
 *
 * pero no sería una buena práctica en una aplicación real.
 *
 * Profesionalmente podríamos utilizar, por ejemplo:
 *
 *      - variables de entorno;
 *      - ficheros externos de configuración;
 *      - gestores de secretos;
 *      - mecanismos proporcionados por el entorno de despliegue.
 *
 *
 * Por ahora lo hacemos así porque nuestro objetivo es comprender
 * primero cómo funciona JDBC.
 */
public final class ConexionBD {



    /*
     * ============================================================
     * URL JDBC
     * ============================================================
     *
     * Esta constante contiene la dirección que JDBC utilizará
     * para localizar nuestra base de datos.
     *
     *
     * Tenemos:
     *
     *      jdbc:postgresql://localhost:5432/tienda_ramiro
     *
     *
     * Vamos a dividirla:
     *
     *
     *      jdbc:postgresql://localhost:5432/tienda_ramiro
     *      ──── ──────────   ───────── ──── ─────────────
     *        │       │           │       │         │
     *        │       │           │       │         │
     *       JDBC  PostgreSQL   servidor puerto    BBDD
     *
     *
     * ------------------------------------------------------------
     * jdbc:
     * ------------------------------------------------------------
     *
     * Indica que estamos utilizando JDBC.
     *
     *
     * ------------------------------------------------------------
     * postgresql:
     * ------------------------------------------------------------
     *
     * Indica el tipo de base de datos con el que queremos
     * comunicarnos.
     *
     * En nuestro caso:
     *
     *      PostgreSQL
     *
     *
     * ------------------------------------------------------------
     * localhost
     * ------------------------------------------------------------
     *
     * Indica dónde se encuentra el servidor PostgreSQL.
     *
     * localhost significa:
     *
     *      "este mismo ordenador"
     *
     *
     * Es decir:
     *
     *      aplicación Java
     *            |
     *            v
     *      mismo ordenador
     *            |
     *            v
     *       PostgreSQL
     *
     *
     * Si PostgreSQL estuviera instalado en otro servidor,
     * aquí podríamos tener una dirección IP o un nombre
     * de servidor.
     *
     *
     * ------------------------------------------------------------
     * 5432
     * ------------------------------------------------------------
     *
     * Es el puerto en el que está escuchando PostgreSQL.
     *
     * 5432 es el puerto habitual/predeterminado de PostgreSQL.
     *
     *
     * ------------------------------------------------------------
     * tienda_ramiro
     * ------------------------------------------------------------
     *
     * Es el nombre de la BASE DE DATOS concreta a la que
     * queremos conectarnos.
     *
     *
     * Por tanto, toda la URL significa aproximadamente:
     *
     * "Quiero utilizar JDBC para conectarme a PostgreSQL,
     *  que está en mi ordenador, escuchando en el puerto 5432,
     *  y quiero acceder a la base de datos tienda_ramiro."
     */
    private static final String URL =
            "jdbc:postgresql://localhost:5432/tienda_ramiro";



    /*
     * ============================================================
     * USUARIO
     * ============================================================
     *
     * Usuario de PostgreSQL con el que nuestra aplicación
     * intentará autenticarse.
     *
     * Nosotros hemos creado:
     *
     *      tienda_app
     *
     * específicamente para que nuestra aplicación pueda
     * trabajar con la base de datos.
     *
     *
     * Esto es preferible didácticamente a utilizar siempre:
     *
     *      postgres
     *
     * porque "postgres" suele ser un usuario administrativo.
     *
     * Nuestra aplicación debería trabajar con un usuario
     * que tenga únicamente los permisos que necesite.
     */
    private static final String USUARIO =
            "tienda_app";



    /*
     * ============================================================
     * CONTRASEÑA
     * ============================================================
     *
     * Contraseña correspondiente al usuario:
     *
     *      tienda_app
     *
     *
     * PostgreSQL comprobará:
     *
     *      usuario     -> tienda_app
     *      contraseña  -> tienda124
     *
     * antes de permitirnos establecer la conexión.
     *
     *
     * Repetimos:
     *
     * Guardarla directamente en el código es una decisión
     * DIDÁCTICA para simplificar nuestro primer proyecto JDBC.
     *
     * No sería la solución apropiada para producción.
     */
    private static final String PASSWORD =
            "tienda124";



    /*
     * ============================================================
     * ¿POR QUÉ static final?
     * ============================================================
     *
     * Las tres variables anteriores están declaradas:
     *
     *      private static final
     *
     *
     * Vamos a analizarlo.
     *
     *
     * private
     * ------------------------------------------------------------
     *
     * Solamente pueden utilizarse directamente desde
     * esta propia clase.
     *
     *
     * static
     * ------------------------------------------------------------
     *
     * Pertenecen a la CLASE y no a un objeto concreto.
     *
     * No necesitamos crear:
     *
     *      new ConexionBD()
     *
     * para disponer de ellas.
     *
     *
     * final
     * ------------------------------------------------------------
     *
     * Una vez asignado su valor, la referencia no puede
     * ser reasignada.
     *
     *
     * Por eso utilizamos nombres en MAYÚSCULAS:
     *
     *      URL
     *      USUARIO
     *      PASSWORD
     *
     * siguiendo la convención habitual de Java para constantes.
     */



    /*
     * ============================================================
     * CONSTRUCTOR PRIVADO
     * ============================================================
     *
     * Este constructor puede resultar extraño:
     *
     *      private ConexionBD() {
     *      }
     *
     *
     * ¿Por qué tenemos un constructor si no hace nada?
     *
     * Precisamente porque queremos IMPEDIR que otras clases
     * creen objetos ConexionBD.
     *
     *
     * No queremos:
     *
     *      ConexionBD conexionBD =
     *              new ConexionBD();
     *
     *
     * Nuestra clase no necesita objetos.
     *
     * Solamente proporciona un método estático:
     *
     *      obtenerConexion()
     *
     *
     * Como el constructor es private, desde otra clase
     * no podremos hacer:
     *
     *      new ConexionBD()
     *
     *
     * Esto deja claro que ConexionBD está diseñada como una
     * clase de utilidad para centralizar la conexión.
     */
    private ConexionBD() {

    }



    /**
     * ============================================================
     * MÉTODO obtenerConexion()
     * ============================================================
     *
     * Este es el método fundamental de esta clase.
     *
     *
     * Su responsabilidad es:
     *
     *      CREAR Y DEVOLVER UNA CONEXIÓN CON POSTGRESQL
     *
     *
     * Por eso su tipo de retorno es:
     *
     *      Connection
     *
     *
     * ------------------------------------------------------------
     * ¿POR QUÉ ES static?
     * ------------------------------------------------------------
     *
     * Porque queremos poder utilizarlo directamente mediante:
     *
     *      ConexionBD.obtenerConexion()
     *
     *
     * sin tener que hacer previamente:
     *
     *      new ConexionBD()
     *
     *
     * Esto encaja con el constructor privado que acabamos
     * de explicar.
     *
     *
     * ------------------------------------------------------------
     * ¿POR QUÉ throws SQLException?
     * ------------------------------------------------------------
     *
     * Porque intentar establecer una conexión puede fallar.
     *
     * Por ejemplo:
     *
     *      PostgreSQL apagado
     *
     *      contraseña incorrecta
     *
     *      usuario incorrecto
     *
     *      base de datos inexistente
     *
     *      puerto incorrecto
     *
     *      servidor inaccesible
     *
     *
     * DriverManager.getConnection(...) puede lanzar:
     *
     *      SQLException
     *
     *
     * En lugar de capturarla aquí, hemos decidido PROPAGARLA.
     *
     * Es decir:
     *
     *      obtenerConexion()
     *
     * comunica al método que lo llame:
     *
     * "Intentaré darte una Connection, pero debes saber que
     *  puede producirse una SQLException."
     *
     *
     * Más adelante nuestro DAO podrá hacer:
     *
     *      Connection con =
     *          ConexionBD.obtenerConexion();
     *
     * y esa SQLException continuará propagándose hasta que
     * decidamos dónde queremos tratarla.
     *
     *
     * @return una conexión JDBC abierta con PostgreSQL.
     *
     * @throws SQLException si no es posible establecer
     *                      la conexión con la base de datos.
     */
    public static Connection obtenerConexion()
            throws SQLException {


        /*
         * ========================================================
         * DriverManager.getConnection(...)
         * ========================================================
         *
         * Aquí ocurre realmente el intento de conexión.
         *
         *
         * Le proporcionamos tres datos:
         *
         *      URL
         *      USUARIO
         *      PASSWORD
         *
         *
         * Es decir:
         *
         *      DriverManager.getConnection(
         *              URL,
         *              USUARIO,
         *              PASSWORD
         *      );
         *
         *
         * Conceptualmente ocurre:
         *
         *
         *              APLICACIÓN JAVA
         *                    |
         *                    |
         *                    v
         *              DriverManager
         *                    |
         *                    | utiliza
         *                    v
         *            Driver PostgreSQL
         *                    |
         *                    | red / protocolo PostgreSQL
         *                    v
         *              PostgreSQL
         *                    |
         *             autenticación
         *                    |
         *              +-----+-----+
         *              |           |
         *              v           v
         *             OK         ERROR
         *              |           |
         *              v           v
         *         Connection   SQLException
         *
         *
         * Si todo funciona correctamente:
         *
         *      getConnection(...)
         *
         * devuelve un objeto que implementa:
         *
         *      Connection
         *
         *
         * Como nuestro método también devuelve Connection,
         * simplemente utilizamos:
         *
         *      return
         *
         *
         * para entregárselo al método que nos llamó.
         */
        return DriverManager.getConnection(
                URL,
                USUARIO,
                PASSWORD
        );
    }

}