package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.CrearAdminRequest;
import com.medikids.medikids.expose.model.request.UsuarioRequest;
import com.medikids.medikids.expose.model.request.IpAutorizadaRequest;
import com.medikids.medikids.process.domain.IntentoLogin;
import com.medikids.medikids.process.domain.Permiso;
import com.medikids.medikids.process.domain.Rol;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.dto.IpAutorizadaDto;
import com.medikids.medikids.process.dto.UsuarioDto;
import com.medikids.medikids.process.repository.PermisoRepository;
import com.medikids.medikids.process.repository.RolPermisoRepository;
import com.medikids.medikids.process.repository.RolRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import com.medikids.medikids.process.service.AuditService;
import com.medikids.medikids.process.service.IpAutorizadaService;
import com.medikids.medikids.process.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IpAutorizadaService ipAutorizadaService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolPermisoRepository rolPermisoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ── IPs Autorizadas (deprecated: usar /ip-autorizada) ───────────────────

    @GetMapping("/ips")
    @PreAuthorize("@permiso.has('ip:read')")
    public ResponseEntity<List<IpAutorizadaDto>> listarIps() {
        return ResponseEntity.ok(ipAutorizadaService.getAll());
    }

    @PostMapping("/ips")
    @PreAuthorize("@permiso.has('ip:write')")
    public ResponseEntity<IpAutorizadaDto> crearIp(@RequestBody IpAutorizadaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ipAutorizadaService.save(request));
    }

    @PutMapping("/ips/{id}")
    @PreAuthorize("@permiso.has('ip:write')")
    public ResponseEntity<IpAutorizadaDto> actualizarIp(@PathVariable int id, @RequestBody IpAutorizadaRequest request) {
        IpAutorizadaDto dto = ipAutorizadaService.update(id, request);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/ips/{id}")
    @PreAuthorize("@permiso.has('ip:write')")
    public ResponseEntity<Void> eliminarIp(@PathVariable int id) {
        if (ipAutorizadaService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ── Auditoría ────────────────────────────────────────────────────────────

    @GetMapping("/auditoria")
    @PreAuthorize("@permiso.has('auditoria:read')")
    public ResponseEntity<List<IntentoLogin>> verAuditoria() {
        return ResponseEntity.ok(auditService.obtenerUltimos50());
    }

    @GetMapping("/auditoria/{tipo}")
    @PreAuthorize("@permiso.has('auditoria:read')")
    public ResponseEntity<List<IntentoLogin>> verAuditoriaPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(auditService.obtenerPorTipo(tipo));
    }

    // ── Health check ─────────────────────────────────────────────────────────

    @GetMapping("/ping")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Panel administrativo activo"));
    }

    // ── Roles ────────────────────────────────────────────────────────────────

    @GetMapping("/roles")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolRepository.findAll());
    }

    @GetMapping("/roles/{id}/permisos")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<List<String>> permisosDeRol(@PathVariable Integer id) {
        return ResponseEntity.ok(rolPermisoRepository.findCodigosPermisoByIdRol(id));
    }

    @PostMapping("/roles/{id}/permisos")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<Void> asignarPermiso(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        Integer idPermiso = body.get("idPermiso");
        if (idPermiso == null) return ResponseEntity.badRequest().build();

        var rp = new com.medikids.medikids.process.domain.RolPermiso();
        rp.setIdRol(id);
        rp.setIdPermiso(idPermiso);
        rolPermisoRepository.save(rp);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/roles/{id}/permisos/{idPermiso}")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<Void> removerPermiso(@PathVariable Integer id, @PathVariable Integer idPermiso) {
        rolPermisoRepository.findAll().stream()
                .filter(rp -> rp.getIdRol().equals(id) && rp.getIdPermiso().equals(idPermiso))
                .findFirst()
                .ifPresent(rp -> rolPermisoRepository.delete(rp));
        return ResponseEntity.ok().build();
    }

    // ── Permisos ─────────────────────────────────────────────────────────────

    @GetMapping("/permisos")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<List<Permiso>> listarPermisos() {
        return ResponseEntity.ok(permisoRepository.findAll());
    }

    // ── Gestión de Usuarios ──────────────────────────────────────────────────

    @PostMapping("/usuarios")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<UsuarioDto> crearUsuario(@RequestBody CrearAdminRequest request) {
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setId_rol(request.getIdRol());
        usuarioRequest.setEmail(request.getEmail());
        usuarioRequest.setPassword(request.getPassword());
        usuarioRequest.setNombres(request.getNombres());
        usuarioRequest.setApellidos(request.getApellidos());
        usuarioRequest.setTelefono(request.getTelefono());
        UsuarioDto dto = usuarioService.save(usuarioRequest);

        usuarioRepository.findById(dto.getId_usuario()).ifPresent(u -> {
            u.setActivo(false);
            usuarioRepository.save(u);
        });

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/usuarios/{id}/rol")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<?> actualizarRol(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int currentUserId = (int) details.get("id");

        if (id.equals(currentUserId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "No puedes cambiar tu propio rol"));
        }

        Integer idRol = body.get("idRol");
        if (idRol == null) return ResponseEntity.badRequest().build();
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setId_rol(idRol);
            u.setFecha_modificado(new Date());
            usuarioRepository.save(u);
        });
        return ResponseEntity.ok().build();
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/usuarios/{id}/status")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<?> cambiarStatus(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int currentUserId = (int) details.get("id");

        Boolean activo = body.get("activo");
        if (activo == null) return ResponseEntity.badRequest().build();

        if (id.equals(currentUserId) && !activo) {
            return ResponseEntity.badRequest().body(Map.of("message", "No puedes desactivarte a ti mismo"));
        }

        usuarioRepository.findById(id).ifPresent(u -> {
            u.setActivo(activo);
            u.setFecha_modificado(new Date());
            usuarioRepository.save(u);
        });
        return ResponseEntity.ok().build();
    }
}
