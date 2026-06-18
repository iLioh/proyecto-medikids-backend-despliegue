package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.SemanaRequest;
import com.medikids.medikids.process.domain.Horario;
import com.medikids.medikids.process.dto.HorarioDto;
import com.medikids.medikids.process.repository.HorarioRepository;
import com.medikids.medikids.utils.helpers.HorarioHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    public List<HorarioDto> getByMedico(int idMedico) {
        return HorarioHelper.mapAll(horarioRepository.findByMedico(idMedico));
    }

    public List<HorarioDto> getDisponiblesByMedico(int idMedico) {
        return HorarioHelper.mapAll(horarioRepository.findDisponiblesByMedico(idMedico));
    }

    public List<HorarioDto> getHorariosBySemana(int idMedico, LocalDate inicio, LocalDate fin) {
        return HorarioHelper.mapAll(horarioRepository.findByMedicoAndFechaBetween(idMedico, inicio, fin));
    }

    @Transactional
    public void saveSemana(SemanaRequest request) {
        horarioRepository.deleteDisponiblesByMedicoAndFechaBetween(
                request.getId_medico(), request.getInicio(), request.getFin()
        );

        List<Horario> bloques = new ArrayList<>();
        for (SemanaRequest.HorarioBloque bloque : request.getBloques()) {
            Horario h = Horario.builder()
                    .fecha(bloque.getFecha())
                    .hora_inicio(bloque.getHora_inicio())
                    .hora_fin(bloque.getHora_fin())
                    .disponible('1')
                    .medico_id(request.getId_medico())
                    .id_medico(request.getId_medico())
                    .build();
            bloques.add(h);
        }

        horarioRepository.saveAll(bloques);
    }
}
