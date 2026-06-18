package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.ClienteRequest;
import com.medikids.medikids.process.domain.Cliente;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.dto.ClienteDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteHelper implements Serializable {
    private ClienteHelper() {
        throw new IllegalStateException("ClienteHelper class");
    }

    // Convierte un cliente "domain" a "dto"
    public static ClienteDto mapCliente(Cliente cliente) {
        return ClienteDto.builder()
                .id_cliente(cliente.getId_cliente())
                .id_usuario(cliente.getUsuario() != null ? cliente.getUsuario().getId_usuario() : 0)
                .dni_responsable(cliente.getDni_responsable())
                .direccion(cliente.getDireccion())
                .build();
    }

    // Convierte un cliente "request" a "domain"
    public static Cliente buildCliente(ClienteRequest cliente) {
        return Cliente.builder()
                .usuario(Usuario.builder().id_usuario(cliente.getId_usuario()).build())
                .dni_responsable(cliente.getDni_responsable())
                .direccion(cliente.getDireccion())
                .build();
    }

    // Convierte una lista de clientes "domain" a "dto"
    public static List<ClienteDto> mapAll(List<Cliente> clientes) {
        return clientes.stream()
                .map(ClienteHelper::mapCliente)
                .collect(Collectors.toList());
    }

    // Convierte un page de clientes "domain" a "dto"
    public static Page<ClienteDto> mapPage(Page<Cliente> clientePage) {
        List<ClienteDto> clientes = clientePage.getContent().stream()
                .map(ClienteHelper::mapCliente)
                .collect(Collectors.toList());
        return new PageImpl<>(clientes);
    }
}
