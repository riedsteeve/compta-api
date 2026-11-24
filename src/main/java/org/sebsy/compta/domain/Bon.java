package org.sebsy.compta.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A purchase order, mapped on {@code bon}. Mapping from Java · Accès aux données, TP 09.
 *
 * <p>The order owns its lines (composition): they are saved with it and deleted when they
 * leave it. No collection of orders on Fournisseur: nothing navigates in that direction.
 */
@Entity
@Table(name = "bon")
public class Bon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business key: UNIQUE, and never rewritten once issued.
    @Column(nullable = false, unique = true, updatable = false)
    private int numero;

    // java.time needs no annotation: the Java type says "date and time" (no @Temporal).
    // The column name differs from the field name, so it is given.
    @Column(name = "date_cmde", nullable = false, updatable = false)
    private LocalDateTime dateCommande;

    // STRING, never the ORDINAL default: the name survives a reordering of the enum, and the
    // column is a VARCHAR anyway. ORDINAL fails VALIDATE on it (expecting tinyint).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Statut statut = Statut.BROUILLON;

    // Integer, not int: NULL means "not delivered yet". An int cannot hold it, and Hibernate
    // refuses to load order 7 into one.
    private Integer delai;

    // Same shape as Article.fournisseur: owning side, LAZY, never null.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fou", nullable = false)
    private Fournisseur fournisseur;

    // Composition: a line has no meaning without its order.
    // cascade PERSIST/MERGE saves the lines with the order; orphanRemoval deletes a line removed
    // from this collection, which CascadeType.REMOVE does not.
    // Neither is the ON DELETE CASCADE of fk_compo_bon: that one acts inside MariaDB, unseen by
    // the persistence context.
    // Not final, for the same reason as Fournisseur.articles.
    @OneToMany(mappedBy = "bon", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ligne> lignes = new ArrayList<>();

    protected Bon() {
        // For Hibernate.
    }

    public Bon(int numero, LocalDateTime dateCommande, Fournisseur fournisseur) {
        this.numero = numero;
        this.dateCommande = Objects.requireNonNull(dateCommande, "dateCommande is mandatory");
        this.fournisseur = Objects.requireNonNull(fournisseur, "fournisseur is mandatory");
    }

    public Long getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public Statut getStatut() {
        return statut;
    }

    public Integer getDelai() {
        return delai;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public List<Ligne> getLignes() {
        return List.copyOf(lignes);
    }

    /** Adds a line and returns it. One line per article: refused here before the key refuses it. */
    public Ligne ajouterLigne(Article article, int quantite) {
        Ligne ligne = new Ligne(this, article, quantite);
        if (lignes.contains(ligne)) {
            throw new IllegalStateException("article %s is already on order %d"
                    .formatted(article.getRef(), numero));
        }
        lignes.add(ligne);
        return ligne;
    }

    /** With orphanRemoval, the row is deleted at the next flush. */
    public void retirerLigne(Ligne ligne) {
        lignes.remove(ligne);
    }

    public void valider() {
        if (lignes.isEmpty()) {
            throw new IllegalStateException("an order with no line cannot be validated");
        }
        statut = Statut.VALIDE;
    }

    /** Only a validated order can be delivered; the delay becomes known at that moment. */
    public void livrer(int delaiEnJours) {
        if (statut != Statut.VALIDE) {
            throw new IllegalStateException("only a VALIDE order can be delivered, not " + statut);
        }
        if (delaiEnJours < 0) {
            throw new IllegalArgumentException("delai must be >= 0");
        }
        delai = delaiEnJours;
        statut = Statut.LIVRE;
    }

    /** Order total, computed in memory: right for one order, an N+1 over a list (Accès aux données, TP 12). */
    public BigDecimal getMontant() {
        return lignes.stream().map(Ligne::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Identity on the order number, read through the getter for the same proxy reason as Article. */
    @Override
    public boolean equals(Object other) {
        return other instanceof Bon autre && numero == autre.getNumero();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numero);
    }

    @Override
    public String toString() {
        return "Bon[numero=%d, statut=%s, delai=%s]".formatted(numero, statut, delai);
    }
}
