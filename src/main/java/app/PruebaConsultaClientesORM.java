
package app;

/*
 * ============================================================
 * IMPORTACIÓN DE JpaUtil
 * ============================================================
 *
 * JpaUtil es nuestra clase auxiliar para trabajar con JPA.
 *
 * Centraliza la creación y el cierre de la factoría:
 *
 *     EntityManagerFactory
 *
 * La factoría se configura mediante el archivo:
 *
 *     src/main/resources/META-INF/persistence.xml
 *
 * En nuestro proyecto, la unidad de persistencia se llama:
 *
 *     tiendaPU
 *
 * Utilizaremos dos métodos:
 *
 * JpaUtil.abrir():
 *     Crea y devuelve un EntityManager.
 *
 * JpaUtil.cerrar():
 *     Cierra la EntityManagerFactory compartida.
 *
 * IMPORTANTE:
 *
 * La EntityManagerFactory es un objeto relativamente
 * costoso de inicializar.
 *
 * Por eso se crea una vez y se reutiliza para obtener
 * diferentes EntityManager.
 */
import conexion.JpaUtil;

/*
 * ============================================================
 * IMPORTACIÓN DE EntityManager
 * ============================================================
 *
 * EntityManager es una interfaz de Jakarta Persistence.
 *
 * Representa un contexto de persistencia.
 *
 * Permite realizar operaciones como:
 *
 *     em.find()
 *     em.persist()
 *     em.remove()
 *     em.createQuery()
 *
 * En esta clase utilizaremos createQuery()
 * para ejecutar una consulta JPQL.
 *
 * Hibernate implementa EntityManager y utiliza
 * JDBC internamente para comunicarse con PostgreSQL.
 */
import jakarta.persistence.EntityManager;

/*
 * ============================================================
 * IMPORTACIÓN DE ClienteEntidad
 * ============================================================
 *
 * ClienteEntidad es nuestra entidad JPA.
 *
 * Representa los registros de la tabla cliente.
 *
 * Está anotada con:
 *
 *     @Entity
 *     @Table(name = "cliente")
 *
 * Sus atributos representan las columnas:
 *
 *     id
 *     nombre
 *     email
 *     activo
 *
 * Hibernate se encarga de convertir las filas
 * recuperadas de PostgreSQL en objetos ClienteEntidad.
 */
import modelo.orm.ClienteEntidad;

/*
 * ============================================================
 * IMPORTACIÓN DE List
 * ============================================================
 *
 * List es una interfaz de colecciones de Java.
 *
 * Permite almacenar varios elementos de un mismo tipo.
 *
 * En nuestro caso:
 *
 *     List<ClienteEntidad>
 *
 * representa una lista de objetos ClienteEntidad.
 *
 * Si la consulta no encuentra registros,
 * obtendremos una lista vacía.
 */
import java.util.List;

/**
 * ============================================================
 * CLASE PruebaConsultaClientesORM
 * ============================================================
 *
 * OBJETIVO:
 *
 * Ejecutar una consulta JPQL directamente mediante
 * un EntityManager y mostrar los clientes recuperados.
 *
 * Esta prueba nos permite comprobar:
 *
 * 1. Que JpaUtil puede crear un EntityManager.
 *
 * 2. Que Hibernate reconoce ClienteEntidad.
 *
 * 3. Que la consulta JPQL es válida.
 *
 * 4. Que Hibernate puede consultar PostgreSQL.
 *
 * 5. Que los resultados se transforman en objetos Java.
 *
 * 6. Que podemos acceder a los atributos mediante getters.
 *
 * DIFERENCIA CON PruebaConexionORM:
 *
 * PruebaConexionORM:
 *
 *     Ejecutaba SQL nativo:
 *
 *         SELECT 1
 *
 *     Solamente comprobaba la ejecución de una consulta.
 *
 * PruebaConsultaClientesORM:
 *
 *     Ejecuta JPQL:
 *
 *         SELECT c FROM ClienteEntidad c ORDER BY c.id
 *
 *     Comprueba también el funcionamiento del mapeo ORM.
 */
public class PruebaConsultaClientesORM {

    /*
     * ========================================================
     * MÉTODO main()
     * ========================================================
     *
     * Es el punto de entrada de esta aplicación de consola.
     *
     * public:
     *     Permite acceder al método desde fuera de la clase.
     *
     * static:
     *     Permite ejecutarlo sin crear un objeto de
     *     PruebaConsultaClientesORM.
     *
     * void:
     *     Indica que no devuelve ningún valor.
     *
     * String[] args:
     *     Permite recibir argumentos desde la consola.
     *
     * En esta prueba no utilizamos esos argumentos.
     */
    public static void main(String[] args) {

        /*
         * ====================================================
         * PASO 1: DECLARAR EL EntityManager
         * ====================================================
         *
         * Declaramos una variable llamada em.
         *
         * Su tipo es EntityManager.
         *
         * Inicialmente le asignamos null porque todavía
         * no hemos creado el gestor de persistencia.
         *
         * ¿POR QUÉ FUERA DEL TRY?
         *
         * Porque necesitaremos acceder a la variable
         * desde el bloque finally para cerrarla.
         *
         * Si la declaramos dentro del try, su ámbito
         * no permitiría utilizarla desde el finally.
         */
        EntityManager em = null;

        /*
         * ====================================================
         * PASO 2: BLOQUE try
         * ====================================================
         *
         * Introducimos aquí las operaciones que pueden
         * producir excepciones.
         *
         * Por ejemplo:
         *
         * - PostgreSQL no está disponible.
         * - Las credenciales son incorrectas.
         * - La entidad no está registrada correctamente.
         * - La consulta JPQL tiene un error.
         * - La tabla cliente no existe.
         *
         * Si se produce una excepción compatible con
         * Exception, se ejecutará el bloque catch.
         *
         * Finalmente, intentaremos liberar los recursos.
         */
        try {

            /*
             * =================================================
             * PASO 3: CREAR EL EntityManager
             * =================================================
             *
             * Llamamos a:
             *
             *     JpaUtil.abrir()
             *
             * Internamente, este método utiliza:
             *
             *     EntityManagerFactory.createEntityManager()
             *
             * El EntityManager obtenido será el contexto
             * de persistencia utilizado durante la consulta.
             *
             * A través de él podremos ejecutar JPQL
             * y recuperar entidades gestionadas.
             */
            em = JpaUtil.abrir();

            /*
             * =================================================
             * PASO 4: CREAR Y EJECUTAR LA CONSULTA JPQL
             * =================================================
             *
             * Esta es la instrucción principal de la clase:
             *
             * em.createQuery(
             *     "SELECT c FROM ClienteEntidad c ORDER BY c.id",
             *     ClienteEntidad.class
             * ).getResultList();
             *
             * Vamos a analizarla por partes.
             *
             * -------------------------------------------------
             * ¿QUÉ ES JPQL?
             * -------------------------------------------------
             *
             * JPQL significa:
             *
             * Jakarta Persistence Query Language.
             *
             * Es el lenguaje de consultas definido por JPA.
             *
             * Su sintaxis es similar a SQL, pero trabaja
             * con entidades y atributos Java.
             *
             * SQL:
             *
             *     SELECT *
             *     FROM cliente
             *     ORDER BY id;
             *
             * JPQL:
             *
             *     SELECT c
             *     FROM ClienteEntidad c
             *     ORDER BY c.id
             *
             * En SQL escribimos el nombre de la tabla.
             *
             * En JPQL escribimos el nombre de la entidad.
             *
             * -------------------------------------------------
             * SELECT c
             * -------------------------------------------------
             *
             * Indica que queremos recuperar las entidades
             * representadas por el alias c.
             *
             * No estamos seleccionando únicamente
             * una columna de PostgreSQL.
             *
             * Estamos solicitando objetos ClienteEntidad.
             *
             * -------------------------------------------------
             * FROM ClienteEntidad c
             * -------------------------------------------------
             *
             * ClienteEntidad:
             *     Es el nombre de nuestra entidad Java.
             *
             * c:
             *     Es un alias que utilizaremos para
             *     referirnos a cada entidad.
             *
             * Podríamos utilizar otro alias:
             *
             *     SELECT cliente
             *     FROM ClienteEntidad cliente
             *
             * El resultado sería equivalente.
             *
             * -------------------------------------------------
             * ORDER BY c.id
             * -------------------------------------------------
             *
             * Ordena los resultados por el atributo id.
             *
             * Al no indicar DESC, se utiliza el orden
             * ascendente por defecto.
             *
             * Por ejemplo:
             *
             *     ID 1
             *     ID 2
             *     ID 3
             *
             * Si quisiéramos el orden inverso:
             *
             *     ORDER BY c.id DESC
             *
             * -------------------------------------------------
             * ClienteEntidad.class
             * -------------------------------------------------
             *
             * Es el segundo argumento de createQuery().
             *
             * Representa el tipo de resultado esperado.
             *
             * Con él creamos una consulta tipada.
             *
             * Hibernate sabe que debe devolver
             * objetos ClienteEntidad.
             *
             * Esto proporciona seguridad de tipos.
             *
             * -------------------------------------------------
             * getResultList()
             * -------------------------------------------------
             *
             * Ejecuta la consulta y recupera los resultados.
             *
             * Devuelve una lista de objetos ClienteEntidad.
             *
             * Hibernate realiza internamente:
             *
             * 1. Interpretar la consulta JPQL.
             *
             * 2. Traducirla a SQL para PostgreSQL.
             *
             * 3. Ejecutar el SQL mediante JDBC.
             *
             * 4. Recuperar las filas devueltas.
             *
             * 5. Construir o recuperar las entidades Java.
             *
             * 6. Asignar los valores de las columnas
             *    a los atributos correspondientes.
             *
             * 7. Devolver la lista de entidades.
             *
             * Si no hay clientes, devuelve una lista vacía.
             *
             * No devuelve null simplemente porque
             * la consulta no encuentre registros.
             */
            List<ClienteEntidad> clientes = em.createQuery(
                    "SELECT c FROM ClienteEntidad c ORDER BY c.id",
                    ClienteEntidad.class
            ).getResultList();

            /*
             * =================================================
             * PASO 5: RECORRER LOS CLIENTES
             * =================================================
             *
             * Utilizamos un bucle for-each.
             *
             * Su estructura es:
             *
             * for (Tipo variable : coleccion) {
             *     instrucciones;
             * }
             *
             * En nuestro caso:
             *
             * ClienteEntidad:
             *     Tipo de elemento de la lista.
             *
             * cliente:
             *     Variable que representa el objeto actual.
             *
             * clientes:
             *     Lista que queremos recorrer.
             *
             * El bucle se ejecutará una vez por cada cliente.
             *
             * Si la lista está vacía, no se ejecutará
             * ninguna iteración.
             */
            for (ClienteEntidad cliente : clientes) {

                /*
                 * =============================================
                 * MOSTRAR EL IDENTIFICADOR
                 * =============================================
                 *
                 * getId() es un método getter.
                 *
                 * Recupera el identificador del cliente.
                 *
                 * Este atributo corresponde a la clave
                 * primaria de la tabla cliente.
                 */
                System.out.println(
                        "ID: " + cliente.getId()
                );

                /*
                 * =============================================
                 * MOSTRAR EL NOMBRE
                 * =============================================
                 *
                 * getNombre() devuelve el valor del atributo
                 * nombre de ClienteEntidad.
                 *
                 * Hibernate habrá cargado este valor
                 * desde la columna nombre de PostgreSQL.
                 */
                System.out.println(
                        "Nombre: " + cliente.getNombre()
                );

                /*
                 * =============================================
                 * MOSTRAR EL EMAIL
                 * =============================================
                 *
                 * getEmail() devuelve el correo electrónico
                 * almacenado en la entidad.
                 */
                System.out.println(
                        "Email: " + cliente.getEmail()
                );

                /*
                 * =============================================
                 * MOSTRAR EL ESTADO ACTIVO
                 * =============================================
                 *
                 * isActivo() es un getter habitual
                 * para atributos boolean.
                 *
                 * Devuelve:
                 *
                 * true:
                 *     El cliente está activo.
                 *
                 * false:
                 *     El cliente está inactivo.
                 *
                 * PostgreSQL puede almacenar este atributo
                 * en una columna de tipo BOOLEAN.
                 */
                System.out.println(
                        "Activo: " + cliente.isActivo()
                );

                /*
                 * Añadimos una línea separadora
                 * para distinguir visualmente los clientes.
                 */
                System.out.println("------------------------");
            }

        } catch (Exception e) {

            /*
             * =================================================
             * PASO 6: GESTIONAR LOS ERRORES
             * =================================================
             *
             * Este bloque se ejecuta si se produce
             * una excepción durante la consulta.
             *
             * System.err.println():
             *     Muestra un mensaje en la salida de errores.
             *
             * e.printStackTrace():
             *     Muestra el tipo de excepción,
             *     su mensaje y la traza de ejecución.
             *
             * Es útil para localizar problemas
             * de conexión, configuración o JPQL.
             */
            System.err.println(
                    "Error al obtener clientes"
            );

            e.printStackTrace();

        } finally {

            /*
             * =================================================
             * PASO 7: CERRAR EL EntityManager
             * =================================================
             *
             * El bloque finally se ejecuta al salir del try,
             * independientemente de si la consulta ha
             * funcionado o ha lanzado una excepción.
             *
             * Primero cerramos el EntityManager.
             *
             * Después cerraremos la factoría JPA.
             */

            /*
             * em != null:
             *     Comprueba que hemos llegado a crear
             *     un EntityManager.
             *
             * em.isOpen():
             *     Comprueba que continúa abierto.
             *
             * &&:
             *     Operador AND lógico con cortocircuito.
             *
             * Si em es null, Java no evaluará em.isOpen().
             *
             * Esto evita una NullPointerException.
             */
            if (em != null && em.isOpen()) {

                /*
                 * close() libera el contexto de persistencia
                 * y los recursos asociados.
                 *
                 * Los objetos Java que ya hemos recuperado
                 * siguen existiendo después del cierre.
                 *
                 * Sin embargo, dejan de estar gestionados
                 * por este EntityManager.
                 *
                 * Pasan al estado detached.
                 */
                em.close();
            }

            /*
             * =================================================
             * PASO 8: CERRAR LA FACTORÍA JPA
             * =================================================
             *
             * Como esta clase es una prueba independiente,
             * cerramos la EntityManagerFactory al finalizar.
             *
             * En una aplicación Swing, la factoría se
             * mantendría abierta durante toda la ejecución.
             *
             * Utilizamos otro try/catch porque el cierre
             * también podría producir un error.
             */
            try {

                /*
                 * Cierra la factoría compartida que utiliza
                 * JpaUtil para crear EntityManager.
                 */
                JpaUtil.cerrar();

            } catch (Exception | LinkageError e) {

                /*
                 * =============================================
                 * CAPTURA MÚLTIPLE DE EXCEPCIONES
                 * =============================================
                 *
                 * Exception:
                 *     Captura excepciones habituales.
                 *
                 * LinkageError:
                 *     Captura problemas relacionados
                 *     con la carga o vinculación de clases.
                 *
                 * El operador | permite capturar ambos
                 * tipos utilizando un único bloque.
                 *
                 * e.getMessage() devuelve el mensaje
                 * asociado a la excepción.
                 *
                 * IMPORTANTE:
                 *
                 * Escribir solamente:
                 *
                 *     e.getMessage();
                 *
                 * no muestra nada por consola.
                 *
                 * Por eso concatenamos el mensaje
                 * dentro de System.err.println().
                 */
                System.err.println(
                        "No se pudo cerrar la Factoría JPA: "
                                + e.getMessage()
                );
            }
        }
    }
}
