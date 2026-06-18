package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.HistorialClinicoRequest;
import com.medikids.medikids.process.domain.HistorialClinico;
import com.medikids.medikids.process.dto.HistorialClinicoDto;
import com.medikids.medikids.process.repository.CitaRepository;
import com.medikids.medikids.process.repository.HistorialClinicoRepository;
import com.medikids.medikids.process.repository.PacienteRepository;
import com.medikids.medikids.utils.helpers.CitaHelper;
import com.medikids.medikids.utils.helpers.HistorialClinicoHelper;
import com.medikids.medikids.utils.helpers.PacienteHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistorialClinicoService {

    @Autowired
    private HistorialClinicoRepository historialClinicoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    // Enriquece un HistorialClinicoDto con los datos de Cita y Paciente
    private HistorialClinicoDto enriquecer(HistorialClinicoDto dto) {
        citaRepository.findById(dto.getId_cita()).ifPresent(cita ->
                dto.setCita(CitaHelper.mapCita(cita))
        );
        pacienteRepository.findById(dto.getId_paciente()).ifPresent(paciente ->
                dto.setPaciente(PacienteHelper.mapPaciente(paciente))
        );
        return dto;
    }

    public List<HistorialClinicoDto> getAll() {
        return HistorialClinicoHelper.mapAll(historialClinicoRepository.findAll()).stream()
                .map(this::enriquecer)
                .collect(Collectors.toList());
    }

    public HistorialClinicoDto getById(int id) {
        Optional<HistorialClinico> historialClinico = historialClinicoRepository.findById(id);
        return historialClinico.map(h -> enriquecer(HistorialClinicoHelper.mapHistorialClinico(h))).orElse(null);
    }

    public HistorialClinicoDto save(HistorialClinicoRequest historialClinico) {
        return enriquecer(HistorialClinicoHelper.mapHistorialClinico(
                historialClinicoRepository.save(HistorialClinicoHelper.buildHistorialClinico(historialClinico))
        ));
    }

    public HistorialClinicoDto update(int id, HistorialClinicoRequest historialClinico) {
        Optional<HistorialClinico> historialUpdate = historialClinicoRepository.findById(id);
        if (historialUpdate.isPresent()) {
            historialUpdate.get().setDiagnostico(historialClinico.getDiagnostico());
            historialUpdate.get().setTratamiento(historialClinico.getTratamiento());
            historialUpdate.get().setObservaciones(historialClinico.getObservaciones());
            historialUpdate.get().setFecha_registro(historialClinico.getFecha_registro());
            historialUpdate.get().setId_cita(historialClinico.getId_cita());
            historialUpdate.get().setId_paciente(historialClinico.getId_paciente());
            return enriquecer(HistorialClinicoHelper.mapHistorialClinico(
                    historialClinicoRepository.save(historialUpdate.get())
            ));
        }
        return null;
    }
}