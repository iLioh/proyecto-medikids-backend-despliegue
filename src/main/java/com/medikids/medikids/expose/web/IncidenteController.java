package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.IncidenteRequest;
import com.medikids.medikids.expose.model.request.IncidenteRespuestaRequest;
import com.medikids.medikids.process.dto.IncidenteDto;
import com.medikids.medikids.process.dto.MedicoDto;
import com.medikids.medikids.process.service.IncidenteService;
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
@RequestMapping("/incidente")
@RequiredArgsConstructor
public class IncidenteController {

    private final IncidenteService incidenteService;
    private final MedicoService medicoService;

    @GetMapping("/all")
    @PreAuthorize("@permiso.has('incidente:read')")
    public List<IncidenteDto> all(Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 3 || role == 4) {
            return incidenteService.getAll();
        }
        int userId = (int) details.get("id");
        MedicoDto medico = medicoService.getByIdUsuario(userId);
        if (medico == null) return List.of();
        return incidenteService.getByMedico(medico.getId_medico());
    }

    @GetMapping("/getBy/{id}")
    @PreAuthorize("@permiso.has('incidente:read') and @owner.ownIncidente(#id)")
    public ResponseEntity<IncidenteDto> getById(@PathVariable int id) {
        IncidenteDto incidenteDto = incidenteService.getById(id);
        if (Objects.nonNull(incidenteDto)) {
            return ResponseEntity.ok(incidenteDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/save")
    @PreAuthorize("@permiso.has('incidente:write')")
    public IncidenteDto save(@RequestBody IncidenteRequest incidente, Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 2) {
            int userId = (int) details.get("id");
            MedicoDto medico = medicoService.getByIdUsuario(userId);
            if (medico != null) {
                incidente.setId_medico(medico.getId_medico());
            }
        }
        return incidenteService.save(incidente);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@permiso.has('incidente:write') and @owner.ownIncidente(#id)")
    public ResponseEntity<IncidenteDto> update(@PathVariable int id, @RequestBody IncidenteRequest incidente) {
        IncidenteDto incidenteDto = incidenteService.update(id, incidente);
        if (Objects.nonNull(incidenteDto)) {
            return ResponseEntity.ok(incidenteDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PatchMapping("/responder/{id}")
    @PreAuthorize("@permiso.has('incidente:respond')")
    public ResponseEntity<IncidenteDto> responder(@PathVariable int id, @RequestBody IncidenteRespuestaRequest request) {
        IncidenteDto incidenteDto = incidenteService.responder(id, request);
        if (Objects.nonNull(incidenteDto)) {
            return ResponseEntity.ok(incidenteDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}