package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.RolRequest;
import com.medikids.medikids.process.domain.Rol;
import com.medikids.medikids.process.dto.RolDto;
import com.medikids.medikids.process.repository.RolRepository;
import com.medikids.medikids.utils.helpers.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<RolDto> getAll() {
        return RolHelper.mapAll(rolRepository.findAll());
    }

    public RolDto getById(int id) {
        Optional<Rol> rol = rolRepository.findById(id);
        return rol.map(RolHelper::mapRol).orElse(null);
    }

    public RolDto save(RolRequest rol) {
        return RolHelper.mapRol(rolRepository.save(RolHelper.buildRol(rol)));
    }

    public RolDto update(int id, RolRequest rol) {
        Optional<Rol> rolUpdate = rolRepository.findById(id);
        if (rolUpdate.isPresent()) {
            rolUpdate.get().setNombre_rol(rol.getNombre_rol());
            return RolHelper.mapRol(rolRepository.save(rolUpdate.get()));
        }
        return null;
    }
}