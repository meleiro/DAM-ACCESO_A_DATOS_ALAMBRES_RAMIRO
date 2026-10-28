
/*
 * ================================================================
 * CLASE: ClienteDAOOrm
 * PAQUETE: dao.orm
 * ================================================================
 *
 * OBJETIVO:
 *
 * Implementar las operaciones de acceso a los datos de clientes
 * utilizando ORM (Object-Relational Mapping).
 *
 * En nuestro proyecto anterior teníamos:
 *
 *     dao.ClienteDAO
 *
 * Esa clase utilizaba JDBC directamente:
 *
 *     Connection
 *     PreparedStatement
 *     ResultSet
 *
 * Ahora utilizaremos:
 *
 *     dao.orm.ClienteDAOOrm
 *
 * Esta nueva clase utiliza JPA, con Hibernate como proveedor.
 *
 * La principal diferencia es que ahora trabajaremos con
 * objetos ClienteEntidad y no directamente con filas SQL.
 *
 * IMPORTANTE:
 *
 * El patrón DAO sigue siendo el mismo.
 *
 * Lo que cambia es la tecnología utilizada para acceder
 * a los datos.
 *
 * Ambas implementaciones pueden trabajar con la misma tabla
 * "cliente" de nuestra base de datos PostgreSQL.
 */

package dao.orm;

/*
 * ================================================================
 * IMPORTACIÓN DE JpaUtil
 * ================================================================
 *
 * JpaUtil es la clase auxiliar que hemos creado en el paquete
 * conexion para centralizar la inicialización de JPA.
 *
 * En el proyecto JDBC utilizábamos:
 *
 *     ConexionBD.obtenerConexion();
 *
 * Ese método nos devolvía un objeto Connection.
 *
 * Ahora utilizaremos:
 *
 *     JpaUtil.abrir();
 *
 * Este método nos devolverá un EntityManager.
 *
 * JpaUtil utiliza internamente una EntityManagerFactory
 * creada a partir de nuestra unidad de persistencia:
 *
 *     tiendaPU
 *
 * Esta unidad está configurada en:
 *
 *     src/main/resources/META-INF/persistence.xml
 */
import conexion.JpaUtil;

/*
 * ================================================================
 * IMPORTACIÓN DE EntityManager
 * ================================================================
 *
 * EntityManager es una interfaz definida por Jakarta Persistence.
 *
 * Es uno de los elementos más importantes de JPA.
 *
 * Su responsabilidad es gestionar entidades persistentes.
 *
 * Nos permite realizar operaciones como:
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
 *         Crear una consulta JPQL.
 *
 *     em.getTransaction();
 *         Obtener una transacción local.
 *
 *     em.close();
 *         Cerrar el contexto de persistencia.
 *
 * IMPORTANTE:
 *
 * EntityManager no es exactamente lo mismo que Connection.
 *
 * Connection representa una conexión JDBC.
 *
 * EntityManager gestiona un contexto de persistencia, que
 * permite realizar el seguimiento de objetos Java y sus cambios.
 *
 * Hibernate utiliza conexiones JDBC internamente cuando necesita
 * comunicarse con PostgreSQL.
 */
import jakarta.persistence.EntityManager;

/*
 * ================================================================
 * IMPORTACIÓN DE ClienteEntidad
 * ================================================================
 *
 * ClienteEntidad es nuestra clase Java anotada con:
 *
 *     @Entity
 *     @Table(name = "cliente")
 *
 * Representa los registros de la tabla "cliente".
 *
 * Antes, con JDBC, utilizábamos:
 *
 *     modelo.Cliente
 *
 * Ahora, con Hibernate, utilizaremos:
 *
 *     modelo.orm.ClienteEntidad
 *
 * Cada objeto ClienteEntidad representará los datos
 * de un cliente recuperado desde PostgreSQL.
 */
import modelo.orm.ClienteEntidad;

/*
 * ================================================================
 * IMPORTACIÓN DE List
 * ================================================================
 *
 * List es una interfaz del framework de colecciones de Java.
 *
 * Representa una colección ordenada de elementos.
 *
 * En nuestro caso utilizaremos:
 *
 *     List<ClienteEntidad>
 *
 * Es decir, una lista de objetos ClienteEntidad.
 *
 * Esta lista podrá contener:
 *
 *     - Ningún cliente.
 *     - Un cliente.
 *     - Varios clientes.
 *
 * El orden de los resultados vendrá determinado por
 * nuestra consulta JPQL, que utiliza ORDER BY c.id.
 */
import java.util.List;

/*
 * ================================================================
 * DECLARACIÓN DE LA CLASE DAO
 * ================================================================
 *
 * DAO significa Data Access Object.
 *
 * Es un patrón de diseño cuya finalidad es separar
 * el acceso a los datos del resto de la aplicación.
 *
 * La interfaz Swing no necesita saber:
 *
 *     - Cómo se establece la conexión.
 *     - Cómo se construyen las consultas.
 *     - Cómo se recuperan los registros.
 *     - Cómo se convierten los resultados en objetos.
 *
 * Simplemente podrá solicitar:
 *
 *     clienteDAO.listar();
 *
 * La clase DAO se encargará de realizar la operación.
 *
 * Esto facilita el mantenimiento y permite sustituir
 * JDBC por Hibernate sin reconstruir toda la aplicación.
 */
public class ClienteDAOOrm {

    /*
     * ============================================================
     * MÉTODO listar()
     * ============================================================
     *
     * OBJETIVO:
     *
     * Recuperar todos los clientes almacenados en PostgreSQL
     * y devolverlos como una lista de objetos ClienteEntidad.
     *
     * DECLARACIÓN:
     *
     * public:
     *     Permite llamar al método desde otras clases,
     *     como VentanaPrincipalORM.
     *
     * List<ClienteEntidad>:
     *     Es el tipo de dato que devuelve el método.
     *
     *     No devuelve un ResultSet como ocurría en JDBC.
     *
     *     Devuelve una lista de objetos Java.
     *
     * listar():
     *     Es el nombre del método.
     *
     *     No recibe parámetros porque queremos recuperar
     *     todos los clientes, sin aplicar filtros.
     *
     * EJEMPLO DE USO:
     *
     *     ClienteDAOOrm dao = new ClienteDAOOrm();
     *
     *     List<ClienteEntidad> clientes = dao.listar();
     *
     *     for (ClienteEntidad cliente : clientes) {
     *         System.out.println(cliente.getNombre());
     *     }
     */
    public List<ClienteEntidad> listar() {

        /*
         * ========================================================
         * PASO 1: OBTENER UN EntityManager
         * ========================================================
         *
         * Llamamos al método estático abrir() de JpaUtil.
         *
         * Este método devuelve un EntityManager.
         *
         * Recordemos que JpaUtil contiene una instrucción
         * similar a:
         *
         *     return FACTORIA.createEntityManager();
         *
         * Donde FACTORIA es una EntityManagerFactory.
         *
         * IMPORTANTE:
         *
         * No estamos creando una nueva EntityManagerFactory
         * cada vez que llamamos a listar().
         *
         * Estamos creando un nuevo EntityManager utilizando
         * la factoría que ya tiene preparada JpaUtil.
         *
         * Esto es importante porque la factoría es un objeto
         * relativamente costoso de inicializar.
         *
         * El EntityManager será nuestro contexto de
         * persistencia durante esta operación.
         */
        EntityManager em = JpaUtil.abrir();

        /*
         * ========================================================
         * PASO 2: BLOQUE try / finally
         * ========================================================
         *
         * Utilizamos try para ejecutar la consulta.
         *
         * Utilizamos finally para garantizar que el
         * EntityManager se cierre al terminar.
         *
         * El bloque finally se ejecuta tanto si la consulta
         * termina correctamente como si se produce una excepción
         * durante su ejecución.
         *
         * Por ejemplo, si PostgreSQL deja de estar disponible,
         * la consulta puede lanzar una excepción.
         *
         * Aunque ocurra ese error, debemos liberar los recursos
         * asociados al EntityManager.
         *
         * IMPORTANTE:
         *
         * No utilizamos catch porque aquí hemos decidido
         * no gestionar la excepción dentro del DAO.
         *
         * Si se produce un error de persistencia, se propagará
         * al método que haya llamado a listar().
         *
         * La interfaz Swing podrá capturar esa excepción
         * y mostrar un mensaje al usuario.
         */
        try {

            /*
             * ====================================================
             * PASO 3: CREAR UNA CONSULTA JPQL
             * ====================================================
             *
             * Utilizamos:
             *
             *     em.createQuery(...)
             *
             * Este método permite crear consultas JPQL.
             *
             * JPQL significa:
             *
             *     Jakarta Persistence Query Language
             *
             * Es un lenguaje de consultas definido por JPA.
             *
             * Su sintaxis se parece a SQL, pero existe
             * una diferencia fundamental:
             *
             * SQL trabaja con tablas y columnas.
             *
             * JPQL trabaja con entidades y atributos Java.
             *
             * NUESTRA CONSULTA:
             *
             *     SELECT c
             *     FROM ClienteEntidad c
             *     ORDER BY c.id
             *
             * DESCOMPOSICIÓN:
             *
             * SELECT c:
             *     Indica que queremos recuperar las entidades
             *     representadas por el alias c.
             *
             * FROM ClienteEntidad:
             *     Indica la entidad Java que queremos consultar.
             *
             *     No escribimos el nombre de la tabla "cliente".
             *
             *     Escribimos ClienteEntidad porque es el nombre
             *     de nuestra entidad JPA.
             *
             * c:
             *     Es un alias que asignamos a ClienteEntidad.
             *
             *     Podríamos haber utilizado otro nombre:
             *
             *         SELECT cliente
             *         FROM ClienteEntidad cliente
             *
             *     Pero utilizamos c por comodidad.
             *
             * ORDER BY c.id:
             *     Ordena los clientes por su identificador.
             *
             *     Al no indicar DESC, el orden es ascendente
             *     por defecto.
             *
             *     Por ejemplo:
             *
             *         ID 1
             *         ID 2
             *         ID 3
             *
             * Si quisiéramos el orden inverso:
             *
             *     ORDER BY c.id DESC
             *
             * ====================================================
             * SEGUNDO PARÁMETRO: ClienteEntidad.class
             * ====================================================
             *
             * ClienteEntidad.class representa el objeto Class
             * correspondiente al tipo ClienteEntidad.
             *
             * Indica a JPA qué tipo de resultado esperamos.
             *
             * De esta manera creamos una consulta tipada.
             *
             * Su resultado será una lista de ClienteEntidad.
             *
             * Esto proporciona seguridad de tipos y evita
             * tener que convertir manualmente los resultados.
             *
             * ====================================================
             * getResultList()
             * ====================================================
             *
             * Ejecuta la consulta y recupera sus resultados.
             *
             * Devuelve:
             *
             *     List<ClienteEntidad>
             *
             * Hibernate se encarga de:
             *
             *     1. Interpretar la consulta JPQL.
             *
             *     2. Traducirla a SQL para PostgreSQL.
             *
             *     3. Ejecutarla mediante JDBC.
             *
             *     4. Recuperar las filas devueltas.
             *
             *     5. Crear o recuperar los objetos
             *        ClienteEntidad correspondientes.
             *
             *     6. Cargar sus atributos a partir
             *        de los valores de las columnas.
             *
             *     7. Devolver la lista de entidades.
             *
             * IMPORTANTE:
             *
             * Si no existen clientes, getResultList()
             * devuelve una lista vacía.
             *
             * No devuelve null simplemente porque
             * la consulta no haya encontrado registros.
             *
             * ====================================================
             * return
             * ====================================================
             *
             * Devuelve la lista al código que llamó al método.
             *
             * Aunque ejecutemos return dentro del try,
             * Java ejecutará primero el bloque finally.
             *
             * Por tanto:
             *
             *     1. Se obtiene la lista.
             *     2. Se ejecuta finally.
             *     3. Se cierra el EntityManager.
             *     4. Se devuelve la lista.
             *
             * Los objetos ClienteEntidad seguirán existiendo
             * después del cierre del EntityManager.
             *
             * Sin embargo, ya no estarán gestionados por
             * ese contexto de persistencia.
             *
             * Pasarán al estado detached (separados).
             *
             * Sus atributos básicos ya cargados podrán
             * seguir consultándose normalmente.
             */
            return em.createQuery(
                    "SELECT c FROM ClienteEntidad c ORDER BY c.id",
                    ClienteEntidad.class
            ).getResultList();

        } finally {

            /*
             * ====================================================
             * PASO 4: CERRAR EL EntityManager
             * ====================================================
             *
             * Cerramos el EntityManager utilizado para la consulta.
             *
             * Esto libera el contexto de persistencia y
             * los recursos asociados a él.
             *
             * IMPORTANTE:
             *
             * Cerrar un EntityManager no significa necesariamente
             * cerrar de forma definitiva una conexión física
             * con PostgreSQL.
             *
             * Hibernate puede utilizar un pool de conexiones,
             * dependiendo de su configuración.
             *
             * Tampoco significa cerrar la EntityManagerFactory.
             *
             * La factoría permanece disponible para que otras
             * operaciones puedan crear nuevos EntityManager.
             *
             * Ejemplo:
             *
             *     dao.listar();
             *
             *     dao.buscar(5);
             *
             *     dao.insertar(cliente);
             *
             * Cada operación puede obtener su propio
             * EntityManager a través de JpaUtil.
             *
             * ¿POR QUÉ EN FINALLY?
             *
             * Porque queremos garantizar su cierre incluso
             * si la consulta falla.
             *
             * Esto evita mantener innecesariamente abiertos
             * contextos de persistencia y recursos asociados.
             */
            em.close();
        }
    }
}
