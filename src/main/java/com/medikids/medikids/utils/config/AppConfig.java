package com.medikids.medikids.utils.config;

import com.medikids.medikids.process.domain.Medico;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.dto.EspecialidadDto;
import com.medikids.medikids.process.dto.MedicoDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public SimpleCache<String, java.util.List<MedicoDto>> medicoListCache() {
        return new SimpleCache<>(30_000);
    }

    @Bean
    public SimpleCache<Integer, Medico> medicoEntityCache() {
        return new SimpleCache<>(30_000);
    }

    @Bean
    public SimpleCache<Integer, Usuario> usuarioEntityCache() {
        return new SimpleCache<>(30_000);
    }

    @Bean
    public SimpleCache<Integer, EspecialidadDto> especialidadDtoCache() {
        return new SimpleCache<>(60_000);
    }
}
