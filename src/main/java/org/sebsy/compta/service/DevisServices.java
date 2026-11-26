package org.sebsy.compta.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.sebsy.compta.devis.RemiseVolume;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("remise")
public class DevisServices implements DevisService {

    private static final BigDecimal TVA = new BigDecimal("1.20");
    private final RemiseVolume remise;

    DevisServices(RemiseVolume remise) {
        this.remise = remise;
    }

    public Devis calculer(BigDecimal prixHt, int quantite) {
        Objects.requireNonNull(prixHt, "prixHt is mandatory");
        if (prixHt.signum() < 0) {
            throw new IllegalArgumentException("Le prix doit etre supérieur a 0");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("quantite doit etre supérieur a 0");
        }
        BigDecimal brut = prixHt.multiply(BigDecimal.valueOf(quantite));
        BigDecimal ht = remise.appliquer(brut, quantite).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ttc = ht.multiply(TVA).setScale(2, RoundingMode.HALF_UP);
        return new Devis(quantite, ht, ttc);
    }

    public record Devis(int quantite, BigDecimal ht, BigDecimal ttc) {
    }
}
