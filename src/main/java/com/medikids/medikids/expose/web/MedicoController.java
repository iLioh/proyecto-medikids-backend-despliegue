package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.MedicoConUsuarioRequest;
import com.medikids.medikids.expose.model.request.MedicoRequest;
import com.medikids.medikids.expose.model.request.UsuarioRequest;
import com.medikids.medikids.process.domain.Medico;
import com.medikids.medikids.process.dto.MedicoDto;
import com.medikids.medikids.process.dto.UsuarioDto;
import com.medikids.medikids.process.service.MedicoService;
import com.medikids.medikids.process.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/medico")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;
    private final UsuarioService usuarioService;

    @GetMapping("/all")
    @PreAuthorize("@permiso.has('medico:read')")
    public List<MedicoDto> all() {
        return medicoService.getAll();
    }

    @GetMapping("/getBy/{id}")
    @PreAuthorize("@permiso.has('medico:read')")
    public ResponseEntity<MedicoDto> getById(@PathVariable int id) {
        MedicoDto dto = medicoService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("@permiso.has('medico:read')")
    public ResponseEntity<MedicoDto> getByIdUsuario(@PathVariable int idUsuario) {
        MedicoDto dto = medicoService.getByIdUsuario(idUsuario);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/save")
    @PreAuthorize("@permiso.has('medico:write')")
    public MedicoDto save(@RequestBody MedicoRequest request) {
        return medicoService.save(request);
    }

    @PostMapping("/saveWithUser")
    @PreAuthorize("@permiso.has('medico:write')")
    public ResponseEntity<Map<String, Object>> saveWithUser(@RequestBody MedicoConUsuarioRequest request) {
        try {
            UsuarioRequest userReq = new UsuarioRequest();
            userReq.setId_rol(2);
            userReq.setNombres(request.getNombres());
            userReq.setApellidos(request.getApellidos());
            userReq.setEmail(request.getEmail());
            userReq.setPassword(request.getPassword());
            userReq.setTelefono(request.getTelefono());

            UsuarioDto usuario = usuarioService.save(userReq);

            MedicoRequest medicoReq = new MedicoRequest();
            medicoReq.setNro_colegiatura(request.getNro_colegiatura());
            medicoReq.setUrl_foto(request.getUrl_foto() != null ? request.getUrl_foto() : "");
            medicoReq.setGenero(request.getGenero() != null ? request.getGenero() : null);
            medicoReq.setEstado(request.getEstado() != null ? Medico.EstadoMedico.valueOf(request.getEstado()) : Medico.EstadoMedico.activo);
            medicoReq.setId_usuario(usuario.getId_usuario());
            medicoReq.setId_especialidad(request.getId_especialidad());

            MedicoDto medico = medicoService.save(medicoReq);

            Map<String, Object> result = new HashMap<>();
            result.put("medico", medico);
            result.put("usuario", usuario);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("ya está registrado")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", msg));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al registrar médico: " + msg));
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@permiso.has('medico:write')")
    public ResponseEntity<MedicoDto> update(@PathVariable int id, @RequestBody MedicoRequest request) {
        MedicoDto dto = medicoService.update(id, request);
        if (Objects.nonNull(dto)) return ResponseEntity.ok(dto);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/toggle-status/{id}")
    @PreAuthorize("@permiso.has('medico:toggle-status')")
    public ResponseEntity<MedicoDto> toggleStatus(@PathVariable int id) {
        MedicoDto dto = medicoService.toggleStatus(id);
        if (Objects.nonNull(dto)) return ResponseEntity.ok(dto);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@permiso.has('medico:delete')")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        if (medicoService.delete(id))
            return ResponseEntity.ok().build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/especialidad/{especialidad}")
    @PreAuthorize("@permiso.has('medico:read')")
    public List<MedicoDto> getByEspecialidad(@PathVariable String especialidad) {
        return medicoService.getByEspecialidad(especialidad);
    }
}
