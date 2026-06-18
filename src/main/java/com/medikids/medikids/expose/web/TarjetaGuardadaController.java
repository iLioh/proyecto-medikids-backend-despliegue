package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.TarjetaGuardadaRequest;
import com.medikids.medikids.process.dto.TarjetaGuardadaDto;
import com.medikids.medikids.process.service.TarjetaGuardadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tarjeta")
@CrossOrigin("*")
public class TarjetaGuardadaController {

    @Autowired
    private TarjetaGuardadaService tarjetaService;

    @GetMapping
    @PreAuthorize("@permiso.has('tarjeta:read')")
    public List<TarjetaGuardadaDto> listar(Authentication auth) {
        return tarjetaService.listarPorUsuario(getUserId(auth));
    }

    @PostMapping("/save")
    @PreAuthorize("@permiso.has('tarjeta:write')")
    public TarjetaGuardadaDto guardar(@RequestBody TarjetaGuardadaRequest request, Authentication auth) {
        return tarjetaService.guardar(request, getUserId(auth));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@permiso.has('tarjeta:delete')")
    public void eliminar(@PathVariable int id, Authentication auth) {
        tarjetaService.eliminar(id, getUserId(auth));
    }

    @PutMapping("/{id}/predeterminada")
    @PreAuthorize("@permiso.has('tarjeta:write')")
    public TarjetaGuardadaDto setPredeterminada(@PathVariable int id, Authentication auth) {
        return tarjetaService.setPredeterminada(id, getUserId(auth));
    }

    @SuppressWarnings("unchecked")
    private int getUserId(Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        return (int) details.get("id");
    }
}
