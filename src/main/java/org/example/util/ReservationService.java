package org.example.util;

import org.example.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.logging.Logger;

public class ReservationService {

    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());

    public void createReservation(EntityManager em, Reservation reservation) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            em.persist(reservation);
            tx.commit();
            LOGGER.info("Reservation creee pour " + reservation.getUtilisateur().getEmail());
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.severe("Echec creation reservation : " + e.getMessage());
            throw e;
        }
    }
}
