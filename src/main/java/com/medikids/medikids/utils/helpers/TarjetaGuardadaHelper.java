package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.TarjetaGuardadaRequest;
import com.medikids.medikids.process.domain.TarjetaGuardada;
import com.medikids.medikids.process.dto.TarjetaGuardadaDto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class TarjetaGuardadaHelper implements Serializable {

    private TarjetaGuardadaHelper() {
        throw new IllegalStateException("TarjetaGuardadaHelper class");
    }

    public static TarjetaGuardadaDto mapTarjeta(TarjetaGuardada tarjeta) {
        return TarjetaGuardadaDto.builder()
                .id_tarjeta(tarjeta.getId_tarjeta())
                .id_usuario(tarjeta.getId_usuario())
                .alias(tarjeta.getAlias())
                .ultimos_digitos(tarjeta.getUltimos_digitos())
                .marca(tarjeta.getMarca())
                .nombre_titular(tarjeta.getNombre_titular())
                .mes_vencimiento(tarjeta.getMes_vencimiento())
                .anio_vencimiento(tarjeta.getAnio_vencimiento())
                .es_predeterminada(tarjeta.isEs_predeterminada())
                .fecha_creacion(tarjeta.getFecha_creacion())
                .build();
    }

    public static TarjetaGuardada buildTarjeta(TarjetaGuardadaRequest request, int idUsuario) {
        return TarjetaGuardada.builder()
                .id_usuario(idUsuario)
                .alias(request.getAlias())
                .ultimos_digitos(request.getUltimos_digitos())
                .marca(request.getMarca())
                .nombre_titular(request.getNombre_titular())
                .mes_vencimiento(request.getMes_vencimiento())
                .anio_vencimiento(request.getAnio_vencimiento())
                .build();
    }

    public static List<TarjetaGuardadaDto> mapAll(List<TarjetaGuardada> tarjetas) {
        return tarjetas.stream()
                .map(TarjetaGuardadaHelper::mapTarjeta)
                .collect(Collectors.toList());
    }
}
