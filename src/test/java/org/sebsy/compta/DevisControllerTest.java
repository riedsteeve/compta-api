package org.sebsy.compta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

/** TP 02. The amounts assume RemiseVolume is the default discount (5 % from 10 units). */
@SpringBootTest
@AutoConfigureMockMvc
class DevisControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void parParametres() {
        assertThat(mvc.get().uri("/api/devis?prixHt=74.99&quantite=3"))
                .hasStatus(HttpStatus.OK)
                .bodyJson().isLenientlyEqualTo("""
                        {"quantite":3,"ht":224.97,"ttc":269.96}""");
    }

    @Test
    void quantiteParDefaut() {
        assertThat(mvc.get().uri("/api/devis?prixHt=2.25"))
                .hasStatus(HttpStatus.OK)
                .bodyJson().isLenientlyEqualTo("""
                        {"quantite":1,"ht":2.25,"ttc":2.70}""");
    }

    @Test
    void parVariableDeChemin() {
        assertThat(mvc.get().uri("/api/devis/3?prixHt=74.99"))
                .hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$.ht").isEqualTo(224.97);
    }

    @Test
    void remiseDeVolumeParDefaut() {
        assertThat(mvc.get().uri("/api/devis?prixHt=2.25&quantite=10"))
                .hasStatus(HttpStatus.OK)
                .bodyJson().isLenientlyEqualTo("""
                        {"quantite":10,"ht":21.38,"ttc":25.66}""");
    }

    @Test
    void prixManquant() {
        assertThat(mvc.get().uri("/api/devis?quantite=3")).hasStatus(HttpStatus.BAD_REQUEST);
    }
}
