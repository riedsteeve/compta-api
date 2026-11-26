package org.sebsy.compta.service;

import java.math.BigDecimal;
import org.sebsy.compta.service.DevisServices.Devis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public interface DevisService {
    Devis calculer(BigDecimal prixHt, int quantite);
}
