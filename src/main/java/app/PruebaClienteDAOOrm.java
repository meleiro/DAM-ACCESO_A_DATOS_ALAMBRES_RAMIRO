
package app;

/*
 * ============================================================
 * IMPORTACIÓN DE JpaUtil
 * ============================================================
 *
 * JpaUtil es nuestra clase auxiliar de conexión con JPA.
 *
 * Contiene una EntityManagerFactory compartida que
 * se inicializa utilizando la unidad de persistencia
 * definida en META-INF/persistence.xml.
 *
 * En esta clase utilizaremos:
 *
 *     JpaUtil.cerrar();
 *
 * para cerrar la factoría al terminar la prueba.
 *
 * IMPORTANTE:
 *
 * No necesitamos abrir directamente un EntityManager,
 * porque esa responsabilidad corresponde al DAO.
 */
import conexion.JpaUtil;

/*
 * ============================================================
 * IMPORTACIÓN DE ClienteDAOOrm
 * ============================================================
 *
 * ClienteDAOOrm es nuestra clase DAO que utiliza Hibernate.
 *
 * Contiene métodos para trabajar con la tabla cliente:
 *
 * - listar()
 * - buscar()
 * - insertar()
 * - actualizar()
 * - eliminar()
 *
 * En esta prueba solamente utilizaremos listar().
 *
 * El DAO se encargará de obtener el EntityManager,
 * ejecutar la consulta JPQL y cerrar el EntityManager.
 */
import dao.orm.ClienteDAOOrm;

/*
 * ============================================================
 * IMPORTACIÓN DE ClienteEntidad
 * ============================================================
 *
 * ClienteEntidad representa la tabla cliente de PostgreSQL.
 *
 * Está anotada con @Entity y @Table.
 *
 * Cada objeto ClienteEntidad contiene los datos
 * de un registro de la tabla.
 *
 * Por ejemplo:
 *
 * id = 1
 * nombre = "Ana"
 * email = "ana@ejemplo.com"
 * activo = true
 *
 * Hibernate construye estos objetos a partir de
 * los resultados de las consultas.
 */
import modelo.orm.ClienteEntidad;

/*
 * ============================================================
 * IMPORTACIÓN DE List
 * ============================================================
 *
 * List es una interfaz de colecciones de Java.
 *
 * Nos permite almacenar varios objetos del mismo tipo.
 *
 * En nuestro caso:
 *
 *     List<ClienteEntidad>
 *
 * representa una lista de entidades ClienteEntidad.
 */
import java.util.List;

/**
 * ============================================================
 * CLASE PruebaClienteDAOOrm
 * ============================================================
 *
 * OBJETIVO:
 *
 * Comprobar que nuestro DAO ORM puede recuperar
 * correctamente los clientes de PostgreSQL.
 *
 * Esta clase es una aplicación de consola.
 *
 * No utiliza Swing.
 *
 * Esto nos permite probar la capa de persistencia
 * independientemente de la interfaz gráfica.
 *
 * SECUENCIA:
 *
 * 1. Crear un objeto ClienteDAOOrm.
 * 2. Ejecutar su método listar().
 * 3. Recuperar una lista de ClienteEntidad.
 * 4. Mostrar el número de clientes encontrados.
 * 5. Recorrer y mostrar los clientes.
 * 6. Gestionar posibles excepciones.
 * 7. Cerrar la EntityManagerFactory.
 */
public class PruebaClienteDAOOrm {

    /*
     * ========================================================
     * MÉTODO main()
     * ========================================================
     *
     * Es el punto de entrada de nuestra aplicación.
     *
     * public:
     *     Puede ser invocado desde fuera de la clase.
     *
     * static:
     *     No necesitamos crear un objeto
     *     PruebaClienteDAOOrm para ejecutarlo.
     *
     * void:
     *     No devuelve ningún valor.
     *
     * String[] args:
     *     Permite recibir argumentos desde la consola.
     *
     * En esta prueba no utilizamos esos argumentos.
     */
    public static void main(String[] args) {

        /*
         * ====================================================
         * BLOQUE try PRINCIPAL
         * ====================================================
         *
         * Introducimos las operaciones en un try porque
         * pueden producirse excepciones.
         *
         * Por ejemplo:
         *
         * - PostgreSQL no está iniciado.
         * - La contraseña es incorrecta.
         * - No existe la tabla cliente.
         * - La configuración de Hibernate es incorrecta.
         * - La consulta JPQL contiene algún error.
         *
         * Si ocurre una excepción, se ejecutará catch.
         */
        try {

            /*
             * ================================================
             * PASO 1: CREAR EL DAO
             * ================================================
             *
             * Creamos un objeto de ClienteDAOOrm.
             *
             * ClienteDAOOrm es la clase responsable
             * de acceder a los datos de clientes.
             *
             * Esta instrucción:
             *
             *     new ClienteDAOOrm()
             *
             * crea un objeto Java.
             *
             * No significa necesariamente que se haya
             * establecido una conexión con PostgreSQL.
             *
             * La conexión se utilizará cuando el DAO
             * necesite ejecutar una consulta.
             */
            ClienteDAOOrm dao = new ClienteDAOOrm();

            /*
             * ================================================
             * PASO 2: OBTENER LOS CLIENTES
             * ================================================
             *
             * Llamamos al método listar() del DAO.
             *
             * Internamente, este método ejecuta una
             * consulta JPQL similar a:
             *
             * SELECT c
             * FROM ClienteEntidad c
             * ORDER BY c.id
             *
             * Hibernate traduce esa consulta a SQL,
             * la ejecuta contra PostgreSQL y transforma
             * los resultados en objetos ClienteEntidad.
             *
             * El resultado se guarda en una variable
             * llamada clientes.
             *
             * ------------------------------------------------
             * ¿QUÉ SIGNIFICA List<ClienteEntidad>?
             * ------------------------------------------------
             *
             * List:
             *     Indica que tenemos una colección.
             *
             * <ClienteEntidad>:
             *     Indica el tipo de objetos que contiene.
             *
             * clientes:
             *     Es el nombre de la variable.
             *
             * Si no hay registros, la lista estará vacía.
             *
             * getResultList() no devuelve null simplemente
             * porque no encuentre clientes.
             */
            List<ClienteEntidad> clientes = dao.listar();

            /*
             * ================================================
             * PASO 3: MOSTRAR EL NÚMERO DE CLIENTES
             * ================================================
             *
             * size() es un método de List.
             *
             * Devuelve el número de elementos almacenados
             * en la colección.
             *
             * Por ejemplo:
             *
             * Si hay 5 clientes:
             *
             *     clientes.size() -> 5
             *
             * Si no hay clientes:
             *
             *     clientes.size() -> 0
             *
             * System.out.println() muestra el mensaje
             * en la salida estándar de la consola.
             *
             * El operador + concatena el texto
             * con el número obtenido.
             */
            System.out.println(
                    "Número de clientes: " + clientes.size()
            );

            /*
             * ================================================
             * PASO 4: RECORRER LA LISTA
             * ================================================
             *
             * Utilizamos un bucle for-each.
             *
             * Su estructura general es:
             *
             * for (Tipo variable : coleccion) {
             *     instrucciones;
             * }
             *
             * En nuestro caso:
             *
             * ClienteEntidad:
             *     Tipo de objeto que vamos a recorrer.
             *
             * cliente:
             *     Variable que representa el elemento actual.
             *
             * clientes:
             *     Colección que queremos recorrer.
             *
             * En cada iteración, cliente representa
             * uno de los objetos ClienteEntidad.
             *
             * Si la lista está vacía, el bucle no
             * ejecutará ninguna iteración.
             */
            for (ClienteEntidad cliente : clientes) {

                /*
                 * ============================================
                 * MOSTRAR LOS ATRIBUTOS DEL CLIENTE
                 * ============================================
                 *
                 * Utilizamos los métodos getter:
                 *
                 * getId():
                 *     Recupera el identificador.
                 *
                 * getNombre():
                 *     Recupera el nombre.
                 *
                 * getEmail():
                 *     Recupera el correo electrónico.
                 *
                 * Los getters permiten consultar los
                 * atributos privados de ClienteEntidad.
                 *
                 * El operador + concatena los valores
                 * y los separadores de texto.
                 *
                 * Por ejemplo:
                 *
                 * 1 - Ana - ana@ejemplo.com
                 *
                 * IMPORTANTE:
                 *
                 * El EntityManager utilizado por listar()
                 * ya se habrá cerrado.
                 *
                 * Sin embargo, los atributos básicos
                 * cargados de las entidades siguen
                 * disponibles para su lectura.
                 *
                 * Los objetos están separados (detached)
                 * del contexto de persistencia anterior.
                 */
                System.out.println(
                        cliente.getId() + " - "
                                + cliente.getNombre() + " - "
                                + cliente.getEmail()
                );
            }

        } catch (Exception e) {

            /*
             * =================================================
             * BLOQUE catch
             * =================================================
             *
             * Se ejecuta si alguna instrucción del try
             * produce una excepción compatible con Exception.
             *
             * La variable e contiene información
             * sobre el error producido.
             *
             * System.err.println():
             *     Muestra un mensaje en la salida de errores.
             *
             * e.printStackTrace():
             *     Imprime el tipo de excepción,
             *     su mensaje y la traza de llamadas.
             *
             * Esta información es especialmente útil
             * durante el desarrollo para localizar errores.
             */
            System.err.println("Error al obtener clientes");

            e.printStackTrace();

        } finally {

            /*
             * =================================================
             * BLOQUE finally
             * =================================================
             *
             * Se ejecuta tanto si la consulta funciona
             * como si se produce una excepción.
             *
             * Aquí queremos cerrar la EntityManagerFactory.
             *
             * IMPORTANTE:
             *
             * No estamos cerrando el EntityManager
             * utilizado por listar().
             *
             * Ese EntityManager ya lo cierra el propio DAO.
             *
             * Aquí cerramos la factoría compartida
             * porque esta aplicación de prueba
             * ya ha terminado de utilizar Hibernate.
             *
             * En una aplicación Swing normal, la factoría
             * permanecería abierta durante la ejecución
             * y se cerraría al finalizar la aplicación.
             */
            try {

                /*
                 * ============================================
                 * CERRAR LA FACTORÍA JPA
                 * ============================================
                 *
                 * JpaUtil.cerrar() comprueba si la
                 * EntityManagerFactory está abierta
                 * y, si corresponde, la cierra.
                 *
                 * Una vez cerrada, no podremos seguir
                 * creando EntityManager con esa factoría.
                 */
                JpaUtil.cerrar();

            } catch (Exception | LinkageError e) {

                /*
                 * ============================================
                 * CAPTURA MÚLTIPLE DE EXCEPCIONES
                 * ============================================
                 *
                 * El símbolo | permite capturar
                 * diferentes tipos de errores
                 * utilizando un mismo bloque catch.
                 *
                 * Exception:
                 *     Captura excepciones habituales.
                 *
                 * LinkageError:
                 *     Representa problemas relacionados
                 *     con la carga o vinculación de clases.
                 *
                 * Esta captura adicional puede ser útil
                 * si la inicialización de JPA ha fallado.
                 *
                 * ------------------------------------------------
                 * ¿QUÉ HACE getMessage()?
                 * ------------------------------------------------
                 *
                 * Devuelve el mensaje asociado al error.
                 *
                 * IMPORTANTE:
                 *
                 * Escribir solamente:
                 *
                 *     e.getMessage();
                 *
                 * no muestra nada en la consola.
                 *
                 * Por eso concatenamos el resultado
                 * con el mensaje de System.err.println().
                 */
                System.err.println(
                        "No se pudo cerrar la Factoría JPA: "
                                + e.getMessage()
                );
            }
        }
    }
}
