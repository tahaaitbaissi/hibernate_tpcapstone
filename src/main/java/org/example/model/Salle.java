package org.example.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salles")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(name = "capacite_accueil", nullable = false)
    private int capaciteAccueil;

    @Column(nullable = false)
    private String batiment;

    private String description;

    @Version
    private int version;

    @OneToMany(mappedBy = "salle", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public Salle() {
    }

    public Salle(String nom, int capaciteAccueil, String batiment) {
        this.nom = nom;
        this.capaciteAccueil = capaciteAccueil;
        this.batiment = batiment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getCapaciteAccueil() {
        return capaciteAccueil;
    }

    public void setCapaciteAccueil(int capaciteAccueil) {
        this.capaciteAccueil = capaciteAccueil;
    }

    public String getBatiment() {
        return batiment;
    }

    public void setBatiment(String batiment) {
        this.batiment = batiment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
}
