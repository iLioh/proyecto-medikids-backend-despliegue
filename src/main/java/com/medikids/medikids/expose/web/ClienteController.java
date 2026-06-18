package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.ClienteRequest;
import com.medikids.medikids.process.dto.ClienteDto;
import com.medikids.medikids.process.service.ClienteService;
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
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @SuppressWarnings("unchecked")
    @GetMapping("")
    @PreAuthorize("@permiso.has('cliente:read')")
    public List<ClienteDto> all(Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 3 || role == 4) {
            return clienteService.getAll();
        }
        int userId = (int) details.get("id");
        ClienteDto propio = clienteService.getByIdUsuario(userId);
        return propio != null ? List.of(propio) : List.of();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permiso.has('cliente:read') and @owner.ownCliente(#id)")
    public ResponseEntity<ClienteDto> getById(@PathVariable int id) {
        ClienteDto clienteDto = clienteService.getById(id);
        if (Objects.nonNull(clienteDto)) {
            return ResponseEntity.ok(clienteDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("@permiso.has('cliente:read') and @owner.ownClienteUsuario(#idUsuario)")
    public ResponseEntity<ClienteDto> getByIdUsuario(@PathVariable int idUsuario) {
        ClienteDto clienteDto = clienteService.getByIdUsuario(idUsuario);
        if (Objects.nonNull(clienteDto)) {
            return ResponseEntity.ok(clienteDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/save")
    public ClienteDto save(@RequestBody ClienteRequest cliente) {
        return clienteService.save(cliente);
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("@permiso.has('cliente:write') and @owner.ownCliente(#id)")
    public ResponseEntity<ClienteDto> update(@PathVariable int id, @RequestBody ClienteRequest cliente) {
        ClienteDto clienteDto = clienteService.update(id, cliente);
        if (Objects.nonNull(clienteDto)) {
            return ResponseEntity.ok(clienteDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("@permiso.has('cliente:read')")
    public List<ClienteDto> getByNombre(@PathVariable String nombre, Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 3 || role == 4) {
            return clienteService.getByNombre(nombre);
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/dni/{dni}")
    @PreAuthorize("@permiso.has('cliente:read')")
    public ResponseEntity<ClienteDto> getByDni(@PathVariable String dni, Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 3 || role == 4) {
            ClienteDto clienteDto = clienteService.getByDni(dni);
            if (Objects.nonNull(clienteDto)) {
                return ResponseEntity.ok(clienteDto);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
