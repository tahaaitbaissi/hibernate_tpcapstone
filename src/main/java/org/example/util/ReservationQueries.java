package org.example.util;

import org.example.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationQueries {

    public static List<Reservation> bySalleJpql(EntityManager em, String nomSalle, LocalDateTime date) {
        String jpql = "SELECT r FROM Reservation r WHERE r.salle.nom = :nomSalle AND r.dateDebut > :date";
        return em.createQuery(jpql, Reservation.class)
                .setParameter("nomSalle", nomSalle)
                .setParameter("date", date)
                .getResultList();
    }

    public static List<Reservation> bySalleCriteria(EntityManager em, String nomSalle, LocalDateTime date) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
        Root<Reservation> root = cq.from(Reservation.class);
        Predicate predicate = cb.and(
                cb.equal(root.get("salle").get("nom"), nomSalle),
                cb.greaterThan(root.get("dateDebut"), date)
        );
        cq.select(root).where(predicate);
        return em.createQuery(cq).getResultList();
    }
}
