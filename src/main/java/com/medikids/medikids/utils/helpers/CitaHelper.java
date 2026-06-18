package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.CitaRequest;
import com.medikids.medikids.process.domain.Cita;
import com.medikids.medikids.process.dto.CitaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CitaHelper implements Serializable {
    private CitaHelper() {
        throw new IllegalStateException("CitaHelper class");
    }

    // Convierte una cita "domain" a "dto"
    public static CitaDto mapCita(Cita cita) {
        return CitaDto.builder()
                .id_cita(cita.getId_cita())
                .motivo(cita.getMotivo())
                .fecha_registro(cita.getFecha_registro())
                .estado(cita.getEstado())
                .asistencia(cita.getAsistencia())
                .comentarios(cita.getComentarios())
                .id_horario(cita.getId_horario())
                .id_medico(cita.getId_medico())
                .id_paciente(cita.getId_paciente())
                .fecha_cita(cita.getFecha_cita() != null ? cita.getFecha_cita().toString() : null)
                .hora_cita(cita.getHora_cita())
                .id_pago(cita.getId_pago() != null ? cita.getId_pago() : 0)
                .build();
    }

    // Convierte una cita "request" a "domain"
    // Convierte una cita "request" a "domain"
    public static Cita buildCita(CitaRequest cita) {
        LocalDate parsedDate = null;

        if (cita.getFecha_cita() != null && !cita.getFecha_cita().isEmpty()) {
            try {
                // Intentamos parsear la fecha normalmente
                parsedDate = LocalDate.parse(cita.getFecha_cita());
            } catch (Exception e) {
                // Si falla (por ejemplo porque viene "NaN-NaN-NaN"),
                // imprimimos el error en consola pero NO detenemos la ejecución.
                System.err.println("Error al parsear fecha: " + cita.getFecha_cita() + ". Usando fecha actual.");
                parsedDate = LocalDate.now();
            }
        }

        return Cita.builder()
                .motivo(cita.getMotivo())
                .estado(cita.getEstado())
                .asistencia(cita.getAsistencia())
                .comentarios(cita.getComentarios())
                .id_horario(cita.getId_horario())
                .id_medico(cita.getId_medico())
                .id_paciente(cita.getId_paciente())
                .fecha_cita(parsedDate)
                .hora_cita(cita.getHora_cita())
                .id_pago(cita.getId_pago() > 0 ? cita.getId_pago() : null)
                .build();
    }

    // Convierte una lista de citas "domain" a "dto"
    public static List<CitaDto> mapAll(List<Cita> citas) {
        return citas.stream()
                .map(CitaHelper::mapCita)
                .collect(Collectors.toList());
    }

    // Convierte un page de citas "domain" a "dto"
    public static Page<CitaDto> mapPage(Page<Cita> citaPage) {
        List<CitaDto> citas = citaPage.getContent().stream()
                .map(CitaHelper::mapCita)
                .collect(Collectors.toList());
        return new PageImpl<>(citas);
    }
}
