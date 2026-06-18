package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.CitaRequest;
import com.medikids.medikids.process.domain.Paciente;
import com.medikids.medikids.process.dto.CitaDto;
import com.medikids.medikids.process.dto.ClienteDto;
import com.medikids.medikids.process.dto.MedicoDto;
import com.medikids.medikids.process.repository.PacienteRepository;
import com.medikids.medikids.process.service.CitaService;
import com.medikids.medikids.process.service.ClienteService;
import com.medikids.medikids.process.service.MedicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/cita")
@RequiredArgsConstructor
public class CitaController {
    private final CitaService citaService;
    private final ClienteService clienteService;
    private final MedicoService medicoService;
    private final PacienteRepository pacienteRepository;

    @GetMapping("/all")
    @PreAuthorize("@permiso.has('cita:read')")
    public List<CitaDto> all(Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        int userId = (int) details.get("id");
        if (role == 3 || role == 4) {
            return citaService.getAll();
        }
        if (role == 2) {
            MedicoDto medico = medicoService.getByIdUsuario(userId);
            if (medico == null) return List.of();
            return citaService.getByMedico(medico.getId_medico());
        }
        ClienteDto cliente = clienteService.getByIdUsuario(userId);
        if (cliente == null) return List.of();
        return citaService.getByCliente(cliente.getId_cliente());
    }

    @GetMapping("/getBy/{id}")
    @PreAuthorize("@permiso.has('cita:read') and @owner.ownCita(#id)")
    public ResponseEntity<CitaDto> getById(@PathVariable int id) {
        CitaDto citaDto = citaService.getById(id);
        if (Objects.nonNull(citaDto)) {
            return ResponseEntity.ok(citaDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/save")
    @PreAuthorize("@permiso.has('cita:write')")
    public CitaDto save(@RequestBody CitaRequest cita, Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 1) {
            int userId = (int) details.get("id");
            ClienteDto cliente = clienteService.getByIdUsuario(userId);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }
            // Verificar que el paciente pertenece al cliente consultando directamente
            // la tabla paciente (evita el bug de la primera cita donde no hay citas previas)
            boolean ownsPaciente = pacienteRepository
                    .findByIdCliente(cliente.getId_cliente())
                    .stream()
                    .anyMatch(p -> p.getId_paciente() == cita.getId_paciente());
            if (!ownsPaciente) {
                throw new RuntimeException("El paciente no pertenece a tus hijos");
            }
        }
        return citaService.save(cita);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@permiso.has('cita:write') and @owner.ownCita(#id)")
    public ResponseEntity<CitaDto> update(@PathVariable int id, @RequestBody CitaRequest cita) {
        CitaDto citaDto = citaService.update(id, cita);
        if (Objects.nonNull(citaDto)) {
            return ResponseEntity.ok(citaDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/paciente/{id_paciente}")
    @PreAuthorize("@permiso.has('cita:read') and @owner.ownPaciente(#id_paciente)")
    public List<CitaDto> getByPaciente(@PathVariable int id_paciente) {
        return citaService.getByPaciente(id_paciente);
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("@permiso.has('cita:read') and @owner.ownCliente(#idCliente)")
    public List<CitaDto> getByCliente(@PathVariable int idCliente) {
        return citaService.getByCliente(idCliente);
    }

    @PatchMapping("/{id}/asistencia")
    @PreAuthorize("@permiso.has('cita:asistencia') and @owner.ownCita(#id)")
    public ResponseEntity<CitaDto> marcarAsistencia(@PathVariable int id, @RequestParam Character asistencia) {
        CitaDto citaDto = citaService.marcarAsistencia(id, asistencia);
        if (Objects.nonNull(citaDto)) {
            return ResponseEntity.ok(citaDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
