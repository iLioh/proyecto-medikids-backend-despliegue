package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.UsuarioRequest;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.dto.RolDto;
import com.medikids.medikids.process.dto.UsuarioDto;
import com.medikids.medikids.process.repository.RolRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import com.medikids.medikids.utils.helpers.RolHelper;
import com.medikids.medikids.utils.helpers.UsuarioHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Enriquece un UsuarioDto con los datos de su Rol
    private UsuarioDto enriquecer(UsuarioDto dto) {
        rolRepository.findById(dto.getId_rol()).ifPresent(rol ->
                dto.setRol(RolHelper.mapRol(rol))
        );
        return dto;
    }

    public List<UsuarioDto> getAll() {
        return UsuarioHelper.mapAll(usuarioRepository.findAll()).stream()
                .map(this::enriquecer)
                .collect(Collectors.toList());
    }

    public UsuarioDto getById(int id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(u -> enriquecer(UsuarioHelper.mapUsuario(u))).orElse(null);
    }

    public UsuarioDto save(UsuarioRequest usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return enriquecer(UsuarioHelper.mapUsuario(usuarioRepository.save(UsuarioHelper.buildUsuario(usuario))));
    }

    public UsuarioDto update(int id, UsuarioRequest usuario) {
        Optional<Usuario> usuarioUpdate = usuarioRepository.findById(id);
        if (usuarioUpdate.isPresent()) {
            usuarioUpdate.get().setId_rol(usuario.getId_rol());
            usuarioUpdate.get().setNombres(usuario.getNombres());
            usuarioUpdate.get().setApellidos(usuario.getApellidos());
            usuarioUpdate.get().setEmail(usuario.getEmail());
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuarioUpdate.get().setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
            usuarioUpdate.get().setTelefono(usuario.getTelefono());
            usuarioUpdate.get().setFecha_modificado(new Date());
            return enriquecer(UsuarioHelper.mapUsuario(usuarioRepository.save(usuarioUpdate.get())));
        }
        return null;
    }

    public UsuarioDto updateProfile(int id, UsuarioRequest usuario) {
        Optional<Usuario> usuarioUpdate = usuarioRepository.findById(id);
        if (usuarioUpdate.isPresent()) {
            usuarioUpdate.get().setNombres(usuario.getNombres());
            usuarioUpdate.get().setApellidos(usuario.getApellidos());
            usuarioUpdate.get().setEmail(usuario.getEmail());
            usuarioUpdate.get().setTelefono(usuario.getTelefono());
            usuarioUpdate.get().setFecha_modificado(new Date());
            return enriquecer(UsuarioHelper.mapUsuario(usuarioRepository.save(usuarioUpdate.get())));
        }
        return null;
    }

    public boolean changePassword(int id, String currentPassword, String newPassword) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent() && passwordEncoder.matches(currentPassword, usuario.get().getPassword())) {
            usuario.get().setPassword(passwordEncoder.encode(newPassword));
            usuario.get().setFecha_modificado(new Date());
            usuarioRepository.save(usuario.get());
            return true;
        }
        return false;
    }

    public Boolean delete(int id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuario.get().setVisible('0');
            usuario.get().setFecha_modificado(new Date());
            usuarioRepository.save(usuario.get());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
