package org.sebsy.compta.controllers;

import org.sebsy.compta.devis.RemiseVolume;
import org.sebsy.compta.service.DevisService;
import org.sebsy.compta.service.DevisServices;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/devis")
public class DevisController {
    private final DevisService devisService;
    private final RemiseVolume remise;

    public DevisController (DevisService devisService, RemiseVolume remise){
        this.devisService = devisService;
        this.remise = remise;
    }

    @GetMapping()
    DevisServices.Devis calculer (@RequestParam BigDecimal prixHt, @RequestParam(defaultValue = "1") int quantite){
        return devisService.calculer(prixHt, quantite);
    }

    @GetMapping("/{quantite}")
    BigDecimal parChemin (@PathVariable int quantite , @RequestParam BigDecimal prixHt){
        return remise.appliquer(prixHt, quantite);
    }

}
