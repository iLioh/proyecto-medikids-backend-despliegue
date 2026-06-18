package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.TarjetaGuardadaRequest;
import com.medikids.medikids.process.domain.TarjetaGuardada;
import com.medikids.medikids.process.dto.TarjetaGuardadaDto;
import com.medikids.medikids.process.repository.TarjetaGuardadaRepository;
import com.medikids.medikids.utils.helpers.TarjetaGuardadaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TarjetaGuardadaService {

    @Autowired
    private TarjetaGuardadaRepository tarjetaRepository;

    public List<TarjetaGuardadaDto> listarPorUsuario(int idUsuario) {
        return TarjetaGuardadaHelper.mapAll(
                tarjetaRepository.findByIdUsuarioAndActivoTrue(idUsuario)
        );
    }

    public TarjetaGuardadaDto guardar(TarjetaGuardadaRequest request, int idUsuario) {
        TarjetaGuardada tarjeta = TarjetaGuardadaHelper.buildTarjeta(request, idUsuario);
        return TarjetaGuardadaHelper.mapTarjeta(tarjetaRepository.save(tarjeta));
    }

    public void eliminar(int idTarjeta, int idUsuario) {
        Optional<TarjetaGuardada> opt = tarjetaRepository.findById(idTarjeta);
        if (opt.isEmpty() || opt.get().getId_usuario() != idUsuario) {
            throw new RuntimeException("Tarjeta no encontrada o no autorizada");
        }
        TarjetaGuardada tarjeta = opt.get();
        tarjeta.setActivo(false);
        tarjetaRepository.save(tarjeta);
    }

    public TarjetaGuardadaDto setPredeterminada(int idTarjeta, int idUsuario) {
        Optional<TarjetaGuardada> opt = tarjetaRepository.findById(idTarjeta);
        if (opt.isEmpty() || opt.get().getId_usuario() != idUsuario) {
            throw new RuntimeException("Tarjeta no encontrada o no autorizada");
        }
        tarjetaRepository.clearPredeterminada(idUsuario);
        TarjetaGuardada tarjeta = opt.get();
        tarjeta.setEs_predeterminada(true);
        return TarjetaGuardadaHelper.mapTarjeta(tarjetaRepository.save(tarjeta));
    }
}
