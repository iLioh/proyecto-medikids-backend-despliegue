package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.PagoRequest;
import com.medikids.medikids.process.dto.PagoDto;
import com.medikids.medikids.process.repository.PagoRepository;
import com.medikids.medikids.utils.helpers.PagoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public List<PagoDto> listarPagos() {
        return PagoHelper.mapAll(pagoRepository.findAll());
    }

    public PagoDto guardarPago(PagoRequest pago) {
        return PagoHelper.mapPago(pagoRepository.save(PagoHelper.buildPago(pago)));
    }

    public List<PagoDto> listarPagosPorCliente(int idCliente) {
        return PagoHelper.mapAll(pagoRepository.findByCliente(idCliente));
    }
}