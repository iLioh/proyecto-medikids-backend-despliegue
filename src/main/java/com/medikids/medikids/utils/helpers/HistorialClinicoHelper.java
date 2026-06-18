package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.HistorialClinicoRequest;
import com.medikids.medikids.process.domain.HistorialClinico;
import com.medikids.medikids.process.dto.HistorialClinicoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialClinicoHelper implements Serializable {

    private HistorialClinicoHelper() {
        throw new IllegalStateException("HistorialClinicoHelper class");
    }

    public static HistorialClinicoDto mapHistorialClinico(HistorialClinico historialClinico) {
        return HistorialClinicoDto.builder()
                .id_historial_clinico(historialClinico.getId_historial_clinico())
                .diagnostico(historialClinico.getDiagnostico())
                .tratamiento(historialClinico.getTratamiento())
                .observaciones(historialClinico.getObservaciones())
                .fecha_registro(historialClinico.getFecha_registro())
                .id_cita(historialClinico.getId_cita())
                .id_paciente(historialClinico.getId_paciente())
                .build();
    }

    public static HistorialClinico buildHistorialClinico(HistorialClinicoRequest historialClinico) {
        return HistorialClinico.builder()
                .diagnostico(historialClinico.getDiagnostico())
                .tratamiento(historialClinico.getTratamiento())
                .observaciones(historialClinico.getObservaciones())
                .fecha_registro(historialClinico.getFecha_registro())
                .id_cita(historialClinico.getId_cita())
                .id_paciente(historialClinico.getId_paciente())
                .build();
    }

    public static List<HistorialClinicoDto> mapAll(List<HistorialClinico> historialClinicos) {
        return historialClinicos.stream()
                .map(HistorialClinicoHelper::mapHistorialClinico)
                .collect(Collectors.toList());
    }

    public static Page<HistorialClinicoDto> mapPage(Page<HistorialClinico> historialPage) {
        List<HistorialClinicoDto> historialClinicos = historialPage.getContent().stream()
                .map(HistorialClinicoHelper::mapHistorialClinico)
                .collect(Collectors.toList());
        return new PageImpl<>(historialClinicos);
    }
}