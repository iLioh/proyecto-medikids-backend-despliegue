package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.MedicoRequest;
import com.medikids.medikids.process.domain.Medico;
import com.medikids.medikids.process.dto.MedicoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MedicoHelper implements Serializable {

    private MedicoHelper() {
        throw new IllegalStateException("MedicoHelper class");
    }

    // Convierte un medico "domain" a "dto" (sin datos enriquecidos de FKs)
    public static MedicoDto mapMedico(Medico medico) {
        return MedicoDto.builder()
                .id_medico(medico.getId_medico())
                .nro_colegiatura(medico.getNro_colegiatura())
                .url_foto(medico.getUrl_foto())
                .genero(medico.getGenero() != null ? medico.getGenero().name() : null)
                .id_usuario(medico.getId_usuario())
                .id_especialidad(medico.getId_especialidad())
                .activo(medico.getActivo())
                .estado(medico.getEstado() != null ? medico.getEstado() : Medico.EstadoMedico.activo)
                .build();
    }

    // Convierte un medico "request" a "domain"
    public static Medico buildMedico(MedicoRequest medico) {
        return Medico.builder()
                .nro_colegiatura(medico.getNro_colegiatura())
                .url_foto(medico.getUrl_foto())
                .genero(medico.getGenero() != null ? Medico.Genero.valueOf(medico.getGenero()) : null)
                .estado(medico.getEstado() != null ? medico.getEstado() : Medico.EstadoMedico.activo)
                .id_usuario(medico.getId_usuario())
                .id_especialidad(medico.getId_especialidad())
                .activo('1')
                .build();
    }

    // Convierte una lista de medicos "domain" a "dto"
    public static List<MedicoDto> mapAll(List<Medico> medicos) {
        return medicos.stream()
                .map(MedicoHelper::mapMedico)
                .collect(Collectors.toList());
    }

    // Convierte un page de medicos "domain" a "dto"
    public static Page<MedicoDto> mapPage(Page<Medico> medicoPage) {
        List<MedicoDto> medicos = medicoPage.getContent().stream()
                .map(MedicoHelper::mapMedico)
                .collect(Collectors.toList());
        return new PageImpl<>(medicos);
    }
}