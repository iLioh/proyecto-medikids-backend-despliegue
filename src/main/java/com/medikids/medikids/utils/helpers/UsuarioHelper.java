package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.UsuarioRequest;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.dto.UsuarioDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioHelper implements Serializable {
    private UsuarioHelper() {
        throw new IllegalStateException("UsuarioHelper class");
    }

    // Convierte un usuario "domain" a "dto"
    public static UsuarioDto mapUsuario(Usuario usuario) {
        return UsuarioDto.builder()
                .id_usuario(usuario.getId_usuario())
                .id_rol(usuario.getId_rol())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .telefono(usuario.getTelefono())
                .fecha_registro(usuario.getFecha_registro())
                .fecha_modificado(usuario.getFecha_modificado())
                .visible(usuario.getVisible())
                .activo(usuario.getActivo())
                .build();
    }

    // Convierte un usuario "request" a "domain"
    public static Usuario buildUsuario(UsuarioRequest usuario) {
        return Usuario.builder()
                .id_rol(usuario.getId_rol())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .telefono(usuario.getTelefono())
                .build();
    }

    // Convierte una lista de usuarios "domain" a "dto"
    public static List<UsuarioDto> mapAll(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioHelper::mapUsuario)
                .collect(Collectors.toList());
    }

    // Convierte un page de usuarios "domain" a "dto"
    public static Page<UsuarioDto> mapPage(Page<Usuario> usuarioPage) {
        List<UsuarioDto> usuarios = usuarioPage.getContent().stream()
                .map(UsuarioHelper::mapUsuario)
                .collect(Collectors.toList());
        return new PageImpl<>(usuarios);
    }
}
