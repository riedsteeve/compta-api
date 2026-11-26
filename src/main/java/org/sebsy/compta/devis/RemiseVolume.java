package org.sebsy.compta.devis;

import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;

public interface RemiseVolume {

    @Primary
    BigDecimal appliquer(BigDecimal prixHt, int quantite);

}
