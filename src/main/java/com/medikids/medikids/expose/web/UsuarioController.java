package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.UsuarioRequest;
import com.medikids.medikids.expose.model.request.PasswordRequest;
import com.medikids.medikids.process.dto.UsuarioDto;
import com.medikids.medikids.process.service.UsuarioService;
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
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @SuppressWarnings("unchecked")
    @GetMapping("/all")
    @PreAuthorize("@permiso.has('usuario:read')")
    public List<UsuarioDto> all(Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 3 || role == 4) {
            return usuarioService.getAll();
        }
        return List.of();
    }

    @GetMapping("/getBy/{id}")
    @PreAuthorize("@permiso.has('usuario:read') and @owner.sameUser(#id)")
    public ResponseEntity<UsuarioDto> getById(@PathVariable int id) {
        UsuarioDto usuarioDto = usuarioService.getById(id);
        if (Objects.nonNull(usuarioDto)) {
            return ResponseEntity.ok(usuarioDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/save")
    public UsuarioDto save(@RequestBody UsuarioRequest usuario) {
        return usuarioService.save(usuario);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@permiso.has('usuario:write')")
    public ResponseEntity<UsuarioDto> update(@PathVariable int id, @RequestBody UsuarioRequest usuario) {
        UsuarioDto usuarioDto = usuarioService.update(id, usuario);
        if (Objects.nonNull(usuarioDto)) {
            return ResponseEntity.ok(usuarioDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioDto> updateProfile(@RequestBody UsuarioRequest usuario, Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int userId = (int) details.get("id");
        UsuarioDto dto = usuarioService.updateProfile(userId, usuario);
        if (dto != null) return ResponseEntity.ok(dto);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/password/{id}")
    @PreAuthorize("@permiso.has('usuario:write')")
    public ResponseEntity<Void> changePassword(@PathVariable int id, @RequestBody PasswordRequest request) {
        if (usuarioService.changePassword(id, request.getCurrentPassword(), request.getNewPassword())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@permiso.has('usuario:write')")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        Boolean isDelete = usuarioService.delete(id);
        if (isDelete)
            return ResponseEntity.ok().build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
