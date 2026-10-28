
/*
 * ================================================================
 * CLASE: JpaUtil
 * PAQUETE: conexion
 * ================================================================
 *
 * OBJETIVO:
 *
 * Centralizar la inicialización y gestión de los recursos
 * principales de JPA (Jakarta Persistence).
 *
 * En nuestro proyecto anterior utilizábamos JDBC y teníamos
 * una clase llamada ConexionBD.
 *
 * Esa clase nos permitía obtener conexiones mediante:
 *
 *     Connection con = ConexionBD.obtenerConexion();
 *
 * Ahora, al incorporar Hibernate, necesitamos trabajar con
 * EntityManager en lugar de gestionar directamente las
 * conexiones JDBC.
 *
 * Utilizaremos:
 *
 *     EntityManager em = JpaUtil.abrir();
 *
 * IMPORTANTE:
 *
 * JpaUtil no sustituye ni elimina ConexionBD.
 *
 * Ambas clases pueden coexistir en nuestro proyecto:
 *
 *     ConexionBD -> Acceso mediante JDBC.
 *
 *     JpaUtil    -> Acceso mediante JPA/Hibernate.
 *
 * Hibernate seguirá utilizando JDBC internamente para
 * comunicarse con PostgreSQL.
 */

package conexion;

/*
 * ================================================================
 * IMPORTACIÓN DE EntityManager
 * ================================================================
 *
 * EntityManager es una interfaz de Jakarta Persistence.
 *
 * Su función es gestionar entidades Java persistentes.
 *
 * Podemos utilizarlo para:
 *
 *     em.persist(cliente);
 *         Registrar una nueva entidad.
 *
 *     em.find(ClienteEntidad.class, id);
 *         Buscar una entidad por su clave primaria.
 *
 *     em.remove(cliente);
 *         Eliminar una entidad gestionada.
 *
 *     em.createQuery(...);
 *         Crear consultas JPQL.
 *
 *     em.getTransaction();
 *         Obtener la transacción local.
 *
 *     em.close();
 *         Cerrar el contexto de persistencia.
 *
 * Un EntityManager mantiene un contexto de persistencia.
 *
 * Dentro de ese contexto, Hibernate puede realizar
 * el seguimiento de las entidades y detectar cambios.
 *
 * IMPORTANTE:
 *
 * EntityManager no es exactamente equivalente a Connection.
 *
 * Connection representa una conexión JDBC.
 *
 * EntityManager representa un contexto de persistencia y
 * permite trabajar con objetos Java.
 *
 * Hibernate obtiene conexiones JDBC cuando las necesita.
 */
import jakarta.persistence.EntityManager;

/*
 * ================================================================
 * IMPORTACIÓN DE EntityManagerFactory
 * ================================================================
 *
 * EntityManagerFactory es una interfaz que representa una
 * factoría de EntityManager.
 *
 * Una factoría es un objeto cuya responsabilidad consiste
 * en crear otros objetos.
 *
 * En nuestro caso:
 *
 *     EntityManagerFactory
 *              |
 *              | createEntityManager()
 *              v
 *         EntityManager
 *
 * La factoría contiene la infraestructura de persistencia
 * inicializada a partir de nuestra configuración.
 *
 * Durante su creación, Hibernate puede:
 *
 *     1. Leer persistence.xml.
 *     2. Localizar la unidad de persistencia.
 *     3. Analizar las clases anotadas con @Entity.
 *     4. Interpretar los mapeos de tablas y columnas.
 *     5. Preparar la configuración JDBC.
 *     6. Validar el esquema de PostgreSQL.
 *
 * Crear una EntityManagerFactory es una operación
 * relativamente costosa.
 *
 * Por eso, normalmente creamos una única factoría
 * para toda nuestra aplicación.
 *
 * IMPORTANTE:
 *
 * EntityManagerFactory está diseñada para poder compartirse
 * entre diferentes hilos.
 *
 * EntityManager, en cambio, no debe compartirse libremente
 * entre hilos.
 */
import jakarta.persistence.EntityManagerFactory;

/*
 * ================================================================
 * IMPORTACIÓN DE Persistence
 * ================================================================
 *
 * Persistence es una clase auxiliar de Jakarta Persistence.
 *
 * Proporciona métodos estáticos para inicializar
 * unidades de persistencia.
 *
 * El método que utilizaremos es:
 *
 *     Persistence.createEntityManagerFactory("tiendaPU");
 *
 * Este método:
 *
 *     1. Busca la configuración JPA disponible.
 *     2. Localiza la unidad llamada "tiendaPU".
 *     3. Selecciona el proveedor de persistencia.
 *     4. Inicializa la infraestructura necesaria.
 *     5. Devuelve una EntityManagerFactory.
 *
 * El nombre "tiendaPU" debe coincidir exactamente
 * con el definido en persistence.xml.
 */
import jakarta.persistence.Persistence;

/*
 * ================================================================
 * DECLARACIÓN DE LA CLASE JpaUtil
 * ================================================================
 *
 * public:
 *     Permite acceder a la clase desde otros paquetes.
 *
 * class:
 *     Declara una clase Java.
 *
 * JpaUtil:
 *     Es el nombre que hemos elegido para nuestra utilidad JPA.
 *
 * No es un nombre obligatorio de Jakarta Persistence.
 *
 * Podríamos haber utilizado:
 *
 *     GestorJPA
 *     ConexionORM
 *     JpaManager
 *
 * Utilizamos JpaUtil porque contiene métodos auxiliares
 * para crear y cerrar recursos JPA.
 *
 * Esta clase no representa una tabla de PostgreSQL.
 *
 * Por tanto, NO lleva la anotación @Entity.
 */
public class JpaUtil {

    /*
     * ============================================================
     * ATRIBUTO FACTORIA
     * ============================================================
     *
     * Declaramos:
     *
     *     private static final EntityManagerFactory FACTORIA
     *
     * Analicemos cada elemento:
     *
     * ------------------------------------------------------------
     * private
     * ------------------------------------------------------------
     *
     * Indica que el atributo solamente puede utilizarse
     * directamente desde el interior de JpaUtil.
     *
     * Esto impide que otras clases manipulen la referencia
     * a la factoría.
     *
     * Los DAO no necesitan acceder directamente a ella.
     *
     * Solamente necesitan llamar a:
     *
     *     JpaUtil.abrir();
     *
     * ------------------------------------------------------------
     * static
     * ------------------------------------------------------------
     *
     * Significa que el atributo pertenece a la clase JpaUtil,
     * y no a un objeto concreto de esa clase.
     *
     * Por tanto, no necesitamos crear:
     *
     *     JpaUtil util = new JpaUtil();
     *
     * Podemos acceder directamente a sus métodos estáticos:
     *
     *     JpaUtil.abrir();
     *
     * La factoría será compartida por las operaciones
     * que utilicen esta clase.
     *
     * ------------------------------------------------------------
     * final
     * ------------------------------------------------------------
     *
     * Indica que la referencia FACTORIA no podrá reasignarse
     * después de su inicialización.
     *
     * Por ejemplo, no podríamos hacer posteriormente:
     *
     *     FACTORIA = otraFactoria;
     *
     * IMPORTANTE:
     *
     * final NO significa que la factoría no pueda cambiar
     * internamente de estado.
     *
     * Por ejemplo, podremos cerrarla:
     *
     *     FACTORIA.close();
     *
     * Lo que no podemos hacer es reasignar la variable.
     *
     * ------------------------------------------------------------
     * EntityManagerFactory
     * ------------------------------------------------------------
     *
     * Es el tipo de dato de FACTORIA.
     *
     * Esta factoría será la encargada de crear los
     * EntityManager que utilizarán nuestros DAO.
     *
     * ------------------------------------------------------------
     * ¿POR QUÉ EL NOMBRE FACTORIA ESTÁ EN MAYÚSCULAS?
     * ------------------------------------------------------------
     *
     * En Java es habitual escribir en mayúsculas los nombres
     * de los atributos static final.
     *
     * Es una convención de estilo, no una obligación.
     *
     * Aunque aquí la referencia es final, la factoría
     * es un objeto que puede cerrarse.
     */
    private static final EntityManagerFactory FACTORIA =

            /*
             * ====================================================
             * INICIALIZACIÓN DE LA FACTORÍA
             * ====================================================
             *
             * Utilizamos:
             *
             * Persistence.createEntityManagerFactory("tiendaPU")
             *
             * Este método busca una unidad de persistencia
             * cuyo nombre sea "tiendaPU".
             *
             * En nuestro persistence.xml tenemos:
             *
             * <persistence-unit
             *     name="tiendaPU"
             *     transaction-type="RESOURCE_LOCAL">
             *
             * También tenemos configurados:
             *
             *     Proveedor:
             *         HibernatePersistenceProvider
             *
             *     Entidades:
             *         ClienteEntidad
             *         ProductoEntidad
             *
             *     URL:
             *         jdbc:postgresql://localhost:5432/tienda_ramiro
             *
             *     Usuario:
             *         tienda_app
             *
             *     Contraseña:
             *         La configurada en persistence.xml.
             *
             *     Validación:
             *         hibernate.hbm2ddl.auto = validate
             *
             * Hibernate utilizará esa información para
             * inicializar el sistema de persistencia.
             *
             * IMPORTANTE:
             *
             * Esta instrucción se ejecuta durante la
             * inicialización de la clase JpaUtil.
             *
             * No se ejecuta cada vez que llamamos a abrir().
             *
             * Por tanto, la factoría se crea una sola vez
             * por inicialización de la clase.
             *
             * Si la configuración es incorrecta, puede
             * producirse un error durante esta inicialización.
             */
            Persistence.createEntityManagerFactory("tiendaPU");

    /*
     * ============================================================
     * CONSTRUCTOR PRIVADO
     * ============================================================
     *
     * Declaramos un constructor sin parámetros:
     *
     *     private JpaUtil()
     *
     * ¿POR QUÉ ES PRIVATE?
     *
     * Porque no queremos que otras clases puedan crear
     * objetos JpaUtil.
     *
     * Esta clase solamente contiene:
     *
     *     - Un atributo estático FACTORIA.
     *     - Un método estático abrir().
     *     - Un método estático cerrar().
     *
     * No necesitamos instanciarla.
     *
     * Este diseño es habitual en las clases de utilidad.
     *
     * Por ejemplo, queremos permitir:
     *
     *     JpaUtil.abrir();
     *
     * Pero impedir:
     *
     *     new JpaUtil();
     *
     * El constructor está vacío porque no necesitamos
     * ejecutar ninguna inicialización adicional.
     *
     * La factoría ya se inicializa mediante el atributo
     * estático FACTORIA.
     */
    private JpaUtil() {

    }

    /*
     * ============================================================
     * MÉTODO abrir()
     * ============================================================
     *
     * OBJETIVO:
     *
     * Crear y devolver un nuevo EntityManager.
     *
     * Este EntityManager será utilizado por nuestros DAO
     * para consultar o modificar entidades.
     *
     * ------------------------------------------------------------
     * public
     * ------------------------------------------------------------
     *
     * Permite llamar al método desde otros paquetes.
     *
     * Por ejemplo:
     *
     *     dao.orm.ClienteDAOOrm
     *
     * ------------------------------------------------------------
     * static
     * ------------------------------------------------------------
     *
     * Permite llamar al método sin crear un objeto JpaUtil.
     *
     * Escribiremos:
     *
     *     JpaUtil.abrir();
     *
     * ------------------------------------------------------------
     * EntityManager
     * ------------------------------------------------------------
     *
     * Es el tipo de dato que devuelve el método.
     *
     * ------------------------------------------------------------
     * abrir()
     * ------------------------------------------------------------
     *
     * Es el nombre que hemos elegido.
     *
     * No es un método estándar de JPA.
     *
     * Es un método propio de nuestra clase JpaUtil.
     *
     * EJEMPLO:
     *
     *     EntityManager em = JpaUtil.abrir();
     *
     * A partir de ese momento podemos utilizar:
     *
     *     em.find(...);
     *     em.persist(...);
     *     em.createQuery(...);
     *
     * IMPORTANTE:
     *
     * Cada llamada a abrir() crea un nuevo EntityManager.
     *
     * Sin embargo, todos se crean a partir de la misma
     * EntityManagerFactory.
     */
    public static EntityManager abrir() {

        /*
         * ========================================================
         * createEntityManager()
         * ========================================================
         *
         * Es un método de EntityManagerFactory.
         *
         * Crea un nuevo EntityManager asociado a nuestra
         * unidad de persistencia.
         *
         * Cada EntityManager dispone de su propio contexto
         * de persistencia.
         *
         * Por ejemplo:
         *
         *     EntityManager em1 = JpaUtil.abrir();
         *     EntityManager em2 = JpaUtil.abrir();
         *
         * em1 y em2 son dos gestores diferentes.
         *
         * Ambos pueden trabajar sobre la misma base de datos,
         * pero no comparten automáticamente las mismas
         * entidades gestionadas en memoria.
         *
         * IMPORTANTE:
         *
         * Crear un EntityManager no significa necesariamente
         * abrir inmediatamente una conexión física nueva
         * con PostgreSQL.
         *
         * Hibernate puede obtener conexiones JDBC cuando
         * las necesita y liberarlas según su configuración.
         *
         * Por eso no debemos confundir:
         *
         *     EntityManager
         *
         * con:
         *
         *     Connection
         *
         * El EntityManager es una abstracción de mayor nivel.
         */
        return FACTORIA.createEntityManager();
    }

    /*
     * ============================================================
     * MÉTODO cerrar()
     * ============================================================
     *
     * OBJETIVO:
     *
     * Cerrar la EntityManagerFactory cuando la aplicación
     * ya no necesita utilizar JPA.
     *
     * IMPORTANTE:
     *
     * Este método NO está pensado para ejecutarse después
     * de cada consulta.
     *
     * Después de cada operación DAO debemos cerrar
     * el EntityManager utilizado.
     *
     * La factoría se cierra normalmente al finalizar
     * la aplicación.
     *
     * EJEMPLO:
     *
     *     JpaUtil.cerrar();
     *
     * Una vez cerrada la factoría, ya no podremos crear
     * nuevos EntityManager mediante esa instancia.
     */
    public static void cerrar() {

        /*
         * ========================================================
         * COMPROBACIÓN isOpen()
         * ========================================================
         *
         * FACTORIA.isOpen() devuelve un boolean.
         *
         * true:
         *     La factoría sigue abierta.
         *
         * false:
         *     La factoría ya está cerrada.
         *
         * Comprobamos su estado antes de intentar cerrarla.
         *
         * Esto evita realizar innecesariamente una operación
         * de cierre sobre una factoría ya cerrada.
         */
        if (FACTORIA.isOpen()) {

            /*
             * ====================================================
             * CIERRE DE LA FACTORÍA
             * ====================================================
             *
             * FACTORIA.close() libera los recursos asociados
             * a la EntityManagerFactory.
             *
             * Según la configuración, pueden incluir:
             *
             *     - Recursos internos del proveedor Hibernate.
             *     - Infraestructura de persistencia.
             *     - Recursos asociados a conexiones.
             *     - Cachés y otros servicios internos.
             *
             * Después de cerrar la factoría no debemos
             * intentar utilizarla para crear más gestores.
             *
             * En nuestra aplicación Swing podemos registrar
             * este cierre cuando finalice la JVM:
             *
             * Runtime.getRuntime().addShutdownHook(
             *     new Thread(JpaUtil::cerrar)
             * );
             *
             * De esa manera intentaremos liberar los recursos
             * al finalizar la aplicación.
             */
            FACTORIA.close();
        }
    }
}
