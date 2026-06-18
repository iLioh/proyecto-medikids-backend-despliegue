package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.PacienteRequest;
import com.medikids.medikids.process.dto.ClienteDto;
import com.medikids.medikids.process.dto.PacienteDto;
import com.medikids.medikids.process.service.ClienteService;
import com.medikids.medikids.process.service.PacienteService;
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
@RequestMapping("/paciente")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final ClienteService clienteService;

    @GetMapping("/all")
    @PreAuthorize("@permiso.has('paciente:read')")
    public List<PacienteDto> all(Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 3 || role == 4) {
            return pacienteService.getAll();
        }
        int userId = (int) details.get("id");
        ClienteDto cliente = clienteService.getByIdUsuario(userId);
        if (cliente == null) return List.of();
        return pacienteService.getByIdCliente(cliente.getId_cliente());
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("@permiso.has('paciente:read') and @owner.ownCliente(#idCliente)")
    public List<PacienteDto> byCliente(@PathVariable int idCliente) {
        return pacienteService.getByIdCliente(idCliente);
    }

    @GetMapping("/getBy/{id}")
    @PreAuthorize("@permiso.has('paciente:read') and @owner.ownPaciente(#id)")
    public ResponseEntity<PacienteDto> getById(@PathVariable int id) {
        PacienteDto pacienteDto = pacienteService.getById(id);
        if (Objects.nonNull(pacienteDto)) {
            return ResponseEntity.ok(pacienteDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/save")
    @PreAuthorize("@permiso.has('paciente:write')")
    public PacienteDto save(@RequestBody PacienteRequest paciente, Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 1) {
            int userId = (int) details.get("id");
            ClienteDto cliente = clienteService.getByIdUsuario(userId);
            if (cliente != null) {
                paciente.setId_cliente(cliente.getId_cliente());
            }
        }
        return pacienteService.save(paciente);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@permiso.has('paciente:write') and @owner.ownPaciente(#id)")
    public ResponseEntity<PacienteDto> update(@PathVariable int id, @RequestBody PacienteRequest paciente) {
        PacienteDto pacienteDto = pacienteService.update(id, paciente);
        if (Objects.nonNull(pacienteDto)) {
            return ResponseEntity.ok(pacienteDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}