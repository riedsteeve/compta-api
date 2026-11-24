package org.sebsy.compta;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/** TP 01. Needs the compta database of env/: the context only starts once the mapping is validated. */
@SpringBootTest
@AutoConfigureMockMvc
class DemarrageTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void laSanteRepondUp() {
        assertThat(mvc.get().uri("/actuator/health"))
                .hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$.status").isEqualTo("UP");
    }
}
