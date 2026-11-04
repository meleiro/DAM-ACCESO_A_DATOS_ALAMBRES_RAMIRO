
package dao.orm;

/*
 * ============================================================
 * IMPORTACIÓN DE JpaUtil
 * ============================================================
 *
 * JpaUtil es nuestra clase auxiliar para trabajar con JPA.
 *
 * Contiene una EntityManagerFactory compartida que se crea
 * utilizando la configuración de persistence.xml.
 *
 * Su método:
 *
 *     JpaUtil.abrir()
 *
 * devuelve un EntityManager nuevo.
 *
 * En la implementación JDBC utilizábamos una Connection.
 * En ORM utilizamos EntityManager para gestionar entidades.
 */
import conexion.JpaUtil;

/*
 * EntityManager es la interfaz principal de JPA.
 *
 * Permite:
 *
 * - Buscar entidades con find().
 * - Insertar entidades con persist().
 * - Eliminar entidades con remove().
 * - Ejecutar consultas con createQuery().
 * - Obtener transacciones con getTransaction().
 *
 * Hibernate implementa esta interfaz y utiliza JDBC
 * internamente para comunicarse con PostgreSQL.
 */
import jakarta.persistence.EntityManager;

/*
 * EntityTransaction representa una transacción local de JPA.
 *
 * Nos proporciona métodos como:
 *
 * begin():
 *     Inicia una transacción.
 *
 * commit():
 *     Confirma los cambios realizados.
 *
 * rollback():
 *     Intenta deshacer los cambios de la transacción.
 *
 * isActive():
 *     Comprueba si la transacción continúa activa.
 *
 * En nuestro persistence.xml utilizamos:
 *
 * transaction-type="RESOURCE_LOCAL"
 *
 * Por eso podemos gestionar las transacciones directamente
 * mediante EntityTransaction.
 */
import jakarta.persistence.EntityTransaction;

/*
 * Function es una interfaz funcional de Java.
 *
 * Está definida en el paquete java.util.function.
 *
 * Permite representar una función que:
 *
 * 1. Recibe un argumento.
 * 2. Ejecuta una operación.
 * 3. Devuelve un resultado.
 *
 * Su forma general es:
 *
 * Function<Entrada, Salida>
 *
 * En nuestro caso:
 *
 * Function<EntityManager, T>
 *
 * Entrada:
 *     EntityManager.
 *
 * Salida:
 *     Un valor del tipo genérico T.
 *
 * Utilizaremos esta interfaz para recibir operaciones
 * mediante expresiones lambda.
 */
import java.util.function.Function;

/**
 * ============================================================
 * CLASE TransaccionesORM
 * ============================================================
 *
 * OBJETIVO:
 *
 * Centralizar la gestión de transacciones de JPA/Hibernate.
 *
 * Los DAO podrán ejecutar operaciones como:
 *
 *     insertar()
 *     actualizar()
 *     eliminar()
 *
 * sin tener que repetir toda la lógica de transacciones.
 *
 * Esta clase se encargará de:
 *
 * 1. Crear un EntityManager.
 * 2. Obtener una EntityTransaction.
 * 3. Iniciar la transacción.
 * 4. Ejecutar la operación recibida.
 * 5. Confirmar los cambios si todo funciona.
 * 6. Intentar revertirlos si ocurre un error.
 * 7. Cerrar el EntityManager.
 *
 * La palabra final impide que otras clases hereden
 * de TransaccionesORM.
 *
 * Es apropiada porque estamos creando una clase
 * auxiliar con métodos estáticos.
 */
public final class TransaccionesORM {

    /*
     * ========================================================
     * CONSTRUCTOR PRIVADO
     * ========================================================
     *
     * Un constructor es el mecanismo utilizado para crear
     * objetos de una clase.
     *
     * Normalmente podríamos escribir:
     *
     *     new TransaccionesORM();
     *
     * Sin embargo, aquí no necesitamos crear objetos.
     *
     * Todos los métodos que queremos utilizar serán static.
     *
     * Por eso declaramos el constructor como private.
     *
     * De esta manera impedimos que otras clases creen
     * instancias de TransaccionesORM.
     *
     * Utilizaremos directamente:
     *
     *     TransaccionesORM.ejecutar(...);
     */
    private TransaccionesORM() {

    }

    /*
     * ========================================================
     * MÉTODO ejecutar()
     * ========================================================
     *
     * DECLARACIÓN:
     *
     * public static <T> T ejecutar(
     *     Function<EntityManager, T> accion
     * )
     *
     * Vamos a descomponerla:
     *
     * public:
     *     Permite llamar al método desde otras clases.
     *
     * static:
     *     Permite utilizar el método sin crear un objeto.
     *
     * <T>:
     *     Declara un parámetro de tipo genérico.
     *
     *     T representa un tipo que se determinará
     *     cuando se utilice el método.
     *
     * T:
     *     Es el tipo de dato que devolverá ejecutar().
     *
     * ejecutar:
     *     Nombre del método.
     *
     * Function<EntityManager, T>:
     *     Es el tipo del parámetro accion.
     *
     * accion:
     *     Representa la operación que queremos ejecutar
     *     dentro de la transacción.
     *
     * --------------------------------------------------------
     * ¿POR QUÉ UTILIZAMOS UN TIPO GENÉRICO?
     * --------------------------------------------------------
     *
     * Porque diferentes operaciones pueden devolver
     * diferentes tipos de resultados.
     *
     * Por ejemplo:
     *
     * Una actualización puede devolver boolean:
     *
     *     true  -> Cliente actualizado.
     *     false -> Cliente no encontrado.
     *
     * Una operación también podría devolver un Integer,
     * un String o incluso una entidad.
     *
     * Gracias a <T>, el mismo método ejecutar() sirve
     * para todas esas operaciones.
     *
     * --------------------------------------------------------
     * ¿QUÉ RECIBE accion?
     * --------------------------------------------------------
     *
     * Recibe una función que trabajará con un EntityManager.
     *
     * Por ejemplo:
     *
     *     em -> {
     *         em.persist(cliente);
     *         return null;
     *     }
     *
     * Esa expresión es una lambda.
     *
     * TransaccionesORM proporcionará el EntityManager
     * a la lambda cuando ejecute accion.apply(em).
     */
    public static <T> T ejecutar(
            Function<EntityManager, T> accion) {

        /*
         * ====================================================
         * PASO 1: CREAR EL EntityManager
         * ====================================================
         *
         * Llamamos al método estático abrir() de JpaUtil.
         *
         * Este método crea un EntityManager utilizando
         * la EntityManagerFactory compartida.
         *
         * El EntityManager representa el contexto de
         * persistencia de esta operación.
         *
         * IMPORTANTE:
         *
         * No estamos creando una nueva factoría.
         *
         * Estamos creando un nuevo EntityManager
         * utilizando la factoría existente.
         *
         * Cada llamada a ejecutar() utilizará su propio
         * EntityManager.
         */
        EntityManager em = JpaUtil.abrir();

        /*
         * ====================================================
         * PASO 2: BLOQUE try / finally EXTERIOR
         * ====================================================
         *
         * Este try tiene como finalidad garantizar
         * el cierre del EntityManager.
         *
         * El bloque finally se ejecutará cuando salgamos
         * de este try, tanto si la operación funciona
         * como si se produce una excepción.
         *
         * Así evitamos dejar abierto innecesariamente
         * el contexto de persistencia.
         */
        try {

            /*
             * =================================================
             * PASO 3: OBTENER LA TRANSACCIÓN
             * =================================================
             *
             * getTransaction() devuelve un EntityTransaction.
             *
             * Este objeto permite controlar el inicio,
             * confirmación y reversión de la transacción.
             *
             * Recordemos que nuestra unidad de persistencia
             * utiliza RESOURCE_LOCAL.
             *
             * Esto significa que la aplicación gestiona
             * explícitamente sus transacciones.
             */
            EntityTransaction tx = em.getTransaction();

            /*
             * =================================================
             * PASO 4: BLOQUE try / catch INTERIOR
             * =================================================
             *
             * Este segundo try controla los errores
             * producidos durante la transacción.
             *
             * Tenemos dos bloques try porque queremos
             * separar dos responsabilidades:
             *
             * try exterior:
             *     Garantizar el cierre del EntityManager.
             *
             * try interior:
             *     Gestionar commit() y rollback().
             */
            try {

                /*
                 * =============================================
                 * INICIAR LA TRANSACCIÓN
                 * =============================================
                 *
                 * begin() inicia una transacción.
                 *
                 * A partir de este momento, las operaciones
                 * realizadas sobre las entidades podrán
                 * formar parte de la misma transacción.
                 *
                 * Por ejemplo:
                 *
                 *     em.persist(cliente);
                 *
                 *     em.persist(producto);
                 *
                 * Ambas operaciones podrían confirmarse
                 * juntas mediante un único commit().
                 *
                 * Si ocurre un error, rollback() intentará
                 * deshacer los cambios de esa transacción.
                 */
                tx.begin();

                /*
                 * =============================================
                 * EJECUTAR LA OPERACIÓN RECIBIDA
                 * =============================================
                 *
                 * accion es el parámetro Function que hemos
                 * recibido en el método ejecutar().
                 *
                 * apply() es el método abstracto principal
                 * de la interfaz Function.
                 *
                 * Al escribir:
                 *
                 *     accion.apply(em)
                 *
                 * estamos ejecutando la función recibida,
                 * proporcionándole el EntityManager.
                 *
                 * Por ejemplo, si ClienteDAOOrm llama a:
                 *
                 *     TransaccionesORM.ejecutar(em -> {
                 *         em.persist(cliente);
                 *         return null;
                 *     });
                 *
                 * entonces accion.apply(em) ejecutará:
                 *
                 *     em.persist(cliente);
                 *
                 * ------------------------------------------------
                 * ¿QUÉ ES T resultado?
                 * ------------------------------------------------
                 *
                 * Es una variable cuyo tipo depende de T.
                 *
                 * Si la lambda devuelve true o false,
                 * el tipo será Boolean.
                 *
                 * Si devuelve un Integer, T será Integer.
                 *
                 * Si devuelve null, el resultado será null.
                 *
                 * Esto permite reutilizar el mismo método
                 * para diferentes operaciones CRUD.
                 */
                T resultado = accion.apply(em);

                /*
                 * =============================================
                 * CONFIRMAR LA TRANSACCIÓN
                 * =============================================
                 *
                 * commit() solicita confirmar los cambios.
                 *
                 * Hibernate sincroniza los cambios pendientes
                 * cuando corresponde y la transacción se
                 * confirma en PostgreSQL.
                 *
                 * Por ejemplo, puede generar:
                 *
                 * INSERT INTO cliente ...
                 *
                 * UPDATE cliente SET ...
                 *
                 * DELETE FROM cliente ...
                 *
                 * La operación no debe considerarse completada
                 * correctamente hasta que commit() termine
                 * sin lanzar una excepción.
                 *
                 * IMPORTANTE:
                 *
                 * commit() también puede fallar.
                 *
                 * Por ejemplo:
                 *
                 * - Por una restricción UNIQUE.
                 * - Por una clave foránea.
                 * - Por un problema de conexión.
                 * - Por un error al sincronizar los datos.
                 */
                tx.commit();

                /*
                 * =============================================
                 * DEVOLVER EL RESULTADO
                 * =============================================
                 *
                 * Devolvemos el resultado producido
                 * por la función recibida.
                 *
                 * Esta instrucción se ejecuta después
                 * de que commit() termine correctamente.
                 *
                 * Por ejemplo, actualizar() podría recibir
                 * true si el cliente se encontró y la
                 * transacción se confirmó.
                 *
                 * Antes de devolver el valor al llamador,
                 * Java ejecutará el bloque finally exterior,
                 * que cerrará el EntityManager.
                 */
                return resultado;

            } catch (RuntimeException ex) {

                /*
                 * =============================================
                 * GESTIÓN DE ERRORES
                 * =============================================
                 *
                 * RuntimeException es la clase base de muchas
                 * excepciones que pueden producirse durante
                 * operaciones JPA.
                 *
                 * Por ejemplo:
                 *
                 * - PersistenceException.
                 * - IllegalArgumentException.
                 * - IllegalStateException.
                 *
                 * Si se produce una excepción durante
                 * begin(), apply() o commit(), entraremos
                 * en este bloque.
                 *
                 * La variable ex contiene la excepción
                 * original que ha provocado el error.
                 */

                /*
                 * =============================================
                 * COMPROBAR SI LA TRANSACCIÓN SIGUE ACTIVA
                 * =============================================
                 *
                 * isActive() devuelve true si la transacción
                 * continúa activa.
                 *
                 * Antes de llamar a rollback() debemos
                 * comprobar su estado.
                 *
                 * Por ejemplo, si el error ocurre antes
                 * de que begin() termine correctamente,
                 * puede que no exista una transacción activa.
                 *
                 * También puede ocurrir que commit() falle
                 * después de que la transacción haya terminado.
                 *
                 * Por eso no llamamos a rollback()
                 * de forma incondicional.
                 */
                if (tx.isActive()) {

                    /*
                     * =========================================
                     * INTENTAR REVERTIR LA TRANSACCIÓN
                     * =========================================
                     *
                     * Utilizamos otro try/catch porque
                     * rollback() también puede fallar.
                     *
                     * Por ejemplo, si existe un problema
                     * grave con la conexión.
                     */
                    try {

                        /*
                         * rollback() solicita deshacer los
                         * cambios de la transacción activa.
                         *
                         * En una transacción normal, esto
                         * evita que se confirmen parcialmente
                         * las operaciones realizadas.
                         *
                         * Por ejemplo:
                         *
                         * 1. Insertamos un cliente.
                         * 2. Intentamos insertar otro cliente.
                         * 3. El segundo INSERT provoca un error.
                         * 4. Se solicita rollback().
                         *
                         * Si la reversión tiene éxito,
                         * no se confirma ninguno de los dos
                         * INSERT de esa transacción.
                         *
                         * IMPORTANTE:
                         *
                         * rollback() no deshace necesariamente
                         * los cambios realizados sobre los
                         * objetos Java que tenemos en memoria.
                         *
                         * Revierte la transacción de base de datos.
                         */
                        tx.rollback();

                    } catch (RuntimeException errorRollback) {

                        /*
                         * =====================================
                         * ¿QUÉ ES addSuppressed()?
                         * =====================================
                         *
                         * Puede ocurrir que tengamos dos errores:
                         *
                         * 1. La excepción original ex.
                         *
                         * 2. Una nueva excepción al intentar
                         *    ejecutar rollback().
                         *
                         * No queremos perder la información
                         * de ninguno de los dos.
                         *
                         * addSuppressed() añade la excepción
                         * del rollback como excepción secundaria
                         * asociada a la original.
                         *
                         * De esta manera, al imprimir la traza
                         * con printStackTrace(), podremos
                         * consultar ambos errores.
                         *
                         * La excepción principal seguirá siendo
                         * la que provocó el fallo inicial.
                         */
                        ex.addSuppressed(errorRollback);
                    }
                }

                /*
                 * =============================================
                 * PROPAGAR LA EXCEPCIÓN ORIGINAL
                 * =============================================
                 *
                 * throw ex vuelve a lanzar la excepción.
                 *
                 * No queremos ocultar el problema.
                 *
                 * Por ejemplo, si un email ya existe y
                 * PostgreSQL rechaza el INSERT, queremos
                 * que ClienteDAOOrm o VentanaPrincipalORM
                 * puedan conocer el error.
                 *
                 * La interfaz gráfica podrá capturarlo
                 * y mostrar un mensaje al usuario.
                 *
                 * Después de este throw se ejecutará
                 * el bloque finally exterior.
                 */
                throw ex;
            }

        } finally {

            /*
             * =================================================
             * PASO 5: CERRAR EL EntityManager
             * =================================================
             *
             * Este bloque se ejecuta tanto si:
             *
             * - La transacción se confirma.
             * - La transacción falla.
             * - Se ejecuta rollback().
             * - Se devuelve un resultado con return.
             * - Se propaga una excepción con throw.
             *
             * isOpen() comprueba si el EntityManager
             * continúa abierto.
             *
             * Si está abierto, llamamos a close().
             *
             * close() libera el contexto de persistencia
             * y los recursos asociados.
             *
             * IMPORTANTE:
             *
             * No estamos cerrando la EntityManagerFactory.
             *
             * JpaUtil conserva esa factoría para crear
             * nuevos EntityManager en otras operaciones.
             *
             * Tampoco significa necesariamente cerrar
             * definitivamente una conexión física JDBC,
             * porque Hibernate puede gestionar conexiones
             * mediante un pool.
             */
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
