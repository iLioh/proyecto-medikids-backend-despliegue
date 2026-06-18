package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.Permiso;
import com.medikids.medikids.process.domain.Rol;
import com.medikids.medikids.process.domain.RolPermiso;
import com.medikids.medikids.process.repository.PermisoRepository;
import com.medikids.medikids.process.repository.RolPermisoRepository;
import com.medikids.medikids.process.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(1)
public class PermisoSeeder implements CommandLineRunner {

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolPermisoRepository rolPermisoRepository;

    @Autowired
    private RolRepository rolRepository;

    private static final int ROL_CLIENTE = 1;
    private static final int ROL_MEDICO = 2;
    private static final int ROL_ADMIN = 3;
    private static final int ROL_ADMIN_OPERATIVO = 4;

    @Override
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            rolRepository.saveAll(List.of(
                    Rol.builder().nombre_rol("Cliente").build(),
                    Rol.builder().nombre_rol("Médico").build(),
                    Rol.builder().nombre_rol("Admin").build(),
                    Rol.builder().nombre_rol("Admin Operativo").build()
            ));
        } else if (rolRepository.findById(ROL_ADMIN_OPERATIVO).isEmpty()) {
            rolRepository.save(Rol.builder().nombre_rol("Admin Operativo").build());
        }

        if (permisoRepository.count() > 0) {
            if (rolPermisoRepository.findCodigosPermisoByIdRol(ROL_ADMIN_OPERATIVO).isEmpty()) {
                Map<String, Permiso> byCodigo = permisoRepository.findAll().stream()
                        .collect(Collectors.toMap(Permiso::getCodigo, p -> p));
                insertarPermisosRol(ROL_ADMIN_OPERATIVO, byCodigo,
                        "paciente:read", "paciente:write",
                        "cita:read", "cita:write", "cita:asistencia",
                        "medico:read", "medico:write", "medico:delete", "medico:toggle-status",
                        "especialidad:read",
                        "horario:read", "horario:write",
                        "pago:read", "pago:write",
                        "incidente:read", "incidente:write", "incidente:respond",
                        "usuario:read", "usuario:write",
                        "cliente:read", "cliente:write",
                        "ip:read", "ip:write",
                        "auditoria:read"
                );
            }

            // Agregar permisos de tarjeta si aún no existen
            Map<String, Permiso> existentes = permisoRepository.findAll().stream()
                    .collect(Collectors.toMap(Permiso::getCodigo, p -> p));
            if (!existentes.containsKey("tarjeta:read")) {
                List<Permiso> nuevos = permisoRepository.saveAll(List.of(
                        Permiso.builder().codigo("tarjeta:read").nombre("Ver tarjetas guardadas").descripcion("Ver tarjetas de pago guardadas").recurso("tarjeta").accion("read").build(),
                        Permiso.builder().codigo("tarjeta:write").nombre("Guardar tarjetas").descripcion("Guardar y modificar tarjetas de pago").recurso("tarjeta").accion("write").build(),
                        Permiso.builder().codigo("tarjeta:delete").nombre("Eliminar tarjetas").descripcion("Eliminar tarjetas guardadas").recurso("tarjeta").accion("delete").build()
                ));
                nuevos.forEach(p -> existentes.put(p.getCodigo(), p));
                insertarPermisosRol(ROL_CLIENTE, existentes, "tarjeta:read", "tarjeta:write", "tarjeta:delete");
                insertarPermisosRol(ROL_ADMIN, existentes, "tarjeta:read", "tarjeta:write", "tarjeta:delete");
                insertarPermisosRol(ROL_ADMIN_OPERATIVO, existentes, "tarjeta:read", "tarjeta:write", "tarjeta:delete");
            }

            return;
        }

        List<Permiso> permisos = permisoRepository.saveAll(List.of(
                // ── Paciente ──
                Permiso.builder().codigo("paciente:read").nombre("Leer pacientes").descripcion("Ver datos de pacientes").recurso("paciente").accion("read").build(),
                Permiso.builder().codigo("paciente:write").nombre("Crear/editar pacientes").descripcion("Registrar y modificar pacientes").recurso("paciente").accion("write").build(),
                // ── Cita ──
                Permiso.builder().codigo("cita:read").nombre("Leer citas").descripcion("Ver citas agendadas").recurso("cita").accion("read").build(),
                Permiso.builder().codigo("cita:write").nombre("Crear/editar citas").descripcion("Agendar y modificar citas").recurso("cita").accion("write").build(),
                Permiso.builder().codigo("cita:asistencia").nombre("Registrar asistencia").descripcion("Marcar asistencia a citas").recurso("cita").accion("asistencia").build(),
                // ── Médico ──
                Permiso.builder().codigo("medico:read").nombre("Leer médicos").descripcion("Ver perfiles de médicos").recurso("medico").accion("read").build(),
                Permiso.builder().codigo("medico:write").nombre("Crear/editar médicos").descripcion("Registrar y modificar médicos").recurso("medico").accion("write").build(),
                Permiso.builder().codigo("medico:delete").nombre("Eliminar médicos").descripcion("Eliminar perfiles de médicos").recurso("medico").accion("delete").build(),
                Permiso.builder().codigo("medico:toggle-status").nombre("Activar/desactivar médico").descripcion("Cambiar estado activo/inactivo").recurso("medico").accion("toggle-status").build(),
                // ── Especialidad ──
                Permiso.builder().codigo("especialidad:read").nombre("Leer especialidades").descripcion("Ver catálogo de especialidades").recurso("especialidad").accion("read").build(),
                // ── Horario ──
                Permiso.builder().codigo("horario:read").nombre("Leer horarios").descripcion("Ver horarios disponibles").recurso("horario").accion("read").build(),
                Permiso.builder().codigo("horario:write").nombre("Crear/editar horarios").descripcion("Gestionar horarios de atención").recurso("horario").accion("write").build(),
                // ── Pago ──
                Permiso.builder().codigo("pago:read").nombre("Leer pagos").descripcion("Ver historial de pagos").recurso("pago").accion("read").build(),
                Permiso.builder().codigo("pago:write").nombre("Crear/editar pagos").descripcion("Registrar pagos y reembolsos").recurso("pago").accion("write").build(),
                // ── Incidente ──
                Permiso.builder().codigo("incidente:read").nombre("Leer incidentes").descripcion("Ver reportes de incidentes").recurso("incidente").accion("read").build(),
                Permiso.builder().codigo("incidente:write").nombre("Crear incidentes").descripcion("Reportar incidentes").recurso("incidente").accion("write").build(),
                Permiso.builder().codigo("incidente:respond").nombre("Responder incidentes").descripcion("Responder a incidentes reportados").recurso("incidente").accion("respond").build(),
                // ── Usuario ──
                Permiso.builder().codigo("usuario:read").nombre("Leer usuarios").descripcion("Ver cuentas de usuario").recurso("usuario").accion("read").build(),
                Permiso.builder().codigo("usuario:write").nombre("Crear/editar usuarios").descripcion("Gestionar cuentas de usuario").recurso("usuario").accion("write").build(),
                // ── Cliente ──
                Permiso.builder().codigo("cliente:read").nombre("Leer clientes").descripcion("Ver datos de clientes/responsables").recurso("cliente").accion("read").build(),
                Permiso.builder().codigo("cliente:write").nombre("Crear/editar clientes").descripcion("Gestionar clientes").recurso("cliente").accion("write").build(),
                // ── Admin ──
                Permiso.builder().codigo("ip:read").nombre("Leer IPs autorizadas").descripcion("Ver lista blanca de IPs").recurso("ip").accion("read").build(),
                Permiso.builder().codigo("ip:write").nombre("Gestionar IPs autorizadas").descripcion("Crear/editar/eliminar IPs").recurso("ip").accion("write").build(),
                Permiso.builder().codigo("auditoria:read").nombre("Ver auditoría").descripcion("Consultar registros de auditoría").recurso("auditoria").accion("read").build(),
                Permiso.builder().codigo("rol:assign").nombre("Asignar roles").descripcion("Asignar roles a usuarios").recurso("rol").accion("assign").build(),
                // ── Tarjeta ──
                Permiso.builder().codigo("tarjeta:read").nombre("Ver tarjetas guardadas").descripcion("Ver tarjetas de pago guardadas").recurso("tarjeta").accion("read").build(),
                Permiso.builder().codigo("tarjeta:write").nombre("Guardar tarjetas").descripcion("Guardar y modificar tarjetas de pago").recurso("tarjeta").accion("write").build(),
                Permiso.builder().codigo("tarjeta:delete").nombre("Eliminar tarjetas").descripcion("Eliminar tarjetas guardadas").recurso("tarjeta").accion("delete").build()
        ));

        Map<String, Permiso> byCodigo = permisos.stream()
                .collect(Collectors.toMap(Permiso::getCodigo, p -> p));

        insertarPermisosRol(ROL_CLIENTE, byCodigo,
                "paciente:read", "paciente:write",
                "cita:read", "cita:write",
                "pago:read", "pago:write",
                "cliente:read", "cliente:write",
                "medico:read",
                "horario:read",
                "especialidad:read",
                "usuario:read",
                "tarjeta:read", "tarjeta:write", "tarjeta:delete"
        );

        insertarPermisosRol(ROL_MEDICO, byCodigo,
                "paciente:read",
                "cita:read", "cita:asistencia",
                "medico:read",
                "horario:read", "horario:write",
                "especialidad:read",
                "incidente:read", "incidente:write",
                "usuario:read"
        );

        insertarPermisosRol(ROL_ADMIN, byCodigo,
                permisos.stream().map(Permiso::getCodigo).toArray(String[]::new)
        );

        insertarPermisosRol(ROL_ADMIN_OPERATIVO, byCodigo,
                "paciente:read", "paciente:write",
                "cita:read", "cita:write", "cita:asistencia",
                "medico:read", "medico:write", "medico:delete", "medico:toggle-status",
                "especialidad:read",
                "horario:read", "horario:write",
                "pago:read", "pago:write",
                "incidente:read", "incidente:write", "incidente:respond",
                "usuario:read", "usuario:write",
                "cliente:read", "cliente:write",
                "ip:read", "ip:write",
                "auditoria:read"
        );
    }

    private void insertarPermisosRol(int idRol, Map<String, Permiso> byCodigo, String... codigos) {
        for (String codigo : codigos) {
            Permiso permiso = byCodigo.get(codigo);
            if (permiso != null) {
                rolPermisoRepository.save(RolPermiso.builder()
                        .idRol(idRol)
                        .idPermiso(permiso.getIdPermiso())
                        .build());
            }
        }
    }
}
