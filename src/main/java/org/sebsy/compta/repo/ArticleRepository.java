package org.sebsy.compta.repo;

import org.sebsy.compta.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    //Afficher les articles d'un fournisseur trié par prix croissant
    List<Article> findByFournisseurIdOrderByPrix(long fournisseurId);

    //Afficher les articles par ref
    Optional<Article> findByRef(String ref);

    // l'existence d'une référence, en boolean
    Boolean existsByRef(String ref);

    //Tous les articles triés par références
    List<Article> findAllByOrderByRefAsc();
}
