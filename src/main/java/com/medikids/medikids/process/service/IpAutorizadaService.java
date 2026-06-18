package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.IpAutorizadaRequest;
import com.medikids.medikids.process.domain.IpAutorizada;
import com.medikids.medikids.process.dto.IpAutorizadaDto;
import com.medikids.medikids.process.repository.IpAutorizadaRepository;
import com.medikids.medikids.utils.helpers.IpAutorizadaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IpAutorizadaService {

    @Autowired
    private IpAutorizadaRepository ipAutorizadaRepository;

    public boolean isIpAuthorized(String ip) {
        return ipAutorizadaRepository.existsByIpAndActivoTrue(ip);
    }

    @Transactional(readOnly = true)
    public List<IpAutorizadaDto> getAll() {
        return IpAutorizadaHelper.mapAll(ipAutorizadaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public IpAutorizadaDto getById(int id) {
        return ipAutorizadaRepository.findById(id)
                .map(IpAutorizadaHelper::mapIpAutorizada)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<IpAutorizadaDto> getByUsuario(int idUsuario) {
        return IpAutorizadaHelper.mapAll(
                ipAutorizadaRepository.findByIdUsuario(idUsuario)
        );
    }

    @Transactional
    public IpAutorizadaDto save(IpAutorizadaRequest request) {
        IpAutorizada entity = IpAutorizadaHelper.buildIpAutorizada(request);
        return IpAutorizadaHelper.mapIpAutorizada(ipAutorizadaRepository.save(entity));
    }

    @Transactional
    public IpAutorizadaDto update(int id, IpAutorizadaRequest request) {
        Optional<IpAutorizada> opt = ipAutorizadaRepository.findById(id);
        if (opt.isEmpty()) return null;

        IpAutorizada entity = opt.get();
        entity.setIp(request.getIp());
        entity.setDescripcion(request.getDescripcion());
        if (request.getActivo() != null) {
            entity.setActivo(request.getActivo());
        }

        return IpAutorizadaHelper.mapIpAutorizada(ipAutorizadaRepository.save(entity));
    }

    @Transactional
    public boolean delete(int id) {
        Optional<IpAutorizada> opt = ipAutorizadaRepository.findById(id);
        if (opt.isEmpty()) return false;

        IpAutorizada entity = opt.get();
        entity.setVisible("0");
        entity.setActivo(false);
        ipAutorizadaRepository.save(entity);
        return true;
    }
}
