package com.medikids.medikids.utils.config;

import com.medikids.medikids.process.domain.Cliente;
import com.medikids.medikids.process.domain.Medico;
import com.medikids.medikids.process.repository.ClienteRepository;
import com.medikids.medikids.process.repository.MedicoRepository;
import com.medikids.medikids.process.repository.PacienteRepository;
import com.medikids.medikids.process.repository.CitaRepository;
import com.medikids.medikids.process.repository.IncidenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("owner")
public class OwnerEvaluator {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    private int getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return -1;
        Object details = auth.getDetails();
        if (!(details instanceof Map)) return -1;
        Object raw = ((Map<?, ?>) details).get("id");
        return raw instanceof Number ? ((Number) raw).intValue() : -1;
    }

    private int getUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return -1;
        Object details = auth.getDetails();
        if (!(details instanceof Map)) return -1;
        Object raw = ((Map<?, ?>) details).get("id_rol");
        return raw instanceof Number ? ((Number) raw).intValue() : -1;
    }

    private boolean isAdmin() {
        int role = getUserRole();
        return role == 3 || role == 4;
    }

    public boolean sameUser(int userId) {
        return isAdmin() || userId == getUserId();
    }

    public boolean ownCliente(int clienteId) {
        if (isAdmin()) return true;
        int userId = getUserId();
        return clienteRepository.findById(clienteId)
                .map(c -> c.getUsuario().getId_usuario() == userId)
                .orElse(false);
    }

    public boolean ownClienteUsuario(int idUsuario) {
        if (isAdmin()) return true;
        return idUsuario == getUserId();
    }

    public boolean ownPaciente(int pacienteId) {
        if (isAdmin()) return true;
        int userId = getUserId();
        return pacienteRepository.findById(pacienteId)
                .flatMap(p -> clienteRepository.findById(p.getId_cliente()))
                .map(c -> c.getUsuario().getId_usuario() == userId)
                .orElse(false);
    }

    public boolean ownCita(int citaId) {
        if (isAdmin()) return true;
        int userId = getUserId();
        int role = getUserRole();
        var optCita = citaRepository.findById(citaId);
        if (optCita.isEmpty()) return false;
        var cita = optCita.get();
        if (role == 2) {
            return medicoRepository.findByIdUsuario(userId)
                    .map(m -> m.getId_medico() == cita.getId_medico())
                    .orElse(false);
        }
        return pacienteRepository.findById(cita.getId_paciente())
                .flatMap(p -> clienteRepository.findById(p.getId_cliente()))
                .map(c -> c.getUsuario().getId_usuario() == userId)
                .orElse(false);
    }

    public boolean ownMedico(int medicoId) {
        if (isAdmin()) return true;
        int userId = getUserId();
        return medicoRepository.findByIdUsuario(userId)
                .map(m -> m.getId_medico() == medicoId)
                .orElse(false);
    }

    public boolean ownHorarioByMedico(int medicoId) {
        if (isAdmin()) return true;
        int role = getUserRole();
        if (role == 1) return true;
        int userId = getUserId();
        return medicoRepository.findByIdUsuario(userId)
                .map(m -> m.getId_medico() == medicoId)
                .orElse(false);
    }

    public boolean ownIncidente(int incidenteId) {
        if (isAdmin()) return true;
        int userId = getUserId();
        return incidenteRepository.findById(incidenteId)
                .flatMap(i -> medicoRepository.findById(i.getId_medico()))
                .map(m -> m.getId_usuario() == userId)
                .orElse(false);
    }
}
