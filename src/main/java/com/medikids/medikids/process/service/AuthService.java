package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.response.AuthResponse;
import com.medikids.medikids.process.domain.RefreshToken;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.domain.PasswordResetToken;
import com.medikids.medikids.process.repository.PasswordResetTokenRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import com.medikids.medikids.utils.helpers.IpUtils;
import com.medikids.medikids.utils.helpers.UsuarioHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private BiometriaService biometriaService;

    @Value("${codigo.2fa.expiration}")
    private long codigoExpiracionMs;

    @Transactional
    public void requestPasswordReset(String email) {
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            String token = java.util.UUID.randomUUID().toString();

            passwordResetTokenRepository.findByUsuario(usuario)
                    .ifPresent(passwordResetTokenRepository::delete);
            passwordResetTokenRepository.flush();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .usuario(usuario)
                    .expiryDate(java.time.LocalDateTime.now().plusMinutes(15))
                    .build();
            passwordResetTokenRepository.save(resetToken);
            emailService.enviarEnlaceRecuperacion(email, token);
        }
    }

    @Transactional
    public boolean validateAndResetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
        passwordResetTokenRepository.delete(resetToken);
        return true;
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredPasswordResetTokens() {
        passwordResetTokenRepository.deleteByExpiryDateBefore(java.time.LocalDateTime.now());
    }

    // ── 2FA DESACTIVADO ────────────────────────────────────────────────────
    // Para reactivar 2FA:
    //   1. Descomentar el método login() de abajo
    //   2. Comentar el método login() actual que retorna token directo
    //   3. En el frontend LoginPage.jsx, descomentar las secciones /* 2FA */
    // ────────────────────────────────────────────────────────────────────────

    // ── Login SIN 2FA (Reemplazado por Login 2FA) ──────────────────────────
    /* public AuthResponse login(String email, String password, HttpServletRequest httpRequest) { ... } */
    // ────────────────────────────────────────────────────────────────────────


    /**
     * Login exclusivo para administradores (rol=3 o rol=4).
     * Si el usuario tiene biometría registrada, requiere verificación facial.
     * Si no, usa 2FA por email (superadmin principal).
     */
    public AuthResponse adminLogin(String email, String password, HttpServletRequest httpRequest) {
        String clientIp = IpUtils.getClientIp(httpRequest);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            auditService.registrarIntento(email, clientIp, false, "ADMIN");
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getVisible() != '1' || Boolean.FALSE.equals(usuario.getActivo()) || (usuario.getId_rol() != 3 && usuario.getId_rol() != 4)) {
            auditService.registrarIntento(email, clientIp, false, "ADMIN");
            return null;
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            auditService.registrarIntento(email, clientIp, false, "ADMIN");
            return null;
        }

        if (biometriaService.existsByUsuarioId(usuario.getId_usuario())) {
            String preAuthToken = generarTokenHex32();

            usuario.setCodigoVerificacion(preAuthToken);
            usuario.setCodigoExpiracion(new Date(System.currentTimeMillis() + 300000));
            usuarioRepository.save(usuario);

            auditService.registrarIntento(email, clientIp, true, "ADMIN_BIOM_PENDING");

            return AuthResponse.builder()
                    .message("VERIFICACION_FACIAL_REQUERIDA")
                    .preAuthToken(preAuthToken)
                    .build();
        }

        String codigo = generarCodigo6Digitos();
        usuario.setCodigoVerificacion(codigo);
        usuario.setCodigoExpiracion(new Date(System.currentTimeMillis() + codigoExpiracionMs));
        usuarioRepository.save(usuario);

        emailService.enviarCodigo2FA(email, codigo);

        auditService.registrarIntento(email, clientIp, true, "ADMIN_2FA_PENDING");

        return AuthResponse.builder()
                .message("Código de verificación enviado al correo: " + ocultarEmail(email))
                .build();
    }

    /**
     * Paso 2 del login admin con biometría: verifica el descriptor facial y genera JWT.
     */
    public AuthResponse adminLoginFaceVerify(String email, List<Double> descriptor,
                                              String preAuthToken, HttpServletRequest httpRequest) {
        String clientIp = IpUtils.getClientIp(httpRequest);
        String fingerprint = computeFingerprint(httpRequest);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getCodigoVerificacion() == null || usuario.getCodigoExpiracion() == null) {
            return null;
        }

        if (new Date().after(usuario.getCodigoExpiracion())) {
            usuario.setCodigoVerificacion(null);
            usuario.setCodigoExpiracion(null);
            usuarioRepository.save(usuario);
            return null;
        }

        if (!usuario.getCodigoVerificacion().equals(preAuthToken)) {
            return null;
        }

        if (!biometriaService.verify(usuario.getId_usuario(), descriptor)) {
            auditService.registrarIntento(email, clientIp, false, "ADMIN_BIOM_FAIL");
            return null;
        }

        usuario.setCodigoVerificacion(null);
        usuario.setCodigoExpiracion(null);
        usuarioRepository.save(usuario);

        String token = jwtService.generateAdminToken(usuario);
        auditService.registrarIntento(email, clientIp, true, "ADMIN_BIOM_OK");

        String refreshToken = refreshTokenService.createRefreshToken(usuario.getId_usuario(), fingerprint, clientIp).getToken();

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .message("Acceso administrativo autorizado con biometria")
                .usuario(UsuarioHelper.mapUsuario(usuario))
                .build();
    }

    // ── Login CON 2FA ─────────────────────────────────────────────────────
    public AuthResponse login(String email, String password, HttpServletRequest httpRequest) {
        String clientIp = IpUtils.getClientIp(httpRequest);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getVisible() != '1') {
            return null;
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return null;
        }

        String codigo = generarCodigo6Digitos();

        usuario.setCodigoVerificacion(codigo);
        usuario.setCodigoExpiracion(new Date(System.currentTimeMillis() + codigoExpiracionMs));
        usuarioRepository.save(usuario);

        emailService.enviarCodigo2FA(email, codigo);

        return AuthResponse.builder()
                .message("Código de verificación enviado al correo: " + ocultarEmail(email))
                .build();
    }
    // ──────────────────────────────────────────────────────────────────────


    /**
     * Paso 2 del login: Verifica el código 2FA y genera JWT token.
     *
     * @param email Correo electrónico del usuario
     * @param code  Código de verificación de 6 dígitos
     * @return AuthResponse con JWT token y datos del usuario, o null si código inválido
     */
    public AuthResponse verify2FA(String email, String code) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getCodigoVerificacion() == null || usuario.getCodigoExpiracion() == null) {
            return null;
        }

        if (new Date().after(usuario.getCodigoExpiracion())) {
            usuario.setCodigoVerificacion(null);
            usuario.setCodigoExpiracion(null);
            usuarioRepository.save(usuario);
            return null;
        }

        if (!usuario.getCodigoVerificacion().equals(code)) {
            return null;
        }

        usuario.setCodigoVerificacion(null);
        usuario.setCodigoExpiracion(null);
        usuarioRepository.save(usuario);

        boolean isAdmin = usuario.getId_rol() == 3 || usuario.getId_rol() == 4;
        String token = isAdmin ? jwtService.generateAdminToken(usuario) : jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .message("Autenticación exitosa")
                .usuario(UsuarioHelper.mapUsuario(usuario))
                .build();
    }

    public AuthResponse resend2FA(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) return null;

        Usuario usuario = usuarioOpt.get();
        String codigo = generarCodigo6Digitos();

        usuario.setCodigoVerificacion(codigo);
        usuario.setCodigoExpiracion(new Date(System.currentTimeMillis() + codigoExpiracionMs));
        usuarioRepository.save(usuario);

        emailService.enviarCodigo2FA(email, codigo);

        return AuthResponse.builder()
                .message("Código reenviado al correo: " + ocultarEmail(email))
                .build();
    }

    private String generarCodigo6Digitos() {
        SecureRandom random = new SecureRandom();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }

    /**
     * Oculta parcialmente el email para mostrar en la respuesta.
     * Ejemplo: d****o@gmail.com
     */
    private String ocultarEmail(String email) {
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return "**@" + parts[1];
        }
        return parts[0].charAt(0)
                + "*".repeat(parts[0].length() - 2)
                + parts[0].charAt(parts[0].length() - 1)
                + "@" + parts[1];
    }

    // ── Refresh Token ─────────────────────────────────────────────────────

    public AuthResponse refreshToken(String refreshTokenValue, HttpServletRequest httpRequest) {
        String currentFingerprint = computeFingerprint(httpRequest);
        String currentIp = IpUtils.getClientIp(httpRequest);

        Optional<RefreshToken> optRt = refreshTokenService.findByToken(refreshTokenValue);
        if (optRt.isEmpty()) return null;

        RefreshToken rt = optRt.get();

        if (rt.isRevoked()) {
            refreshTokenService.revokeAllUserTokens(rt.getIdUsuario());
            return null;
        }

        if (rt.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            return null;
        }

        if (!refreshTokenService.isSessionValid(rt, currentFingerprint, currentIp)) {
            refreshTokenService.revokeAllUserTokens(rt.getIdUsuario());
            return null;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(rt.getIdUsuario());
        if (usuarioOpt.isEmpty()) return null;

        Usuario usuario = usuarioOpt.get();
        if (usuario.getVisible() != '1' || Boolean.FALSE.equals(usuario.getActivo())) {
            return null;
        }

        String newToken = jwtService.generateToken(usuario);

        refreshTokenService.revokeToken(refreshTokenValue);
        String newRefreshToken = refreshTokenService.createRefreshToken(usuario.getId_usuario(), currentFingerprint, currentIp).getToken();

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .message("Token renovado exitosamente")
                .usuario(UsuarioHelper.mapUsuario(usuario))
                .build();
    }

    public boolean logout(String refreshTokenValue) {
        Optional<RefreshToken> optRt = refreshTokenService.findByToken(refreshTokenValue);
        if (optRt.isEmpty()) return false;

        refreshTokenService.revokeAllUserTokens(optRt.get().getIdUsuario());
        return true;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String generarTokenHex32() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuilder hex = new StringBuilder(32);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b & 0xff));
        }
        return hex.toString();
    }

    private String computeFingerprint(HttpServletRequest request) {
        try {
            String ua = request.getHeader("User-Agent");
            String lang = request.getHeader("Accept-Language");
            String ip = IpUtils.getClientIp(request);
            String raw = (ua != null ? ua : "") + "||" + (lang != null ? lang : "") + "||" + (ip != null ? ip : "");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : hash) {
                hex.append(String.format("%02x", b & 0xff));
            }
            return hex.toString();
        } catch (Exception e) {
            log.warn("Error al calcular fingerprint: {}", e.getMessage());
            return null;
        }
    }

}
