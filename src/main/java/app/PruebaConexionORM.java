
package app;

/*
 * ============================================================
 * IMPORTACIÓN DE JpaUtil
 * ============================================================
 *
 * JpaUtil es una clase auxiliar que hemos creado
 * en el paquete conexion.
 *
 * Su función es centralizar la gestión de JPA.
 *
 * Internamente dispone de una EntityManagerFactory
 * que se crea utilizando la unidad de persistencia
 * configurada en:
 *
 * src/main/resources/META-INF/persistence.xml
 *
 * En nuestro proyecto, esa unidad se llama:
 *
 *     tiendaPU
 *
 * Utilizaremos dos métodos:
 *
 * JpaUtil.abrir():
 *     Devuelve un EntityManager para trabajar con JPA.
 *
 * JpaUtil.cerrar():
 *     Cierra la EntityManagerFactory compartida.
 */
import conexion.JpaUtil;

/*
 * ============================================================
 * IMPORTACIÓN DE EntityManager
 * ============================================================
 *
 * EntityManager es una interfaz de Jakarta Persistence.
 *
 * Permite trabajar con entidades y ejecutar consultas.
 *
 * Por ejemplo:
 *
 * em.find():
 *     Busca una entidad por su identificador.
 *
 * em.persist():
 *     Registra una entidad nueva.
 *
 * em.remove():
 *     Solicita eliminar una entidad.
 *
 * em.createQuery():
 *     Crea una consulta JPQL.
 *
 * em.createNativeQuery():
 *     Crea una consulta SQL nativa.
 *
 * em.close():
 *     Cierra el contexto de persistencia.
 *
 * IMPORTANTE:
 *
 * EntityManager no es exactamente una conexión JDBC.
 *
 * Representa un contexto de persistencia administrado
 * por el proveedor JPA, que en nuestro caso es Hibernate.
 *
 * Hibernate utiliza JDBC internamente para comunicarse
 * con PostgreSQL cuando necesita ejecutar operaciones.
 */
import jakarta.persistence.EntityManager;

/**
 * ============================================================
 * CLASE PruebaConexionORM
 * ============================================================
 *
 * OBJETIVO:
 *
 * Comprobar que nuestra configuración JPA/Hibernate
 * permite ejecutar una consulta contra PostgreSQL.
 *
 * Esta clase es una aplicación de consola.
 *
 * No utiliza Swing ni los DAO de clientes o productos.
 *
 * La prueba consiste en:
 *
 * 1. Obtener un EntityManager.
 * 2. Ejecutar la consulta SQL SELECT 1.
 * 3. Recuperar el resultado.
 * 4. Comprobar que el resultado es 1.
 * 5. Mostrar mensajes por consola.
 * 6. Cerrar los recursos utilizados.
 *
 * SELECT 1 no necesita consultar ninguna tabla.
 *
 * Sin embargo, al inicializar Hibernate pueden
 * producirse errores de configuración o validación
 * de entidades y tablas.
 */
public class PruebaConexionORM {

    /*
     * ========================================================
     * MÉTODO main()
     * ========================================================
     *
     * Es el punto de entrada de la aplicación.
     *
     * public:
     *     Permite que el método sea accesible.
     *
     * static:
     *     Permite ejecutarlo sin crear un objeto
     *     de la clase PruebaConexionORM.
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
         * PASO 1: MOSTRAR UN MENSAJE INICIAL
         * ====================================================
         *
         * System.out.println() escribe texto en la consola.
         *
         * System:
         *     Clase estándar de Java.
         *
         * out:
         *     Salida estándar.
         *
         * println():
         *     Imprime el texto y añade un salto de línea.
         */
        System.out.println("Prueba de conexión");

        /*
         * ====================================================
         * PASO 2: DECLARAR EL EntityManager
         * ====================================================
         *
         * Declaramos una variable llamada em.
         *
         * Su tipo es EntityManager.
         *
         * Inicialmente le asignamos null porque todavía
         * no hemos creado el gestor de persistencia.
         *
         * ¿POR QUÉ LA DECLARAMOS FUERA DEL TRY?
         *
         * Porque necesitaremos acceder a ella
         * desde el bloque finally.
         *
         * Si la declaramos dentro del try, no podríamos
         * utilizar esa variable desde el finally.
         *
         * La inicialización a null también permite
         * comprobar si llegó a crearse correctamente.
         */
        EntityManager em = null;

        /*
         * ====================================================
         * PASO 3: BLOQUE try
         * ====================================================
         *
         * En el try colocamos las operaciones que pueden
         * producir excepciones.
         *
         * Por ejemplo:
         *
         * - Un error en persistence.xml.
         * - Una contraseña incorrecta.
         * - PostgreSQL no está disponible.
         * - Un problema con Hibernate.
         * - Una consulta SQL incorrecta.
         *
         * Si se produce una excepción compatible con
         * Exception, se ejecutará el bloque catch.
         *
         * Independientemente del resultado, después
         * se ejecutará el bloque finally.
         */
        try {

            /*
             * =================================================
             * PASO 4: CREAR EL EntityManager
             * =================================================
             *
             * Llamamos al método estático abrir()
             * de nuestra clase JpaUtil.
             *
             * Internamente, JpaUtil utiliza:
             *
             * EntityManagerFactory.createEntityManager()
             *
             * para obtener un nuevo EntityManager.
             *
             * La EntityManagerFactory se configura mediante
             * la unidad de persistencia tiendaPU.
             *
             * Esa configuración incluye:
             *
             * - El proveedor Hibernate.
             * - El controlador JDBC de PostgreSQL.
             * - La URL de la base de datos.
             * - El usuario y la contraseña.
             * - Las entidades que queremos gestionar.
             *
             * IMPORTANTE:
             *
             * Crear correctamente un EntityManager
             * no demuestra por sí solo que se haya
             * ejecutado una consulta en PostgreSQL.
             *
             * Esa comprobación la realizaremos después.
             */
            em = JpaUtil.abrir();

            /*
             * Mostramos un mensaje para indicar que
             * hemos obtenido el EntityManager.
             *
             * Este mensaje no garantiza todavía
             * que la consulta SQL vaya a funcionar.
             */
            System.out.println(
                    "Conexión creada correctamente"
            );

            /*
             * =================================================
             * PASO 5: EJECUTAR UNA CONSULTA SQL NATIVA
             * =================================================
             *
             * Utilizamos:
             *
             * em.createNativeQuery("SELECT 1")
             *
             * createNativeQuery() permite escribir SQL
             * directamente.
             *
             * A diferencia de createQuery(), que utiliza
             * JPQL, aquí escribimos SQL nativo.
             *
             * -------------------------------------------------
             * ¿QUÉ HACE SELECT 1?
             * -------------------------------------------------
             *
             * Es una consulta SQL muy sencilla:
             *
             * SELECT 1;
             *
             * PostgreSQL devuelve una fila que contiene
             * el valor numérico 1.
             *
             * No necesita acceder a la tabla cliente
             * ni a la tabla producto.
             *
             * Es útil para comprobar que podemos
             * ejecutar una consulta en el servidor.
             *
             * -------------------------------------------------
             * ¿QUÉ HACE getSingleResult()?
             * -------------------------------------------------
             *
             * Ejecuta la consulta y recupera
             * un único resultado.
             *
             * Como SELECT 1 devuelve un único valor,
             * podemos utilizar este método.
             *
             * Si una consulta no devolviese resultados,
             * getSingleResult() podría lanzar una excepción,
             * dependiendo de la versión de JPA y del método.
             *
             * -------------------------------------------------
             * ¿POR QUÉ GUARDAMOS EL RESULTADO EN Object?
             * -------------------------------------------------
             *
             * Las consultas SQL nativas pueden devolver
             * diferentes tipos de datos.
             *
             * Por eso utilizamos una variable Object,
             * que es la clase base de los objetos Java.
             *
             * En esta consulta esperamos recibir
             * un valor numérico.
             */
            Object resultado = em.createNativeQuery(
                    "SELECT 1"
            ).getSingleResult();

            /*
             * =================================================
             * PASO 6: MOSTRAR EL RESULTADO
             * =================================================
             *
             * Concatenamos el texto "Resultado: "
             * con el valor recuperado de PostgreSQL.
             *
             * Si todo funciona, debería mostrarse:
             *
             * Resultado: 1
             */
            System.out.println(
                    "Resultado: " + resultado
            );

            /*
             * =================================================
             * PASO 7: COMPROBAR EL VALOR DEVUELTO
             * =================================================
             *
             * Queremos comprobar que el resultado
             * obtenido es exactamente el número 1.
             *
             * La expresión es:
             *
             * ((Number) resultado).intValue() == 1
             *
             * Vamos a descomponerla.
             *
             * -------------------------------------------------
             * (Number) resultado
             * -------------------------------------------------
             *
             * Realizamos una conversión de tipos,
             * conocida como casting.
             *
             * resultado está declarado como Object.
             *
             * Pero sabemos que SELECT 1 debe devolver
             * un valor numérico.
             *
             * Number es una clase abstracta de Java
             * que sirve como base para tipos numéricos
             * como Integer, Long y BigDecimal.
             *
             * Por eso convertimos Object a Number.
             *
             * -------------------------------------------------
             * .intValue()
             * -------------------------------------------------
             *
             * Este método obtiene una representación
             * del número como un valor primitivo int.
             *
             * Por ejemplo:
             *
             * Integer.valueOf(1).intValue() -> 1
             *
             * -------------------------------------------------
             * == 1
             * -------------------------------------------------
             *
             * El operador == compara dos valores primitivos.
             *
             * En este caso comprueba si el número
             * obtenido es igual a 1.
             *
             * Si la condición es verdadera, entraremos
             * en el bloque if.
             *
             * Si resultado no fuese un Number,
             * el casting provocaría ClassCastException.
             */
            if (((Number) resultado).intValue() == 1) {

                /*
                 * Este mensaje confirma que:
                 *
                 * 1. Hemos podido ejecutar una consulta.
                 * 2. PostgreSQL ha devuelto un resultado.
                 * 3. El resultado coincide con el esperado.
                 *
                 * Por tanto, la comunicación básica
                 * entre Hibernate y PostgreSQL funciona.
                 */
                System.out.println(
                        "CONEXIÓN Y CONSULTA CORRECTAS"
                );
            }

        } catch (Exception e) {

            /*
             * =================================================
             * PASO 8: CAPTURAR POSIBLES EXCEPCIONES
             * =================================================
             *
             * Si se produce una excepción dentro del try,
             * se interrumpe su ejecución normal y entramos
             * en este bloque catch.
             *
             * Exception es una clase base de muchas
             * excepciones de Java.
             *
             * La variable e contiene la excepción
             * que se ha producido.
             *
             * System.err.println():
             *     Escribe un mensaje en la salida de errores.
             *
             * A diferencia de System.out, utilizamos
             * System.err para identificar los mensajes
             * relacionados con problemas.
             */
            System.err.println(
                    "ERROR EN LA PRUEBA ORM:"
            );

            /*
             * printStackTrace() imprime información detallada
             * sobre la excepción:
             *
             * - Tipo de excepción.
             * - Mensaje de error.
             * - Métodos por los que ha pasado la ejecución.
             * - Líneas donde se ha producido el problema.
             *
             * Es especialmente útil durante el desarrollo.
             */
            e.printStackTrace();

        } finally {

            /*
             * =================================================
             * PASO 9: BLOQUE finally
             * =================================================
             *
             * El bloque finally se ejecuta normalmente
             * tanto si el try termina correctamente
             * como si se produce una excepción.
             *
             * Su objetivo es liberar recursos.
             *
             * En nuestro caso tenemos dos recursos:
             *
             * 1. EntityManager.
             * 2. EntityManagerFactory.
             *
             * Primero cerramos el EntityManager.
             *
             * Después cerramos la factoría.
             */

            /*
             * =================================================
             * COMPROBAR Y CERRAR EL EntityManager
             * =================================================
             *
             * em != null:
             *     Comprueba que la variable contiene
             *     una referencia a un EntityManager.
             *
             * em.isOpen():
             *     Comprueba si continúa abierto.
             *
             * &&:
             *     Es el operador AND lógico.
             *
             * Las dos condiciones deben ser verdaderas
             * para ejecutar em.close().
             *
             * IMPORTANTE:
             *
             * && utiliza evaluación de cortocircuito.
             *
             * Si em es null, Java no evalúa em.isOpen().
             *
             * Así evitamos una NullPointerException.
             */
            if (em != null && em.isOpen()) {

                /*
                 * close() cierra el EntityManager.
                 *
                 * Esto libera el contexto de persistencia
                 * y sus recursos asociados.
                 *
                 * No significa necesariamente cerrar
                 * definitivamente una conexión JDBC física.
                 */
                em.close();
            }

            /*
             * =================================================
             * PASO 10: CERRAR LA FACTORÍA JPA
             * =================================================
             *
             * Ahora queremos cerrar la EntityManagerFactory.
             *
             * En una aplicación Swing normal, la factoría
             * permanece abierta durante toda la ejecución.
             *
             * Sin embargo, esta es una prueba independiente
             * que termina después de ejecutar SELECT 1.
             *
             * Por eso podemos cerrar la factoría aquí.
             *
             * Utilizamos otro try/catch porque el cierre
             * también puede provocar una excepción.
             */
            try {

                /*
                 * JpaUtil.cerrar() intenta cerrar
                 * la EntityManagerFactory compartida.
                 *
                 * Una vez cerrada, no podremos seguir
                 * creando EntityManager con esa factoría.
                 */
                JpaUtil.cerrar();

            } catch (Exception | LinkageError e) {

                /*
                 * =============================================
                 * CAPTURA MÚLTIPLE DE EXCEPCIONES
                 * =============================================
                 *
                 * El símbolo | permite capturar
                 * distintos tipos de errores
                 * utilizando un único catch.
                 *
                 * Exception:
                 *     Captura excepciones habituales.
                 *
                 * LinkageError:
                 *     Captura problemas relacionados con
                 *     la carga o vinculación de clases.
                 *
                 * Esta captura puede ser útil si se
                 * ha producido un error al inicializar JPA.
                 *
                 * ------------------------------------------------
                 * ¿QUÉ HACE e.getMessage()?
                 * ------------------------------------------------
                 *
                 * Devuelve el mensaje de la excepción.
                 *
                 * IMPORTANTE:
                 *
                 * Si escribimos solamente:
                 *
                 *     e.getMessage();
                 *
                 * no mostramos nada por consola.
                 *
                 * Por eso incluimos el resultado
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
