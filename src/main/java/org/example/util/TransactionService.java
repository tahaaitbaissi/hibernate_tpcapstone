package org.example.util;

import org.example.model.Salle;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;

public class TransactionService {

    public static void updateDescription(EntityManager em, Long salleId, String description) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Salle salle = em.find(Salle.class, salleId);
            salle.setDescription(description);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public static boolean updateWithRetry(EntityManager em, Long salleId, String description) {
        try {
            updateDescription(em, salleId, description);
            return true;
        } catch (OptimisticLockException ole) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Conflit detecte, veuillez reessayer.");
            return false;
        }
    }
}
