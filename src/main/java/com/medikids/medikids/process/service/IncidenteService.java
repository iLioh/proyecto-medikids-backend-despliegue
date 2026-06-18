package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.IncidenteRequest;
import com.medikids.medikids.expose.model.request.IncidenteRespuestaRequest;
import com.medikids.medikids.process.domain.Incidente;
import com.medikids.medikids.process.dto.IncidenteDto;
import com.medikids.medikids.process.repository.IncidenteRepository;
import com.medikids.medikids.process.repository.MedicoRepository;
import com.medikids.medikids.utils.helpers.IncidenteHelper;
import com.medikids.medikids.utils.helpers.MedicoHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.medikids.medikids.process.repository.IncidenteRepository;

@Service
public class IncidenteService {

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    // Enriquece un IncidenteDto con los datos de su Medico
    private IncidenteDto enriquecer(IncidenteDto dto) {
        medicoRepository.findById(dto.getId_medico()).ifPresent(medico ->
                dto.setMedico(MedicoHelper.mapMedico(medico))
        );
        return dto;
    }

    public List<IncidenteDto> getAll() {
        return IncidenteHelper.mapAll(incidenteRepository.findAll()).stream()
                .map(this::enriquecer)
                .collect(Collectors.toList());
    }

    public List<IncidenteDto> getByMedico(int idMedico) {
        return IncidenteHelper.mapAll(incidenteRepository.findByIdMedico(idMedico)).stream()
                .map(this::enriquecer)
                .collect(Collectors.toList());
    }

    public IncidenteDto getById(int id) {
        Optional<Incidente> incidente = incidenteRepository.findById(id);
        return incidente.map(i -> enriquecer(IncidenteHelper.mapIncidente(i))).orElse(null);
    }

    public IncidenteDto save(IncidenteRequest incidente) {
        return enriquecer(IncidenteHelper.mapIncidente(
                incidenteRepository.save(IncidenteHelper.buildIncidente(incidente))
        ));
    }

    public IncidenteDto update(int id, IncidenteRequest incidente) {
        Optional<Incidente> incidenteUpdate = incidenteRepository.findById(id);
        if (incidenteUpdate.isPresent()) {
            incidenteUpdate.get().setTipo_incidente(incidente.getTipo_incidente());
            incidenteUpdate.get().setDescripcion(incidente.getDescripcion());
            incidenteUpdate.get().setId_medico(incidente.getId_medico());
            return enriquecer(IncidenteHelper.mapIncidente(
                    incidenteRepository.save(incidenteUpdate.get())
            ));
        }
        return null;
    }

    public IncidenteDto responder(int id, IncidenteRespuestaRequest request) {
        Optional<Incidente> incidenteUpdate = incidenteRepository.findById(id);
        if (incidenteUpdate.isPresent()) {
            incidenteUpdate.get().setRespuesta_admin(request.getRespuesta_admin());
            return enriquecer(IncidenteHelper.mapIncidente(
                    incidenteRepository.save(incidenteUpdate.get())
            ));
        }
        return null;
    }
}