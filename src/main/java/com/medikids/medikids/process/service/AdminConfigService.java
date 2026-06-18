package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.AdminConfig;
import com.medikids.medikids.process.repository.AdminConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HexFormat;

@Service
public class AdminConfigService {

    private static final String CLAVE_SECRET_PATH = "secret_path";
    private static final int HASH_BYTES = 16;

    @Autowired
    private AdminConfigRepository adminConfigRepository;

    @PostConstruct
    public void init() {
        if (adminConfigRepository.findByClave(CLAVE_SECRET_PATH).isEmpty()) {
            String hash = generarHash();
            AdminConfig config = AdminConfig.builder()
                    .clave(CLAVE_SECRET_PATH)
                    .valor(hash)
                    .build();
            adminConfigRepository.save(config);
        }
    }

    public String getSecretPath() {
        return adminConfigRepository.findByClave(CLAVE_SECRET_PATH)
                .map(AdminConfig::getValor)
                .orElse(null);
    }

    public boolean validateHash(String hash) {
        String stored = getSecretPath();
        return stored != null && stored.equals(hash);
    }

    private String generarHash() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[HASH_BYTES];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
