package org.sebsy.compta;

import org.junit.jupiter.api.Test;
import org.sebsy.compta.repo.ArticleRepository;
import org.sebsy.compta.service.ArticleDto;
import org.sebsy.compta.service.CatalogueService;
import org.sebsy.compta.service.Introuvable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TP 03. Runs on the data of env/; assertions hold even after articles were added by hand
 * (statement, demo): order and membership, never an exact list.
 */
@SpringBootTest
@Transactional
class CatalogueServiceTest {

    @Autowired
    CatalogueService catalogue;

    @Autowired
    ArticleRepository articles;

    @Test
    void lesArticlesDeDuboisTriesParPrix() {
        var dubois = catalogue.parFournisseur(3L);
        assertThat(dubois).isSortedAccordingTo(Comparator.comparing(ArticleDto::prix));
        assertThat(dubois).extracting(ArticleDto::ref).containsSubsequence("D03", "D01", "D04");
        assertThat(dubois).allSatisfy(a -> assertThat(a.fournisseur()).isEqualTo("Dubois & Fils"));
    }

    @Test
    void unFournisseurInconnu() {
        assertThatThrownBy(() -> catalogue.parFournisseur(99L))
                .isInstanceOf(Introuvable.class)
                .hasMessage("Fournisseur 99 introuvable");
    }

    @Test
    void toutLeCatalogueTrieParReference() {
        // One call only: its SQL is what the statement asks you to read in the log.
        var tous = catalogue.tous();
        assertThat(tous).hasSizeGreaterThanOrEqualTo(12);
        // The database collation decides the order: only the starting references are compared,
        // whatever students added around them.
        assertThat(tous).extracting(ArticleDto::ref).containsSubsequence(
                "A01", "A02", "A03", "A04", "D01", "D03", "D04", "F01", "F02", "F03", "F04", "F05");
        assertThat(tous).extracting(ArticleDto::fournisseur).contains("Française d'Imports", "FDM SA", "Dubois & Fils");
    }

    @Test
    void lesRequetesDerivees() {
        assertThat(articles.findByFournisseurIdOrderByPrix(3L)).isNotEmpty()
                .allSatisfy(a -> assertThat(a.getFournisseur().getId()).isEqualTo(3L));
        assertThat(articles.findByRef("A01")).hasValueSatisfying(a -> assertThat(a.getDesignation()).isEqualTo("Perceuse P1"));
        assertThat(articles.existsByRef("A01")).isTrue();
        assertThat(articles.existsByRef("Z99")).isFalse();
    }
}
