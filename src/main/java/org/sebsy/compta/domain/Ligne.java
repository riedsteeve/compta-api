package org.sebsy.compta.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * One line of a purchase order: the {@code compo} table, mapped as an association entity.
 * Mapping from Java · Accès aux données, TP 09.
 *
 * <p>{@code compo} carries {@code qte}, so {@code @ManyToMany} has nowhere to put it. A line is
 * also a business concept with its own rules (quantity > 0, one line per article and order).
 */
@Entity
@Table(name = "compo")
public class Ligne {

    @EmbeddedId
    private LigneId id;

    // @MapsId takes the name of the KEY COMPONENT ("articleId"), not the column ("id_art"):
    // the association feeds that component, so key and association cannot disagree.
    @MapsId("articleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_art")
    private Article article;

    @MapsId("bonId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_bon")
    private Bon bon;

    // Domain name in Java, inherited name in the table.
    @Column(name = "qte", nullable = false)
    private int quantite;

    protected Ligne() {
        // For Hibernate.
    }

    // Package-private: a line is created by Bon.ajouterLigne, never on its own.
    Ligne(Bon bon, Article article, int quantite) {
        this.bon = Objects.requireNonNull(bon, "bon is mandatory");
        this.article = Objects.requireNonNull(article, "article is mandatory");
        setQuantite(quantite);
        // A new order has no id yet: Hibernate fills both components from the associations
        // at persist time, through @MapsId. The instance only has to exist.
        this.id = new LigneId(article.getId(), bon.getId());
    }

    public LigneId getId() {
        return id;
    }

    public Article getArticle() {
        return article;
    }

    public Bon getBon() {
        return bon;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("quantite must be > 0 (CHECK constraint ck_compo_qte)");
        }
        this.quantite = quantite;
    }

    /** Line total. Fine for one order; a report over every order is aggregated in SQL (Accès aux données, TP 10). */
    public BigDecimal getMontant() {
        return article.getPrix().multiply(BigDecimal.valueOf(quantite));
    }

    // Identity is the pair (article, order), known before persist, unlike the generated key
    // of a new order. Delegated to Article and Bon, whose equals read the other side through
    // getters: proxy-safe, at the cost of loading an uninitialised article to compare it.
    @Override
    public boolean equals(Object other) {
        return other instanceof Ligne autre
                && article.equals(autre.getArticle())
                && bon.equals(autre.getBon());
    }

    @Override
    public int hashCode() {
        return Objects.hash(article, bon);
    }

    @Override
    public String toString() {
        return "Ligne[article=%s, qte=%d]".formatted(article.getRef(), quantite);
    }
}
