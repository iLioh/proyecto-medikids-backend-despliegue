package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.IpAutorizadaRequest;
import com.medikids.medikids.process.dto.IpAutorizadaDto;
import com.medikids.medikids.process.service.IpAutorizadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/ip-autorizada")
@RequiredArgsConstructor
public class IpAutorizadaController {

    private final IpAutorizadaService ipAutorizadaService;

    @GetMapping
    @PreAuthorize("@permiso.has('ip:read')")
    public List<IpAutorizadaDto> all() {
        return ipAutorizadaService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permiso.has('ip:read')")
    public ResponseEntity<IpAutorizadaDto> getById(@PathVariable int id) {
        IpAutorizadaDto ipAutorizadaDto = ipAutorizadaService.getById(id);
        if (Objects.nonNull(ipAutorizadaDto)) {
            return ResponseEntity.ok(ipAutorizadaDto);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("@permiso.has('ip:read')")
    public List<IpAutorizadaDto> getByUsuario(@PathVariable int idUsuario) {
        return ipAutorizadaService.getByUsuario(idUsuario);
    }

    @PostMapping
    @PreAuthorize("@permiso.has('ip:write')")
    public IpAutorizadaDto save(@RequestBody IpAutorizadaRequest request) {
        return ipAutorizadaService.save(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permiso.has('ip:write')")
    public ResponseEntity<IpAutorizadaDto> update(@PathVariable int id, @RequestBody IpAutorizadaRequest request) {
        IpAutorizadaDto ipAutorizadaDto = ipAutorizadaService.update(id, request);
        if (Objects.nonNull(ipAutorizadaDto)) {
            return ResponseEntity.ok(ipAutorizadaDto);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permiso.has('ip:write')")
    public ResponseEntity<Boolean> delete(@PathVariable int id) {
        Boolean deleted = ipAutorizadaService.delete(id);
        if (Boolean.TRUE.equals(deleted)) {
            return ResponseEntity.ok(Boolean.TRUE);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}