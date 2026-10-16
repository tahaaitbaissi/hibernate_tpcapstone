package org.example;

import org.example.model.Reservation;
import org.example.model.Salle;
import org.example.util.DataInitializer;
import org.example.util.MigrationExecutor;
import org.example.util.PerformanceReport;
import org.example.util.ReservationQueries;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityManager em2 = null;
        try {
            emf = Persistence.createEntityManagerFactory("ReservationPU");

            new MigrationExecutor("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "")
                    .executeMigration("db/migration_V2.sql");

            em = emf.createEntityManager();
            new DataInitializer(em).initData();
            em.close();

            em2 = emf.createEntityManager();
            Salle s1 = em2.createQuery("SELECT s FROM Salle s WHERE s.nom = :nom", Salle.class)
                    .setParameter("nom", "Atlas").getSingleResult();
            System.out.println("Salle cache 1 : " + s1.getNom());
            em2.close();

            em = emf.createEntityManager();
            Salle s2 = em.find(Salle.class, s1.getId());
            System.out.println("Salle cache 2 : " + s2.getNom());

            List<Reservation> reservations = em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
            for (Reservation r : reservations) {
                System.out.println(r.getMotif() + " : " + r.getSalle().getNom() + " / " + r.getUtilisateur().getNomComplet());
            }
            System.out.println("JPQL Atlas : " + ReservationQueries.bySalleJpql(em, "Atlas", java.time.LocalDateTime.now().minusDays(1)).size());
            System.out.println("Criteria Atlas : " + ReservationQueries.bySalleCriteria(em, "Atlas", java.time.LocalDateTime.now().minusDays(1)).size());
            new PerformanceReport(em).printReport();
        } catch (Exception e) {
            System.out.println("Erreur application : " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (em2 != null && em2.isOpen()) {
                em2.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
}
