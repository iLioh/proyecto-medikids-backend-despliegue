package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.IncidenteRequest;
import com.medikids.medikids.process.domain.Incidente;
import com.medikids.medikids.process.dto.IncidenteDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class IncidenteHelper implements Serializable {
    private IncidenteHelper() {
        throw new IllegalStateException("IncidenteHelper class");
    }

    // Convierte un incidente "domain" a "dto"
    public static IncidenteDto mapIncidente(Incidente incidente) {
        return IncidenteDto.builder()
                .id_incidente(incidente.getId_incidente())
                .tipo_incidente(incidente.getTipo_incidente())
                .descripcion(incidente.getDescripcion())
                .respuesta_admin(incidente.getRespuesta_admin())
                .fecha_registro(incidente.getFecha_registro())
                .id_medico(incidente.getId_medico())
                .build();
    }

    // Convierte un incidente "request" a "domain"
    public static Incidente buildIncidente(IncidenteRequest incidente) {
        return Incidente.builder()
                .tipo_incidente(incidente.getTipo_incidente())
                .descripcion(incidente.getDescripcion())
                .fecha_registro(LocalDateTime.now())
                .id_medico(incidente.getId_medico())
                .build();
    }

    // Convierte una lista de incidentes "domain" a "dto"
    public static List<IncidenteDto> mapAll(List<Incidente> incidentes) {
        return incidentes.stream()
                .map(IncidenteHelper::mapIncidente)
                .collect(Collectors.toList());
    }

    // Convierte un page de incidentes "domain" a "dto"
    public static Page<IncidenteDto> mapPage(Page<Incidente> incidentePage) {
        List<IncidenteDto> incidentes = incidentePage.getContent().stream()
                .map(IncidenteHelper::mapIncidente)
                .collect(Collectors.toList());
        return new PageImpl<>(incidentes);
    }
}