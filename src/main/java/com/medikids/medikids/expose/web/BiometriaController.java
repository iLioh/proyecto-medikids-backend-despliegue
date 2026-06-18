package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.BiometriaEnrollRequest;
import com.medikids.medikids.expose.model.response.BiometriaStatusResponse;
import com.medikids.medikids.process.domain.Biometria;
import com.medikids.medikids.process.repository.BiometriaRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import com.medikids.medikids.process.service.BiometriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/biometria")
public class BiometriaController {

    @Autowired
    private BiometriaService biometriaService;

    @Autowired
    private BiometriaRepository biometriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/enroll")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<Map<String, String>> enroll(@RequestBody BiometriaEnrollRequest request) {
        try {
            biometriaService.enroll(request.getIdUsuario(), request.getDescriptors());

            usuarioRepository.findById(request.getIdUsuario()).ifPresent(u -> {
                u.setActivo(true);
                u.setFecha_modificado(new Date());
                usuarioRepository.save(u);
            });

            return ResponseEntity.ok(Map.of("message", "Biometria registrada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/status/{idUsuario}")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<BiometriaStatusResponse> status(@PathVariable Integer idUsuario) {
        boolean registrado = biometriaService.existsByUsuarioId(idUsuario);
        List<Biometria> biometrics = biometriaRepository.findByUsuarioIdAndActivoTrue(idUsuario);
        var fecha = biometrics.isEmpty() ? null : biometrics.getFirst().getFecha_registro();
        return ResponseEntity.ok(new BiometriaStatusResponse(registrado, fecha));
    }

    @DeleteMapping("/{idUsuario}")
    @PreAuthorize("@permiso.has('rol:assign')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Integer idUsuario) {
        biometriaService.deleteByUsuarioId(idUsuario);
        return ResponseEntity.ok(Map.of("message", "Biometria eliminada correctamente"));
    }
}
