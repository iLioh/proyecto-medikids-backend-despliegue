package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.EspecialidadRequest;
import com.medikids.medikids.process.dto.EspecialidadDto;
import com.medikids.medikids.process.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especialidad")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    @GetMapping("/all")
    @PreAuthorize("@permiso.has('especialidad:read')")
    public List<EspecialidadDto> all() {
        return especialidadService.getAll();
    }

    @GetMapping("/getBy/{id}")
    @PreAuthorize("@permiso.has('especialidad:read')")
    public EspecialidadDto getById(@PathVariable int id) {
        return especialidadService.getById(id);
    }
}
