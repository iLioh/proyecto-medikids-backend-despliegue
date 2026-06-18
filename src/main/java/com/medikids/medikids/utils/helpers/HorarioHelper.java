package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.process.domain.Horario;
import com.medikids.medikids.process.dto.HorarioDto;

import java.util.List;
import java.util.stream.Collectors;

public class HorarioHelper {

    private HorarioHelper() {
        throw new IllegalStateException("HorarioHelper class");
    }

    public static HorarioDto mapHorario(Horario horario) {
        int medicoId = horario.getMedico_id() != null ? horario.getMedico_id() : horario.getId_medico();
        return HorarioDto.builder()
                .id_horario(horario.getId_horario())
                .fecha(horario.getFecha())
                .hora_inicio(horario.getHora_inicio())
                .hora_fin(horario.getHora_fin())
                .disponible(horario.getDisponible())
                .id_medico(medicoId)
                .build();
    }

    public static List<HorarioDto> mapAll(List<Horario> horarios) {
        return horarios.stream()
                .map(HorarioHelper::mapHorario)
                .collect(Collectors.toList());
    }
}
