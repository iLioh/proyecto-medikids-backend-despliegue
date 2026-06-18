package com.medikids.medikids.utils.config;

import com.medikids.medikids.process.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtService.validateToken(token, email)) {
                    Map<String, Object> claims = jwtService.extractAllClaims(token);
                    Integer idRol = (Integer) claims.get("id_rol");

                    String role = switch (idRol != null ? idRol : 0) {
                        case 1 -> "ROLE_CLIENTE";
                        case 2 -> "ROLE_MEDICO";
                        case 3 -> "ROLE_ADMIN";
                        default -> "ROLE_USER";
                    };

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                email, null,
                                org.springframework.security.core.authority.AuthorityUtils.createAuthorityList(role)
                            );

                    Object webDetails = new WebAuthenticationDetailsSource().buildDetails(request);
                    Object rawPermisos = claims.get("permisos");
                    List<String> permisos = (rawPermisos instanceof List) ? (List<String>) rawPermisos : List.of();
                    Map<String, Object> extendedDetails = new HashMap<>();
                    extendedDetails.put("web", webDetails);
                    extendedDetails.put("permisos", permisos);
                    extendedDetails.put("id", claims.get("id"));
                    extendedDetails.put("id_rol", claims.get("id_rol"));
                    authToken.setDetails(extendedDetails);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Error al validar JWT token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
