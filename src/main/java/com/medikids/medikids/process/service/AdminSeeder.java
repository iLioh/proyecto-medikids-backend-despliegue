package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.secret-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail("legonardo.garcia@gmail.com").isPresent()) return;

        Usuario admin = Usuario.builder()
                .email("legonardo.garcia@gmail.com")
                .password(passwordEncoder.encode(adminPassword))
                .nombres("Admin")
                .apellidos("Medikids")
                .id_rol(3)
                .telefono(999999999)
                .visible('1')
                .build();

        usuarioRepository.save(admin);
    }
}
