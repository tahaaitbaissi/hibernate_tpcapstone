package org.example.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "nom_complet", nullable = false)
    private String nomComplet;

    @Version
    private int version;

    @OneToMany(mappedBy = "utilisateur", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public Utilisateur() {
    }

    public Utilisateur(String nomComplet, String email) {
        this.nomComplet = nomComplet;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public int getVersion() {
        return version;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
}
