
package dao.orm;

/*
 * JpaUtil proporciona los EntityManager necesarios para
 * trabajar con Hibernate.
 *
 * En JDBC utilizábamos ConexionBD.obtenerConexion()
 * para conseguir una conexión de tipo Connection.
 *
 * Con JPA utilizamos JpaUtil.abrir() para obtener
 * un EntityManager.
 */
import conexion.JpaUtil;

/*
 * EntityManager es la interfaz principal de JPA para
 * consultar, insertar, actualizar y eliminar entidades.
 *
 * Hibernate implementa esta interfaz y utiliza JDBC
 * internamente para comunicarse con PostgreSQL.
 */
import jakarta.persistence.EntityManager;

/*
 * ClienteEntidad es la clase que representa la tabla
 * cliente de nuestra base de datos.
 *
 * Está anotada con @Entity y @Table(name = "cliente").
 */
import modelo.orm.ClienteEntidad;

/*
 * List permite devolver colecciones de entidades.
 *
 * En nuestro caso:
 *
 * List<ClienteEntidad>
 *
 * representa una lista de clientes.
 */
import java.util.List;

/**
 * ============================================================
 * CLASE ClienteDAOOrm
 * ============================================================
 *
 * DAO significa Data Access Object.
 *
 * Esta clase centraliza las operaciones de acceso
 * a los datos de clientes utilizando JPA/Hibernate.
 *
 * Implementamos las operaciones CRUD:
 *
 * CREATE -> insertar()
 * READ   -> listar()
 * UPDATE -> actualizar()
 * DELETE -> eliminar()
 *
 * La implementación JDBC original permanece intacta.
 *
 * La diferencia principal es que ahora trabajamos
 * con entidades Java en lugar de construir manualmente
 * sentencias INSERT, UPDATE y DELETE.
 */
public class ClienteDAOOrm {

    /*
     * ========================================================
     * MÉTODO listar()
     * ========================================================
     *
     * Recupera todos los clientes de PostgreSQL.
     *
     * Devuelve una lista de objetos ClienteEntidad.
     *
     * Si no existen registros, devuelve una lista vacía.
     *
     * No necesita una transacción de escritura porque
     * únicamente vamos a consultar información.
     */
    public List<ClienteEntidad> listar() {

        /*
         * Creamos un EntityManager utilizando la factoría
         * que tenemos configurada en JpaUtil.
         *
         * El EntityManager representa un contexto
         * de persistencia.
         *
         * Durante su vida, Hibernate puede gestionar
         * las entidades que recuperemos.
         */
        EntityManager em = JpaUtil.abrir();

        /*
         * Utilizamos try/finally para garantizar el cierre
         * del EntityManager aunque ocurra una excepción.
         */
        try {

            /*
             * createQuery() permite escribir consultas JPQL.
             *
             * JPQL se parece a SQL, pero utiliza nombres
             * de entidades y atributos Java.
             *
             * CONSULTA:
             *
             * SELECT c
             * FROM ClienteEntidad c
             * ORDER BY c.id
             *
             * SELECT c:
             *     Recupera las entidades identificadas por c.
             *
             * FROM ClienteEntidad:
             *     Consulta la entidad Java ClienteEntidad.
             *
             * c:
             *     Alias utilizado para referirnos a la entidad.
             *
             * ORDER BY c.id:
             *     Ordena los clientes por su identificador.
             *
             * ClienteEntidad.class:
             *     Indica el tipo de objeto que esperamos obtener.
             *
             * getResultList():
             *     Ejecuta la consulta y devuelve una lista.
             *
             * Hibernate traduce esta consulta a SQL,
             * la ejecuta y construye los objetos Java.
             */
            return em.createQuery(
                    "SELECT c FROM ClienteEntidad c ORDER BY c.id",
                    ClienteEntidad.class
            ).getResultList();

        } finally {

            /*
             * Cerramos el EntityManager.
             *
             * Esto libera su contexto de persistencia.
             *
             * No cerramos la EntityManagerFactory,
             * porque seguirá utilizándose en otras operaciones.
             *
             * Las entidades recuperadas pasan a estar
             * separadas (detached) de este contexto.
             */
            em.close();
        }
    }

    /*
     * ========================================================
     * MÉTODO insertar()
     * ========================================================
     *
     * OBJETIVO:
     *
     * Insertar un nuevo cliente en PostgreSQL.
     *
     * PARÁMETRO:
     *
     * ClienteEntidad nuevo
     *
     * Es el objeto que contiene los datos del cliente
     * que queremos almacenar.
     *
     * Por ejemplo:
     *
     * ClienteEntidad cliente = new ClienteEntidad(
     *     null,
     *     "Ana",
     *     "ana@ejemplo.com",
     *     true
     * );
     *
     * dao.insertar(cliente);
     *
     * El identificador se establece inicialmente a null
     * porque PostgreSQL lo genera automáticamente mediante
     * la estrategia GenerationType.IDENTITY.
     *
     * DIFERENCIA CON JDBC:
     *
     * Antes escribíamos una sentencia INSERT INTO cliente...
     *
     * Ahora utilizamos em.persist(nuevo).
     */
    public void insertar(ClienteEntidad nuevo) {

        /*
         * TransaccionesORM es una clase auxiliar
         * que hemos creado en el mismo paquete.
         *
         * Su método ejecutar() se encarga de:
         *
         * 1. Crear un EntityManager.
         * 2. Iniciar una transacción.
         * 3. Ejecutar la operación recibida.
         * 4. Confirmar con commit().
         * 5. Revertir con rollback() si ocurre un error.
         * 6. Cerrar el EntityManager.
         *
         * Así evitamos repetir ese código en cada DAO.
         *
         * ----------------------------------------------------
         * ¿QUÉ SIGNIFICA em -> { ... }?
         * ----------------------------------------------------
         *
         * Es una expresión lambda de Java.
         *
         * La lambda representa una función que recibe
         * un EntityManager y ejecuta una operación.
         *
         * em:
         *     Es el parámetro que recibe la lambda.
         *
         * ->:
         *     Separa el parámetro del cuerpo de la función.
         *
         * { ... }:
         *     Contiene las instrucciones que ejecutaremos.
         *
         * No creamos aquí el EntityManager.
         *
         * Lo crea TransaccionesORM y nos lo proporciona
         * como argumento de la lambda.
         */
        TransaccionesORM.ejecutar(em -> {

            /*
             * ====================================================
             * em.persist(nuevo)
             * ====================================================
             *
             * persist() registra una entidad nueva en el
             * contexto de persistencia.
             *
             * El objeto nuevo pasa del estado transient
             * al estado managed.
             *
             * transient:
             *     Objeto Java que todavía no está gestionado
             *     por un EntityManager.
             *
             * managed:
             *     Objeto gestionado por Hibernate.
             *
             * Hibernate se encargará de realizar el INSERT
             * necesario para almacenar sus datos.
             *
             * IMPORTANTE:
             *
             * persist() no equivale exactamente a ejecutar
             * inmediatamente una sentencia INSERT.
             *
             * Normalmente Hibernate sincroniza los cambios
             * durante flush() o commit().
             *
             * Sin embargo, con GenerationType.IDENTITY,
             * puede necesitar ejecutar el INSERT antes
             * para obtener el identificador generado.
             *
             * El commit() de TransaccionesORM confirma
             * finalmente la operación.
             */
            em.persist(nuevo);

            /*
             * ====================================================
             * return null
             * ====================================================
             *
             * TransaccionesORM.ejecutar() recibe una función
             * que debe devolver un resultado.
             *
             * En nuestro caso, insertar() es void.
             *
             * No necesitamos devolver ningún dato.
             *
             * Por eso devolvemos null desde la lambda.
             *
             * Este return pertenece a la lambda,
             * no al método insertar().
             */
            return null;
        });
    }

    /*
     * ========================================================
     * MÉTODO actualizar()
     * ========================================================
     *
     * OBJETIVO:
     *
     * Modificar los datos de un cliente existente.
     *
     * PARÁMETRO:
     *
     * ClienteEntidad nuevo
     *
     * Contiene los nuevos valores que queremos guardar.
     *
     * El objeto debe tener un identificador válido.
     *
     * VALOR DEVUELTO:
     *
     * boolean
     *
     * true:
     *     El cliente existe y la actualización se ha
     *     confirmado correctamente.
     *
     * false:
     *     No se ha encontrado el cliente.
     *
     * Si ocurre un error en la base de datos,
     * se propagará una excepción.
     *
     * DIFERENCIA CON JDBC:
     *
     * Antes ejecutábamos una sentencia UPDATE.
     *
     * Ahora recuperamos la entidad gestionada
     * y modificamos sus atributos.
     */
    public boolean actualizar(ClienteEntidad nuevo) {

        /*
         * Ejecutamos la actualización dentro de una transacción.
         *
         * Como la lambda devuelve true o false,
         * TransaccionesORM.ejecutar() devolverá un Boolean
         * que Java puede convertir automáticamente a boolean.
         *
         * A esto se le llama unboxing.
         */
        return TransaccionesORM.ejecutar(em -> {

            /*
             * ====================================================
             * PASO 1: BUSCAR EL CLIENTE EXISTENTE
             * ====================================================
             *
             * em.find() busca una entidad por su clave primaria.
             *
             * Recibe dos argumentos:
             *
             * ClienteEntidad.class:
             *     Tipo de entidad que queremos recuperar.
             *
             * nuevo.getId():
             *     Identificador del cliente que queremos buscar.
             *
             * Si existe, Hibernate devuelve una entidad
             * gestionada por el EntityManager actual.
             *
             * Si no existe, devuelve null.
             *
             * La variable actual contendrá la entidad
             * que está asociada al contexto de persistencia.
             */
            ClienteEntidad actual = em.find(
                    ClienteEntidad.class,
                    nuevo.getId()
            );

            /*
             * ====================================================
             * PASO 2: COMPROBAR SI EXISTE
             * ====================================================
             *
             * Si find() devuelve null, significa que no se
             * ha encontrado ningún cliente con ese ID.
             *
             * En ese caso devolvemos false.
             *
             * Aunque la lambda termine aquí,
             * TransaccionesORM finalizará su transacción
             * y cerrará el EntityManager.
             */
            if (actual == null) {
                return false;
            }

            /*
             * ====================================================
             * PASO 3: MODIFICAR LOS ATRIBUTOS
             * ====================================================
             *
             * Ahora tenemos dos objetos diferentes:
             *
             * nuevo:
             *     Contiene los valores que queremos guardar.
             *
             * actual:
             *     Es la entidad gestionada por Hibernate.
             *
             * Copiamos los valores de nuevo a actual.
             *
             * Utilizamos los métodos setter de la entidad.
             */
            actual.setNombre(nuevo.getNombre());

            actual.setEmail(nuevo.getEmail());

            actual.setActivo(nuevo.isActivo());

            /*
             * ====================================================
             * ¿DÓNDE ESTÁ EL UPDATE?
             * ====================================================
             *
             * No necesitamos escribir:
             *
             * UPDATE cliente SET ...
             *
             * Tampoco necesitamos llamar a persist(actual).
             *
             * La entidad actual ya está gestionada.
             *
             * Hibernate utiliza un mecanismo denominado:
             *
             * DIRTY CHECKING
             *
             * Consiste en detectar modificaciones realizadas
             * sobre entidades gestionadas.
             *
             * Al sincronizar el contexto de persistencia,
             * Hibernate comprueba los cambios y genera
             * el SQL necesario.
             *
             * Por ejemplo, podría ejecutar:
             *
             * UPDATE cliente
             * SET nombre = ?, email = ?, activo = ?
             * WHERE id = ?
             *
             * La sentencia exacta depende de Hibernate
             * y de su configuración.
             *
             * Finalmente, TransaccionesORM ejecutará commit().
             */

            /*
             * Devolvemos true porque hemos encontrado
             * el cliente y solicitado su actualización.
             *
             * El método ejecutar() confirmará la transacción
             * antes de devolver este resultado al DAO.
             *
             * Si commit() falla, se propagará una excepción
             * en lugar de devolver true normalmente.
             */
            return true;
        });
    }

    /*
     * ========================================================
     * MÉTODO eliminar()
     * ========================================================
     *
     * OBJETIVO:
     *
     * Eliminar un cliente utilizando su identificador.
     *
     * PARÁMETRO:
     *
     * int id
     *
     * Identificador del cliente que queremos eliminar.
     *
     * VALOR DEVUELTO:
     *
     * true:
     *     El cliente existe y la eliminación se confirma.
     *
     * false:
     *     No existe ningún cliente con ese ID.
     *
     * Si PostgreSQL impide la eliminación, por ejemplo
     * debido a una restricción de clave foránea,
     * se propagará una excepción.
     */
    public boolean eliminar(int id) {

        /*
         * Ejecutamos la eliminación dentro de una transacción.
         *
         * TransaccionesORM gestionará begin(), commit(),
         * rollback() y el cierre del EntityManager.
         */
        return TransaccionesORM.ejecutar(em -> {

            /*
             * ====================================================
             * PASO 1: BUSCAR LA ENTIDAD
             * ====================================================
             *
             * Antes de eliminar un cliente necesitamos
             * recuperar su entidad gestionada.
             *
             * em.find() devuelve:
             *
             * - Un ClienteEntidad si existe.
             * - null si no existe.
             *
             * Buscamos por la clave primaria id.
             */
            ClienteEntidad actual = em.find(
                    ClienteEntidad.class,
                    id
            );

            /*
             * ====================================================
             * PASO 2: COMPROBAR SU EXISTENCIA
             * ====================================================
             *
             * Si el cliente no existe, no hay nada que eliminar.
             *
             * Devolvemos false.
             */
            if (actual == null) {
                return false;
            }

            /*
             * ====================================================
             * PASO 3: ELIMINAR LA ENTIDAD
             * ====================================================
             *
             * em.remove() solicita la eliminación
             * de una entidad gestionada.
             *
             * IMPORTANTE:
             *
             * remove() debe recibir una entidad gestionada
             * por el EntityManager actual.
             *
             * Por eso primero utilizamos em.find().
             *
             * Después de llamar a remove(), la entidad
             * queda marcada como eliminada.
             *
             * Hibernate generará la sentencia DELETE
             * cuando sincronice el contexto.
             *
             * Una sentencia SQL equivalente sería:
             *
             * DELETE FROM cliente
             * WHERE id = ?
             *
             * No necesitamos construirla manualmente.
             *
             * La eliminación se confirmará cuando
             * TransaccionesORM ejecute commit().
             */
            em.remove(actual);

            /*
             * Devolvemos true porque hemos encontrado
             * el cliente y solicitado su eliminación.
             *
             * El resultado se entregará al llamador
             * después de confirmar la transacción.
             */
            return true;
        });
    }
}
