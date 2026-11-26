package org.sebsy.compta.service;

import java.math.BigDecimal;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class RemiseVolume implements org.sebsy.compta.devis.RemiseVolume {

    static final int MAXCOMMANDE = 10;
    private static final BigDecimal TAUX_APRES_REMISE = new BigDecimal("0.5");

    public BigDecimal appliquer(BigDecimal prixHt, int quantite) {
        return quantite >= MAXCOMMANDE ? prixHt.multiply(TAUX_APRES_REMISE) : prixHt;
    }
}
