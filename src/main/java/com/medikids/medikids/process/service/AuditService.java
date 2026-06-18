package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.IntentoLogin;
import com.medikids.medikids.process.repository.IntentoLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    @Autowired
    private IntentoLoginRepository intentoLoginRepository;

    public void registrarIntento(String email, String ip, boolean exitoso, String tipo) {
        IntentoLogin intento = IntentoLogin.builder()
                .email(email)
                .ipOrigen(ip)
                .exitoso(exitoso)
                .tipo(tipo)
                .build();
        intentoLoginRepository.save(intento);
    }

    public List<IntentoLogin> obtenerUltimos50() {
        return intentoLoginRepository.findTop50ByOrderByFechaIntentoDesc();
    }

    public List<IntentoLogin> obtenerPorTipo(String tipo) {
        return intentoLoginRepository.findByTipoOrderByFechaIntentoDesc(tipo);
    }
}
