package org.sebsy.compta.service;

import org.sebsy.compta.domain.Article;

import java.math.BigDecimal;

/** What a client sees of an article: its supplier by name, never the entity and its graph. */
public record ArticleDto(Long id, String ref, String designation, BigDecimal prix, String fournisseur) {
    static ArticleDto of(Article a) {
        return new ArticleDto(a.getId(), a.getRef(), a.getDesignation(), a.getPrix(),
                a.getFournisseur().getNom());
    }
}
