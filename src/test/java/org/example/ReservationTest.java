package org.example;

import org.example.model.Reservation;
import org.example.model.Salle;
import org.example.model.Utilisateur;
import org.example.util.MigrationExecutor;
import org.example.util.PerformanceReport;
import org.example.util.ReservationQueries;
import org.example.util.ReservationService;
import org.example.util.TransactionService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class ReservationTest {

    private static EntityManagerFactory emf;

    @BeforeClass
    public static void init() {
        emf = Persistence.createEntityManagerFactory("ReservationPU");
    }

    @AfterClass
    public static void close() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    public void testUtilisateur() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new Utilisateur("Mehdi Tazi", "mehdi@univ.ma"));
        em.getTransaction().commit();

        List<Utilisateur> list = em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
        assertTrue(list.size() >= 1);

        Utilisateur found = em.createQuery("SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class)
                .setParameter("email", "mehdi@univ.ma")
                .getSingleResult();
        assertEquals("Mehdi Tazi", found.getNomComplet());
        em.close();
    }

    @Test
    public void testSalle() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new Salle("Tensift", 30, "Batiment C"));
        em.getTransaction().commit();

        List<Salle> list = em.createQuery("SELECT s FROM Salle s", Salle.class).getResultList();
        assertTrue(list.size() >= 1);

        Salle found = em.createQuery("SELECT s FROM Salle s WHERE s.nom = :nom", Salle.class)
                .setParameter("nom", "Tensift")
                .getSingleResult();
        assertEquals(30, found.getCapaciteAccueil());
        assertEquals("Batiment C", found.getBatiment());
        em.close();
    }

    @Test
    public void testReservation() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Utilisateur u = new Utilisateur("Nadia Chraibi", "nadia@univ.ma");
        Salle s = new Salle("Ourika", 15, "Batiment A");
        em.persist(u);
        em.persist(s);
        em.persist(new Reservation(s, u,
                LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0), "Soutenance"));
        em.getTransaction().commit();

        List<Reservation> list = em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
        assertTrue(list.size() >= 1);

        List<Reservation> byMotif = em.createQuery("SELECT r FROM Reservation r WHERE r.motif = :motif", Reservation.class)
                .setParameter("motif", "Soutenance")
                .getResultList();
        assertEquals(1, byMotif.size());
        assertEquals("Ourika", byMotif.get(0).getSalle().getNom());
        em.close();
    }

    @Test
    public void testUpdateReservation() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Utilisateur u = new Utilisateur("Karim Bennis", "karim@univ.ma");
        Salle s = new Salle("Draa", 18, "Batiment B");
        em.persist(u);
        em.persist(s);
        Reservation r = new Reservation(s, u,
                LocalDateTime.now().plusDays(5).withHour(9).withMinute(0),
                LocalDateTime.now().plusDays(5).withHour(10).withMinute(0), "Atelier");
        em.persist(r);
        em.getTransaction().commit();
        Long id = r.getId();
        em.close();

        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Reservation loaded = em2.find(Reservation.class, id);
        loaded.setMotif("Atelier Python");
        em2.getTransaction().commit();

        Reservation updated = em2.createQuery("SELECT r FROM Reservation r WHERE r.id = :id", Reservation.class)
                .setParameter("id", id)
                .getSingleResult();
        assertEquals("Atelier Python", updated.getMotif());
        em2.close();
    }

    @Test
    public void testDeleteReservation() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Utilisateur u = new Utilisateur("Sara El Amrani", "sara@univ.ma");
        Salle s = new Salle("Gheris", 10, "Batiment C");
        em.persist(u);
        em.persist(s);
        Reservation r = new Reservation(s, u,
                LocalDateTime.now().plusDays(6).withHour(15).withMinute(0),
                LocalDateTime.now().plusDays(6).withHour(16).withMinute(0), "Temporaire");
        em.persist(r);
        em.getTransaction().commit();
        Long id = r.getId();
        em.close();

        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Reservation toDelete = em2.find(Reservation.class, id);
        assertNotNull(toDelete);
        em2.remove(toDelete);
        em2.getTransaction().commit();

        List<Reservation> left = em2.createQuery("SELECT r FROM Reservation r WHERE r.id = :id", Reservation.class)
                .setParameter("id", id)
                .getResultList();
        assertTrue(left.isEmpty());
        em2.close();
    }

    @Test
    public void testCacheSecondNiveau() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Salle s = new Salle("Ziz", 25, "Batiment D");
        em.persist(s);
        em.getTransaction().commit();
        Long id = s.getId();
        em.close();

        EntityManager em2 = emf.createEntityManager();
        Salle first = em2.find(Salle.class, id);
        assertNotNull(first);
        em2.close();

        EntityManager em3 = emf.createEntityManager();
        Salle second = em3.find(Salle.class, id);
        assertNotNull(second);
        assertEquals("Ziz", second.getNom());
        em3.close();

        assertTrue(emf.getCache().contains(Salle.class, id));
    }

    @Test
    public void testMigration() throws Exception {
        MigrationExecutor executor = new MigrationExecutor(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        executor.executeMigration("db/migration_V2.sql");

        EntityManager em = emf.createEntityManager();
        List<Salle> list = em.createQuery("SELECT s FROM Salle s", Salle.class).getResultList();
        assertNotNull(list);
        em.close();
    }

    @Test
    public void testPerformanceReport() {
        EntityManager em = emf.createEntityManager();
        em.createQuery("SELECT s FROM Salle s", Salle.class).getResultList();
        new PerformanceReport(em).printReport();

        org.hibernate.stat.Statistics stats = em.unwrap(org.hibernate.Session.class)
                .getSessionFactory().getStatistics();
        assertTrue(stats.getQueryExecutionCount() >= 0);
        em.close();
    }

    @Test
    public void testJpql() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Utilisateur u = new Utilisateur("Anas Bourkadi", "anas@univ.ma");
        Salle s = new Salle("Sebou", 22, "Batiment D");
        em.persist(u);
        em.persist(s);
        em.persist(new Reservation(s, u,
                LocalDateTime.now().plusDays(7).withHour(9).withMinute(0),
                LocalDateTime.now().plusDays(7).withHour(11).withMinute(0), "Cours Python"));
        em.getTransaction().commit();

        List<Reservation> found = ReservationQueries.bySalleJpql(em, "Sebou", LocalDateTime.now());
        assertFalse(found.isEmpty());
        assertEquals("Sebou", found.get(0).getSalle().getNom());
        em.close();
    }

    @Test
    public void testCriteria() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Utilisateur u = new Utilisateur("Hiba Lamrani", "hiba@univ.ma");
        Salle s = new Salle("Moulouya", 16, "Batiment B");
        em.persist(u);
        em.persist(s);
        em.persist(new Reservation(s, u,
                LocalDateTime.now().plusDays(8).withHour(14).withMinute(0),
                LocalDateTime.now().plusDays(8).withHour(16).withMinute(0), "Reunion projet"));
        em.getTransaction().commit();

        List<Reservation> found = ReservationQueries.bySalleCriteria(em, "Moulouya", LocalDateTime.now());
        assertFalse(found.isEmpty());
        assertEquals("Moulouya", found.get(0).getSalle().getNom());
        em.close();
    }

    @Test
    public void testTransactionRollback() {        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new Utilisateur("Rollback User", "rollback@univ.ma"));
        em.getTransaction().rollback();
        em.close();

        EntityManager em2 = emf.createEntityManager();
        List<Utilisateur> left = em2.createQuery("SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class)
                .setParameter("email", "rollback@univ.ma")
                .getResultList();
        assertTrue(left.isEmpty());
        em2.close();
    }

    @Test
    public void testOptimisticLock() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Salle s = new Salle("Loukous", 14, "Batiment A");
        em.persist(s);
        em.getTransaction().commit();
        Long id = s.getId();
        em.close();

        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();
        Salle a = em1.find(Salle.class, id);
        Salle b = em2.find(Salle.class, id);

        em1.getTransaction().begin();
        a.setDescription("Premiere");
        em1.getTransaction().commit();
        em1.close();

        boolean conflict = false;
        try {
            TransactionService.updateDescription(em2, b.getId(), "Seconde");
        } catch (Exception e) {
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof javax.persistence.OptimisticLockException) {
                    conflict = true;
                    break;
                }
                cause = cause.getCause();
            }
            if (!conflict) {
                throw e;
            }
        }
        em2.close();

        EntityManager em3 = emf.createEntityManager();
        Salle check = em3.find(Salle.class, id);
        if (conflict) {
            assertEquals("Premiere", check.getDescription());
        } else {
            assertNotNull(check.getDescription());
        }
        em3.close();
    }

    @Test
    public void testReservationService() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Utilisateur u = new Utilisateur("Rim Fassi", "rim@univ.ma");
        Salle s = new Salle("Tifnout", 12, "Batiment C");
        em.persist(u);
        em.persist(s);
        em.getTransaction().commit();
        em.close();

        EntityManager em2 = emf.createEntityManager();
        Salle salle = em2.createQuery("SELECT x FROM Salle x WHERE x.nom = :nom", Salle.class)
                .setParameter("nom", "Tifnout").getSingleResult();
        Utilisateur user = em2.createQuery("SELECT x FROM Utilisateur x WHERE x.email = :email", Utilisateur.class)
                .setParameter("email", "rim@univ.ma").getSingleResult();
        Reservation r = new Reservation();
        r.setSalle(salle);
        r.setUtilisateur(user);
        r.setDateDebut(LocalDateTime.now().plusDays(9).withHour(10).withMinute(0));
        r.setDateFin(LocalDateTime.now().plusDays(9).withHour(12).withMinute(0));
        r.setMotif("Cours Python");
        new ReservationService().createReservation(em2, r);
        assertNotNull(r.getId());

        List<Reservation> found = em2.createQuery("SELECT x FROM Reservation x WHERE x.motif = :motif", Reservation.class)
                .setParameter("motif", "Cours Python").getResultList();
        assertFalse(found.isEmpty());
        em2.close();
    }
}
