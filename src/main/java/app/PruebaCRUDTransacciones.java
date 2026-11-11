
package app;

// Importamos nuestra clase de utilidad para obtener EntityManager.
// JpaUtil se encarga de configurar y abrir el acceso a la persistencia.
import conexion.JpaUtil;

// EntityManager es la interfaz principal de JPA.
// Permite guardar, buscar, actualizar y eliminar entidades.
import jakarta.persistence.EntityManager;

// EntityTransaction permite controlar las transacciones:
// begin(), commit() y rollback().
import jakarta.persistence.EntityTransaction;

// Importamos la entidad que representa a los clientes
// y que está relacionada con una tabla de PostgreSQL.
import modelo.orm.ClienteEntidad;


public class PruebaCRUDTransacciones {

    public static void main(String[] args) {

        // PRUEBA 1:
        // Insertar un cliente y provocar un error para
        // comprobar que rollback() deshace la operación.
        //
        // pruebaInsertar();

        // PRUEBA 2:
        // Insertar un cliente, eliminarlo y confirmar
        // ambas operaciones dentro de una transacción.
        pruebaEliminar();
    }


    // ==========================================================
    // PRUEBA 1: PERSISTENCIA, FLUSH Y ROLLBACK
    // ==========================================================

    public static void pruebaInsertar() {

        // Abrimos un EntityManager mediante nuestra clase JpaUtil.
        //
        // EntityManager gestiona las entidades y su relación
        // con la base de datos.
        //
        // Cada EntityManager tiene su propio contexto
        // de persistencia.
        EntityManager em = JpaUtil.abrir();

        // Obtenemos el objeto que controla la transacción.
        //
        // La transacción agrupa operaciones que deben
        // confirmarse o deshacerse conjuntamente.
        EntityTransaction tx = em.getTransaction();

        try {

            // Creamos un objeto Java de tipo ClienteEntidad.
            //
            // En este momento es un objeto TRANSITORIO:
            // todavía no está gestionado por JPA.
            //
            // El ID es null porque esperamos que lo genere
            // la base de datos o el mecanismo configurado
            // en la entidad.
            ClienteEntidad cliente = new ClienteEntidad(
                    null,
                    "Prueba ORM",
                    "PRUEBAA@PRUEBA.COM",
                    true
            );

            // Comprobamos el identificador antes de persistir.
            //
            // Si el ID es autogenerado, normalmente
            // veremos null en este momento.
            System.out.println(
                    "Valor de ID antes del persist: "
                            + cliente.getId()
            );

            // Iniciamos una transacción.
            //
            // A partir de aquí podemos agrupar operaciones
            // que se confirmarán mediante commit()
            // o se desharán mediante rollback().
            tx.begin();

            // persist() convierte el objeto transitorio
            // en una entidad GESTIONADA por JPA.
            //
            // Hibernate la incorpora al contexto de persistencia
            // y programa su inserción en la base de datos.
            //
            // IMPORTANTE:
            // Dependiendo de la estrategia de generación del ID,
            // Hibernate puede ejecutar el INSERT inmediatamente
            // o esperar hasta flush()/commit().
            em.persist(cliente);

            // Comprobamos el ID después de persist().
            //
            // Si utilizamos GenerationType.IDENTITY,
            // Hibernate normalmente ejecuta el INSERT
            // para obtener el identificador generado.
            //
            // Por eso el ID puede estar disponible
            // incluso antes de llamar a flush().
            System.out.println(
                    "Valor de ID después del persist: "
                            + cliente.getId()
            );

            // flush() sincroniza los cambios pendientes
            // del contexto de persistencia con la base de datos.
            //
            // Puede provocar la ejecución de instrucciones SQL
            // INSERT, UPDATE o DELETE que estaban pendientes.
            //
            // MUY IMPORTANTE:
            // flush() NO confirma la transacción.
            //
            // Aunque se haya ejecutado el INSERT,
            // todavía podemos deshacerlo con rollback().
            em.flush();

            // PROVOCAMOS UN ERROR INTENCIONADAMENTE.
            //
            // Esta excepción simula un problema que aparece
            // después de haber enviado los cambios a PostgreSQL.
            //
            // Al producirse, el programa salta al catch.
            //
            // Como todavía no hemos hecho commit(),
            // podremos ejecutar rollback().
            throw new RuntimeException("ERROR PROVOCADO");

        } catch (RuntimeException e) {

            // Comprobamos si la transacción continúa activa.
            //
            // Es importante hacerlo porque el error podría
            // haberse producido antes de tx.begin()
            // o la transacción podría haber terminado.
            if (tx.isActive()) {

                // rollback() deshace los cambios realizados
                // dentro de la transacción.
                //
                // Aunque flush() haya enviado el INSERT,
                // el cliente NO quedará guardado definitivamente.
                tx.rollback();
            }

            // Volvemos a lanzar la excepción.
            //
            // Esto permite que el código que llamó al método
            // conozca el error.
            //
            // En este ejemplo, como no hay otro catch en main,
            // aparecerá la excepción en la consola.
            throw e;

        } finally {

            // Cerramos el EntityManager.
            //
            // finally se ejecuta tanto si la operación
            // termina correctamente como si aparece un error.
            //
            // Es importante liberar los recursos asociados
            // al contexto de persistencia.
            em.close();
        }
    }


    // ==========================================================
    // PRUEBA 2: PERSISTENCIA, ELIMINACIÓN Y COMMIT
    // ==========================================================

    public static void pruebaEliminar() {

        // Abrimos un nuevo EntityManager.
        //
        // Este método utiliza su propio contexto
        // de persistencia, independiente del anterior.
        EntityManager em = JpaUtil.abrir();

        // Obtenemos el controlador de transacciones.
        EntityTransaction tx = em.getTransaction();

        try {

            // Iniciamos la transacción.
            //
            // Todas las operaciones siguientes formarán parte
            // de la misma unidad de trabajo.
            tx.begin();

            // Creamos un nuevo cliente.
            //
            // Todavía es un objeto transitorio.
            ClienteEntidad cliente = new ClienteEntidad(
                    null,
                    "Prueba ORM eliminación",
                    "PRUEBAE@PRUEBA.COM",
                    true
            );

            // Registramos el cliente en el contexto
            // de persistencia.
            //
            // A partir de este momento, Hibernate
            // gestiona su estado.
            em.persist(cliente);

            // Sincronizamos los cambios con PostgreSQL.
            //
            // Si el INSERT estaba pendiente, Hibernate
            // lo ejecutará durante esta sincronización.
            //
            // No se ha realizado todavía commit().
            em.flush();

            // Recuperamos el identificador asignado
            // al nuevo cliente.
            Integer id = cliente.getId();

            // Mostramos el ID para comprobar que
            // Hibernate ha generado o recuperado
            // el identificador correctamente.
            System.out.println(
                    "ID de cliente: " + id
            );

            // Marcamos la entidad para su eliminación.
            //
            // remove() se aplica a entidades gestionadas.
            //
            // En este caso funciona porque cliente
            // fue incorporado al contexto mediante persist().
            //
            // Hibernate gestionará la eliminación
            // al sincronizar la transacción.
            em.remove(cliente);

            // Confirmamos definitivamente la transacción.
            //
            // Hibernate sincroniza los cambios pendientes
            // y solicita a PostgreSQL que confirme
            // la transacción.
            //
            // En esta prueba:
            // 1. Se ha insertado el cliente.
            // 2. Se ha solicitado su eliminación.
            // 3. Se confirman las operaciones.
            //
            // Resultado final:
            // El cliente no debe existir en la tabla.
            tx.commit();

        } catch (RuntimeException e) {

            // Si ocurre cualquier error durante
            // la transacción, comprobamos si sigue activa.
            if (tx.isActive()) {

                // Deshacemos los cambios no confirmados.
                //
                // De esta manera evitamos dejar
                // una operación incompleta.
                tx.rollback();
            }

            // Propagamos la excepción.
            throw e;

        } finally {

            // Cerramos siempre el EntityManager
            // para liberar los recursos.
            em.close();
        }
    }
}
