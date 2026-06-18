package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.AdminLoginRequest;
import com.medikids.medikids.expose.model.request.BiometriaVerifyRequest;
import com.medikids.medikids.expose.model.response.AuthResponse;
import com.medikids.medikids.process.service.AdminConfigService;
import com.medikids.medikids.process.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminConfigService adminConfigService;

    /**
     * Endpoint de descubrimiento: devuelve el hash secreto.
     */
    @GetMapping("/discover")
    public ResponseEntity<Map<String, String>> discover() {
        String hash = adminConfigService.getSecretPath();
        if (hash == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(Map.of("hash", hash));
    }

    /**
     * Verifica que un hash sea válido (coincida con el almacenado en BD).
     */
    @PostMapping("/admin-hash/verify")
    public ResponseEntity<Void> verifyHash(@RequestBody Map<String, String> body) {
        String hash = body.get("hash");
        if (hash == null || !adminConfigService.validateHash(hash)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AdminLoginRequest request,
                                              HttpServletRequest httpRequest) {
        AuthResponse response = authService.adminLogin(request.getEmail(), request.getPassword(), httpRequest);

        if (Objects.nonNull(response)) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.builder()
                        .message("Credenciales inválidas o no tienes permisos de administrador")
                        .build());
    }

    @PostMapping("/biometria/verify")
    public ResponseEntity<AuthResponse> verifyFace(@RequestBody BiometriaVerifyRequest request,
                                                    HttpServletRequest httpRequest) {
        AuthResponse response = authService.adminLoginFaceVerify(
                request.getEmail(),
                request.getDescriptor(),
                request.getPreAuthToken(),
                httpRequest
        );

        if (Objects.nonNull(response)) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.builder()
                        .message("Verificación facial fallida")
                        .build());
    }

    @PostMapping("/auth/verify-2fa")
    public ResponseEntity<AuthResponse> verify2FA(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        if (email == null || code == null) {
            return ResponseEntity.badRequest().build();
        }

        AuthResponse response = authService.verify2FA(email, code);

        if (Objects.nonNull(response)) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse.builder()
                        .message("Código inválido o expirado")
                        .build());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
