package com.medikids.medikids;

import com.medikids.medikids.process.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Test de arranque: verifica que el contexto de Spring Boot carga correctamente.
 *
 * EmailService se mockea con @MockitoBean (Spring Boot 4 / Spring Framework 6.2+)
 * para evitar la dependencia de JavaMailSender en el entorno de pruebas.
 * La configuración de BD y exclusión de Mail se maneja en src/test/resources/application.properties.
 */
@SpringBootTest
@DisplayName("Backend MediKids - Test de arranque de contexto")
class BackendMedikidsApplicationTests {

    // Simula EmailService para que Spring no intente crear JavaMailSender en tests
    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("Debe cargar el contexto de Spring Boot correctamente")
    void contextLoads() {
        // Verifica que el contexto de Spring Boot arranca sin errores
    }

}
