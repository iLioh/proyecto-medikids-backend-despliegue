package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.MedicoRequest;
import com.medikids.medikids.process.domain.Medico;
import com.medikids.medikids.process.domain.Medico.EstadoMedico;
import com.medikids.medikids.process.dto.MedicoDto;
import com.medikids.medikids.process.repository.EspecialidadRepository;
import com.medikids.medikids.process.repository.MedicoRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import com.medikids.medikids.utils.config.SimpleCache;
import com.medikids.medikids.utils.helpers.EspecialidadHelper;
import com.medikids.medikids.utils.helpers.MedicoHelper;
import com.medikids.medikids.utils.helpers.UsuarioHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private SimpleCache<String, List<MedicoDto>> medicoListCache;

    // Enriquece un solo MedicoDto (usado para getById, save, update)
    private MedicoDto enriquecer(MedicoDto dto) {
        usuarioRepository.findById(dto.getId_usuario()).ifPresent(usuario ->
                dto.setUsuario(UsuarioHelper.mapUsuario(usuario))
        );
        especialidadRepository.findById(dto.getId_especialidad()).ifPresent(especialidad ->
                dto.setEspecialidad(EspecialidadHelper.mapEspecialidad(especialidad))
        );
        return dto;
    }

    // Enriquece una lista de MedicoDto en batch
    private List<MedicoDto> enriquecerBatch(List<Medico> medicos) {
        if (medicos.isEmpty()) return Collections.emptyList();

        List<MedicoDto> dtos = MedicoHelper.mapAll(medicos);

        Set<Integer> usuarioIds = new HashSet<>();
        Set<Integer> especialidadIds = new HashSet<>();
        for (Medico m : medicos) {
            usuarioIds.add(m.getId_usuario());
            especialidadIds.add(m.getId_especialidad());
        }

        var usuarioMap = usuarioRepository.findAllById(usuarioIds).stream()
                .collect(Collectors.toMap(u -> u.getId_usuario(), UsuarioHelper::mapUsuario));

        var especialidadMap = especialidadRepository.findAllById(especialidadIds).stream()
                .collect(Collectors.toMap(e -> e.getId_especialidad(), EspecialidadHelper::mapEspecialidad));

        for (MedicoDto dto : dtos) {
            dto.setUsuario(usuarioMap.get(dto.getId_usuario()));
            dto.setEspecialidad(especialidadMap.get(dto.getId_especialidad()));
        }

        return dtos;
    }

    public MedicoDto getByIdUsuario(int idUsuario) {
        Optional<Medico> medico = medicoRepository.findByIdUsuario(idUsuario);
        return medico.map(m -> enriquecer(MedicoHelper.mapMedico(m))).orElse(null);
    }

    public List<MedicoDto> getAll() {
        List<MedicoDto> cached = medicoListCache.get("all");
        if (cached != null) return cached;
        List<MedicoDto> result = enriquecerBatch(medicoRepository.findAll());
        medicoListCache.put("all", result);
        return result;
    }

    public MedicoDto getById(int id) {
        Optional<Medico> medico = medicoRepository.findById(id);
        return medico.map(m -> enriquecer(MedicoHelper.mapMedico(m))).orElse(null);
    }

    public MedicoDto save(MedicoRequest medico) {
        MedicoDto result = enriquecer(MedicoHelper.mapMedico(
                medicoRepository.save(MedicoHelper.buildMedico(medico))
        ));
        medicoListCache.invalidate("all");
        return result;
    }

    public MedicoDto update(int id, MedicoRequest medico) {
        Optional<Medico> medicoUpdate = medicoRepository.findById(id);
        if (medicoUpdate.isPresent()) {
            medicoUpdate.get().setNro_colegiatura(medico.getNro_colegiatura());
            medicoUpdate.get().setUrl_foto(medico.getUrl_foto());
            medicoUpdate.get().setGenero(medico.getGenero() != null ? Medico.Genero.valueOf(medico.getGenero()) : null);
            medicoUpdate.get().setEstado(medico.getEstado() != null ? medico.getEstado() : Medico.EstadoMedico.activo);
            medicoUpdate.get().setId_especialidad(medico.getId_especialidad());

            MedicoDto result = enriquecer(MedicoHelper.mapMedico(
                    medicoRepository.save(medicoUpdate.get())
            ));
            medicoListCache.invalidate("all");
            return result;
        }
        return null;
    }

    public MedicoDto toggleStatus(int id) {
        Optional<Medico> medicoOpt = medicoRepository.findById(id);
        if (medicoOpt.isEmpty()) return null;

        Medico medico = medicoOpt.get();
        if (medico.getActivo() == '1') {
            medico.setActivo('0');
            medico.setEstado(Medico.EstadoMedico.inactivo);
        } else {
            medico.setActivo('1');
            medico.setEstado(Medico.EstadoMedico.activo);
        }
        MedicoDto result = enriquecer(MedicoHelper.mapMedico(medicoRepository.save(medico)));
        medicoListCache.invalidate("all");
        return result;
    }

    public boolean delete(int id) {
        if (medicoRepository.existsById(id)) {
            medicoRepository.deleteById(id);
            medicoListCache.invalidate("all");
            return true;
        }
        return false;
    }

    public List<MedicoDto> getByEspecialidad(String especialidad) {
        return MedicoHelper.mapAll(
                medicoRepository.findAll().stream()
                        .filter(medico -> medico.getId_especialidad() == Integer.parseInt(especialidad))
                        .toList()
        ).stream().map(this::enriquecer).collect(Collectors.toList());
    }
}
