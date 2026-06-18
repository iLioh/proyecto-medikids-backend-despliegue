package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.IpAutorizadaRequest;
import com.medikids.medikids.process.domain.IpAutorizada;
import com.medikids.medikids.process.dto.IpAutorizadaDto;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class IpAutorizadaHelper implements Serializable {
    private IpAutorizadaHelper() {
        throw new IllegalStateException("IpAutorizadaHelper class");
    }

    public static IpAutorizadaDto mapIpAutorizada(IpAutorizada ipAutorizada) {
        return IpAutorizadaDto.builder()
                .id_ip_autorizada(ipAutorizada.getIdIpAutorizada())
                .id_usuario(ipAutorizada.getIdUsuario())
                .ip(ipAutorizada.getIp())
                .descripcion(ipAutorizada.getDescripcion())
                .activo(ipAutorizada.getActivo() != null && ipAutorizada.getActivo())
                .fecha_registro(toDate(ipAutorizada.getFechaRegistro()))
                .fecha_modificado(toDate(ipAutorizada.getFechaModificado()))
                .build();
    }

    private static Date toDate(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static IpAutorizada buildIpAutorizada(IpAutorizadaRequest request) {
        return IpAutorizada.builder()
                .ip(request.getIp())
                .descripcion(request.getDescripcion())
                .activo(request.getActivo() == null || request.getActivo())
                .idUsuario(request.getId_usuario())
                .build();
    }

    public static List<IpAutorizadaDto> mapAll(List<IpAutorizada> ipAutorizadas) {
        return ipAutorizadas.stream()
                .map(IpAutorizadaHelper::mapIpAutorizada)
                .collect(Collectors.toList());
    }
}