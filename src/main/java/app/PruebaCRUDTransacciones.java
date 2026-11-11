package app;

import conexion.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.orm.ClienteEntidad;

public class PruebaCRUDTransacciones {
    public static void main(String[] args) {
            //pruebaInsertar();
            pruebaEliminar();
    }

    public static void pruebaInsertar(){
        EntityManager em = JpaUtil.abrir();

        EntityTransaction tx = em.getTransaction();

        try{
            ClienteEntidad cliente = new ClienteEntidad(
                    null,
                    "Prueba ORM",
                    "PRUEBAA@PRUEBA.COM",
                    true
            );

            System.out.println("Valor de ID antes del persist: " + cliente.getId());

            tx.begin();

            em.persist(cliente);

            System.out.println("Valor de ID después del persist: " + cliente.getId());

            em.flush();

            throw new RuntimeException("ERROR PROBOCADO");

        } catch (RuntimeException e){

                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;

        } finally {
            em.close();
        }
    }

    public static void pruebaEliminar(){
        EntityManager em = JpaUtil.abrir();

        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            ClienteEntidad cliente = new ClienteEntidad(
                    null,
                    "Prueba ORM eliminación",
                    "PRUEBAE@PRUEBA.COM",
                    true
            );

            em.persist(cliente);

            em.flush();

            Integer id = cliente.getId();

            System.out.println("ID de cliente: " + id);

            em.remove(cliente);

            tx.commit();

        } catch (RuntimeException e) {
            if(tx.isActive()){
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}


