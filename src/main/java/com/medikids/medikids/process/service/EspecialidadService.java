package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.EspecialidadRequest;
import com.medikids.medikids.process.domain.Especialidad;
import com.medikids.medikids.process.dto.EspecialidadDto;
import com.medikids.medikids.process.repository.EspecialidadRepository;
import com.medikids.medikids.utils.helpers.EspecialidadHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @PostConstruct
    public void seed() {
        if (especialidadRepository.count() > 0) return;
        especialidadRepository.saveAll(List.of(
                Especialidad.builder().nombre("Pediatría General").descripcion("Atención pediátrica general").build(),
                Especialidad.builder().nombre("Neurología Pediátrica").descripcion("Trastornos neurológicos infantiles").build(),
                Especialidad.builder().nombre("Odontopediatría").descripcion("Salud dental infantil").build(),
                Especialidad.builder().nombre("Dermatología Pediátrica").descripcion("Enfermedades de la piel en niños").build(),
                Especialidad.builder().nombre("Cardiología Pediátrica").descripcion("Enfermedades del corazón infantiles").build(),
                Especialidad.builder().nombre("Oftalmología Pediátrica").descripcion("Salud visual infantil").build(),
                Especialidad.builder().nombre("Psicología Infantil").descripcion("Salud mental y emocional infantil").build()
        ));
    }

    public List<EspecialidadDto> getAll() {
        return EspecialidadHelper.mapAll(especialidadRepository.findAll());
    }

    public EspecialidadDto getById(int id) {
        Optional<Especialidad> especialidad = especialidadRepository.findById(id);
        return especialidad.map(EspecialidadHelper::mapEspecialidad).orElse(null);
    }

    public EspecialidadDto save(EspecialidadRequest especialidad) {
        return EspecialidadHelper.mapEspecialidad(
                especialidadRepository.save(EspecialidadHelper.buildEspecialidad(especialidad))
        );
    }

    public EspecialidadDto update(int id, EspecialidadRequest especialidad) {
        Optional<Especialidad> especialidadUpdate = especialidadRepository.findById(id);
        if (especialidadUpdate.isPresent()) {
            especialidadUpdate.get().setNombre(especialidad.getNombre());
            especialidadUpdate.get().setDescripcion(especialidad.getDescripcion());
            especialidadUpdate.get().setPrecio(especialidad.getPrecio());

            return EspecialidadHelper.mapEspecialidad(especialidadRepository.save(especialidadUpdate.get()));
        }
        return null;
    }
}
