package org.example.util;

import org.example.model.Reservation;
import org.example.model.Salle;
import org.example.model.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;

public class DataInitializer {

    private final EntityManager em;

    public DataInitializer(EntityManager em) {
        this.em = em;
    }

    public void initData() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Utilisateur yassine = new Utilisateur();
        yassine.setNomComplet("Yassine El Fassi");
        yassine.setEmail("yassine@univ.ma");
        em.persist(yassine);

        Utilisateur salma = new Utilisateur();
        salma.setNomComplet("Salma Bennani");
        salma.setEmail("salma@univ.ma");
        em.persist(salma);

        Utilisateur mehdi = new Utilisateur();
        mehdi.setNomComplet("Mehdi Tazi");
        mehdi.setEmail("mehdi@univ.ma");
        em.persist(mehdi);

        Utilisateur nadia = new Utilisateur();
        nadia.setNomComplet("Nadia Chraibi");
        nadia.setEmail("nadia@univ.ma");
        em.persist(nadia);

        Salle atlas = new Salle();
        atlas.setNom("Atlas");
        atlas.setCapaciteAccueil(20);
        atlas.setBatiment("Batiment A");
        em.persist(atlas);

        Salle sahara = new Salle();
        sahara.setNom("Sahara");
        sahara.setCapaciteAccueil(12);
        sahara.setBatiment("Batiment B");
        em.persist(sahara);

        Salle ourika = new Salle();
        ourika.setNom("Ourika");
        ourika.setCapaciteAccueil(15);
        ourika.setBatiment("Batiment A");
        em.persist(ourika);

        Reservation r1 = new Reservation();
        r1.setUtilisateur(yassine);
        r1.setSalle(atlas);
        r1.setDateDebut(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0));
        r1.setDateFin(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));
        r1.setMotif("Cours Python");
        em.persist(r1);

        Reservation r2 = new Reservation();
        r2.setUtilisateur(salma);
        r2.setSalle(sahara);
        r2.setDateDebut(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0));
        r2.setDateFin(LocalDateTime.now().plusDays(2).withHour(16).withMinute(0));
        r2.setMotif("Reunion projet");
        em.persist(r2);

        Reservation r3 = new Reservation();
        r3.setUtilisateur(mehdi);
        r3.setSalle(ourika);
        r3.setDateDebut(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0));
        r3.setDateFin(LocalDateTime.now().plusDays(3).withHour(12).withMinute(0));
        r3.setMotif("Formation JEE");
        em.persist(r3);

        Reservation r4 = new Reservation();
        r4.setUtilisateur(nadia);
        r4.setSalle(atlas);
        r4.setDateDebut(LocalDateTime.now().plusDays(4).withHour(9).withMinute(0));
        r4.setDateFin(LocalDateTime.now().plusDays(4).withHour(10).withMinute(30));
        r4.setMotif("Soutenance");
        em.persist(r4);

        tx.commit();
    }
}
