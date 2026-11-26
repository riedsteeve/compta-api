package org.sebsy.compta.service;

import org.sebsy.compta.domain.Article;
import org.sebsy.compta.repo.ArticleRepository;
import org.sebsy.compta.repo.FournisseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogueService {
    private final ArticleRepository articles;
    private final FournisseurRepository fournisseurs;

    public CatalogueService(ArticleRepository articleRepository, FournisseurRepository fournisseurRepository) {
        this.articles = articleRepository;
        this.fournisseurs = fournisseurRepository;
    }

    public List <ArticleDto> parFournisseur(Long id){
        if (!fournisseurs.existsById(id)) throw new Introuvable("Fournisseur " + id + " introuvable");
        return articles.findByFournisseurIdOrderByPrix(id).stream().map(ArticleDto::of).toList();
    }

    public List <ArticleDto> tous(){
        return articles.findAllByOrderByRefAsc().stream().map(ArticleDto::of).toList();
    }
}
