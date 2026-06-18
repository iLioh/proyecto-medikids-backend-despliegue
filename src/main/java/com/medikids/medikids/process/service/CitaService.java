package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.CitaRequest;
import com.medikids.medikids.process.domain.Cita;
import com.medikids.medikids.process.domain.Especialidad;
import com.medikids.medikids.process.domain.Horario;
import com.medikids.medikids.process.domain.Medico;
import com.medikids.medikids.process.domain.Paciente;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.dto.CitaDto;
import com.medikids.medikids.process.dto.EspecialidadDto;
import com.medikids.medikids.process.dto.MedicoDto;
import com.medikids.medikids.process.dto.PacienteDto;
import com.medikids.medikids.process.dto.UsuarioDto;
import com.medikids.medikids.process.repository.CitaRepository;
import com.medikids.medikids.process.repository.EspecialidadRepository;
import com.medikids.medikids.process.repository.HorarioRepository;
import com.medikids.medikids.process.repository.MedicoRepository;
import com.medikids.medikids.process.repository.PacienteRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import com.medikids.medikids.process.domain.Cliente;
import com.medikids.medikids.process.repository.ClienteRepository;
import com.medikids.medikids.utils.config.SimpleCache;
import com.medikids.medikids.utils.helpers.CitaHelper;
import com.medikids.medikids.utils.helpers.EspecialidadHelper;
import com.medikids.medikids.utils.helpers.MedicoHelper;
import com.medikids.medikids.utils.helpers.PacienteHelper;
import com.medikids.medikids.utils.helpers.PagoHelper;
import com.medikids.medikids.utils.helpers.UsuarioHelper;
import com.medikids.medikids.process.repository.PagoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CitaService {
    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private SimpleCache<Integer, Medico> medicoEntityCache;

    @Autowired
    private SimpleCache<Integer, Usuario> usuarioEntityCache;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private PagoRepository pagoRepository;

    // Enriquece un solo CitaDto (usado para getById, save, update)
    private CitaDto enriquecer(CitaDto dto) {
        medicoRepository.findById(dto.getId_medico()).ifPresent(medico -> {
            MedicoDto medicoDto = MedicoHelper.mapMedico(medico);
            usuarioRepository.findById(medico.getId_usuario()).ifPresent(usuario ->
                    medicoDto.setUsuario(UsuarioHelper.mapUsuario(usuario))
            );
            especialidadRepository.findById(medico.getId_especialidad()).ifPresent(especialidad ->
                    medicoDto.setEspecialidad(EspecialidadHelper.mapEspecialidad(especialidad))
            );
            dto.setMedico(medicoDto);
        });
        pacienteRepository.findById(dto.getId_paciente()).ifPresent(paciente ->
                dto.setPaciente(PacienteHelper.mapPaciente(paciente))
        );
        if (dto.getId_pago() > 0) {
            pagoRepository.findById(dto.getId_pago()).ifPresent(pago ->
                    dto.setPago(PagoHelper.mapPago(pago))
            );
        }
        return dto;
    }

    // Enriquece una lista de CitaDto en batch (reduce N+1 a 5 consultas totales)
    private List<CitaDto> enriquecerBatch(List<Cita> citas) {
        if (citas.isEmpty()) return Collections.emptyList();

        List<CitaDto> dtos = CitaHelper.mapAll(citas);

        Set<Integer> medicoIds = new HashSet<>();
        Set<Integer> pacienteIds = new HashSet<>();
        for (CitaDto dto : dtos) {
            medicoIds.add(dto.getId_medico());
            pacienteIds.add(dto.getId_paciente());
        }

        Set<Integer> medicoIdsToFetch = new HashSet<>();
        Map<Integer, Medico> medicoEntities = new HashMap<>();
        for (Integer mid : medicoIds) {
            Medico cached = medicoEntityCache.get(mid);
            if (cached != null) {
                medicoEntities.put(mid, cached);
            } else {
                medicoIdsToFetch.add(mid);
            }
        }
        if (!medicoIdsToFetch.isEmpty()) {
            for (Medico m : medicoRepository.findAllById(medicoIdsToFetch)) {
                medicoEntities.putIfAbsent(m.getId_medico(), m);
                medicoEntityCache.put(m.getId_medico(), m);
            }
        }

        Set<Integer> usuarioIds = new HashSet<>();
        Set<Integer> especialidadIds = new HashSet<>();
        for (Medico m : medicoEntities.values()) {
            usuarioIds.add(m.getId_usuario());
            especialidadIds.add(m.getId_especialidad());
        }

        Set<Integer> usuarioIdsToFetch = new HashSet<>();
        Map<Integer, Usuario> usuarioEntities = new HashMap<>();
        for (Integer uid : usuarioIds) {
            Usuario cached = usuarioEntityCache.get(uid);
            if (cached != null) {
                usuarioEntities.put(uid, cached);
            } else {
                usuarioIdsToFetch.add(uid);
            }
        }
        if (!usuarioIdsToFetch.isEmpty()) {
            for (Usuario u : usuarioRepository.findAllById(usuarioIdsToFetch)) {
                usuarioEntities.putIfAbsent(u.getId_usuario(), u);
                usuarioEntityCache.put(u.getId_usuario(), u);
            }
        }

        Map<Integer, UsuarioDto> usuarioMap = usuarioEntities.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> UsuarioHelper.mapUsuario(e.getValue())));

        Map<Integer, EspecialidadDto> especialidadMap = especialidadRepository.findAllById(especialidadIds).stream()
                .collect(Collectors.toMap(Especialidad::getId_especialidad, EspecialidadHelper::mapEspecialidad));

        Map<Integer, PacienteDto> pacienteMap = pacienteRepository.findAllById(pacienteIds).stream()
                .collect(Collectors.toMap(Paciente::getId_paciente, PacienteHelper::mapPaciente));

        // Resolver pagos en batch usando id_pago de cada CitaDto
        Set<Integer> pagoIds = dtos.stream()
                .map(CitaDto::getId_pago)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());
        Map<Integer, com.medikids.medikids.process.dto.PagoDto> pagoMap = pagoIds.isEmpty()
                ? Collections.emptyMap()
                : pagoRepository.findAllById(pagoIds).stream()
                        .collect(Collectors.toMap(
                                com.medikids.medikids.process.domain.Pago::getId_pago,
                                PagoHelper::mapPago));

        for (CitaDto dto : dtos) {
            Medico medico = medicoEntities.get(dto.getId_medico());
            if (medico != null) {
                MedicoDto medicoDto = MedicoHelper.mapMedico(medico);
                medicoDto.setUsuario(usuarioMap.get(medico.getId_usuario()));
                medicoDto.setEspecialidad(especialidadMap.get(medico.getId_especialidad()));
                dto.setMedico(medicoDto);
            }
            dto.setPaciente(pacienteMap.get(dto.getId_paciente()));
            if (dto.getId_pago() > 0) {
                dto.setPago(pagoMap.get(dto.getId_pago()));
            }
        }

        return dtos;
    }

    public List<CitaDto> getAll() {
        return enriquecerBatch(citaRepository.findAll());
    }

    public CitaDto getById(int id) {
        Optional<Cita> cita = citaRepository.findById(id);
        return cita.map(c -> enriquecer(CitaHelper.mapCita(c))).orElse(null);
    }

    @Transactional
    public CitaDto save(CitaRequest cita) {
        Horario horario = horarioRepository.findById(cita.getId_horario())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado: " + cita.getId_horario()));

        if (horario.getDisponible() != '1') {
            throw new RuntimeException("El horario seleccionado ya no está disponible");
        }

        if (horario.getFecha() != null && horario.getFecha().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("No se pueden agendar citas en fechas pasadas");
        }

        if (cita.getFecha_cita() == null || cita.getFecha_cita().contains("NaN")) {
            cita.setFecha_cita(LocalDate.now().toString());
        }

        Cita saved = citaRepository.save(CitaHelper.buildCita(cita));

        horario.setDisponible('0');
        horarioRepository.save(horario);

        CitaDto citaDto = enriquecer(CitaHelper.mapCita(saved));

        // Enviar correo de confirmación al responsable del paciente
        try {
            pacienteRepository.findById(cita.getId_paciente()).ifPresent(paciente -> {
                clienteRepository.findById(paciente.getId_cliente()).ifPresent(cliente -> {
                    Usuario usuario = cliente.getUsuario();
                    if (usuario != null && usuario.getEmail() != null) {
                        String nombreMedico = "Médico asignado";
                        String especialidad = "";
                        if (citaDto.getMedico() != null) {
                            if (citaDto.getMedico().getUsuario() != null) {
                                nombreMedico = citaDto.getMedico().getUsuario().getNombres()
                                        + " " + citaDto.getMedico().getUsuario().getApellidos();
                            }
                            if (citaDto.getMedico().getEspecialidad() != null) {
                                especialidad = citaDto.getMedico().getEspecialidad().getNombre();
                            }
                        }

                        // Generar PDF del comprobante solo si NO es pago en efectivo
                        byte[] pdfBytes = null;
                        String tipoComprobante = cita.getTipoComprobante();
                        boolean esEfectivo = "Efectivo".equalsIgnoreCase(cita.getMetodoPago());

                        if (!esEfectivo
                                && tipoComprobante != null && !tipoComprobante.isBlank()
                                && cita.getNumeroDocumento() != null
                                && cita.getNombreRazonSocial() != null) {
                            pdfBytes = pdfService.generarComprobante(
                                    tipoComprobante,
                                    cita.getNumeroDocumento(),
                                    cita.getNombreRazonSocial(),
                                    paciente.getNombre_completo(),
                                    nombreMedico,
                                    especialidad,
                                    citaDto.getFecha_cita() != null ? citaDto.getFecha_cita() : "Por confirmar",
                                    citaDto.getHora_cita() != null ? citaDto.getHora_cita() : "Por confirmar",
                                    cita.getMetodoPago()
                            );
                        }

                        // Si es efectivo, pasar tipoComprobante como "efectivo" para que el correo incluya la nota
                        String tipoParaCorreo = esEfectivo ? "efectivo" : tipoComprobante;

                        emailService.enviarConfirmacionCita(
                                usuario.getEmail(),
                                paciente.getNombre_completo(),
                                nombreMedico,
                                especialidad,
                                citaDto.getFecha_cita() != null ? citaDto.getFecha_cita() : "Por confirmar",
                                citaDto.getHora_cita() != null ? citaDto.getHora_cita() : "Por confirmar",
                                cita.getMotivo(),
                                pdfBytes,
                                tipoParaCorreo
                        );
                    }
                });
            });
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo de confirmación de cita: {}", e.getMessage());
        }

        return citaDto;
    }
    public CitaDto update(int id, CitaRequest cita) {
        Optional<Cita> citaUpdate = citaRepository.findById(id);
        if (citaUpdate.isPresent()) {
            citaUpdate.get().setMotivo(cita.getMotivo());
            citaUpdate.get().setEstado(cita.getEstado());
            citaUpdate.get().setAsistencia(cita.getAsistencia());
            citaUpdate.get().setComentarios(cita.getComentarios());
            citaUpdate.get().setId_horario(cita.getId_horario());
            citaUpdate.get().setId_medico(cita.getId_medico());
            citaUpdate.get().setId_paciente(cita.getId_paciente());
            citaUpdate.get().setFecha_cita(cita.getFecha_cita() != null ? LocalDate.parse(cita.getFecha_cita()) : null);
            citaUpdate.get().setHora_cita(cita.getHora_cita());
            citaUpdate.get().setId_pago(cita.getId_pago() > 0 ? cita.getId_pago() : null);
            return enriquecer(CitaHelper.mapCita(citaRepository.save(citaUpdate.get())));
        }
        return null;
    }

    public List<CitaDto> getByPaciente(int id_paciente) {
        return enriquecerBatch(citaRepository.findByIdPaciente(id_paciente));
    }

    public List<CitaDto> getByCliente(int idCliente) {
        return enriquecerBatch(citaRepository.findByCliente(idCliente));
    }

    public List<CitaDto> getByMedico(int idMedico) {
        return enriquecerBatch(citaRepository.findByIdMedico(idMedico));
    }

    public List<Integer> getCitaIdsByCliente(int idCliente) {
        return citaRepository.findCitaIdsByCliente(idCliente);
    }

    public CitaDto marcarAsistencia(int id, Character asistencia) {
        Optional<Cita> cita = citaRepository.findById(id);
        if (cita.isPresent()) {
            cita.get().setAsistencia(asistencia);
            return enriquecer(CitaHelper.mapCita(citaRepository.save(cita.get())));
        }
        return null;
    }
}
