package com.medikids.medikids.expose.web;

import com.medikids.medikids.expose.model.request.PagoRequest;
import com.medikids.medikids.process.dto.ClienteDto;
import com.medikids.medikids.process.dto.PagoDto;
import com.medikids.medikids.process.service.ClienteService;
import com.medikids.medikids.process.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pago")
@CrossOrigin("*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @PreAuthorize("@permiso.has('pago:read')")
    public List<PagoDto> listarPagos(Authentication auth) {
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 3 || role == 4) {
            return pagoService.listarPagos();
        }
        int userId = (int) details.get("id");
        ClienteDto cliente = clienteService.getByIdUsuario(userId);
        if (cliente == null) return List.of();
        return pagoService.listarPagosPorCliente(cliente.getId_cliente());
    }

    @PostMapping("/save")
    @PreAuthorize("@permiso.has('pago:write')")
    public PagoDto guardarPago(@RequestBody PagoRequest pago, Authentication auth) {
        // Validar rol del usuario (Debe ser cliente)
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        int role = (int) details.get("id_rol");
        if (role == 1) {
            int userId = (int) details.get("id");
            ClienteDto cliente = clienteService.getByIdUsuario(userId);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }
        }
        return pagoService.guardarPago(pago);
    }
}