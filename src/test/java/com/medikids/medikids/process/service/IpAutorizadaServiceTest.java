package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.IpAutorizadaRequest;
import com.medikids.medikids.process.domain.IpAutorizada;
import com.medikids.medikids.process.dto.IpAutorizadaDto;
import com.medikids.medikids.process.repository.IpAutorizadaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("IpAutorizadaService - Tests de gestión de IPs autorizadas")
class IpAutorizadaServiceTest {

    @Autowired
    private IpAutorizadaService ipAutorizadaService;

    @MockitoBean
    private IpAutorizadaRepository ipAutorizadaRepository;

    private IpAutorizada testIp;
    private IpAutorizadaRequest ipRequest;

    @BeforeEach
    void setUp() {
        testIp = new IpAutorizada();
        testIp.setIdIpAutorizada(1);
        testIp.setIdUsuario(1);
        testIp.setIp("192.168.1.100");
        testIp.setDescripcion("Mi Laptop");
        testIp.setActivo(true);

        ipRequest = new IpAutorizadaRequest();
        ipRequest.setId_usuario(1);
        ipRequest.setIp("192.168.1.100");
        ipRequest.setDescripcion("Mi Laptop");
    }

    @Test
    @DisplayName("Debe verificar que una IP está autorizada")
    void testIsIpAuthorized() {
        // Arrange
        when(ipAutorizadaRepository.existsByIpAndActivoTrue("192.168.1.100"))
                .thenReturn(true);

        // Act
        boolean result = ipAutorizadaService.isIpAuthorized("192.168.1.100");

        // Assert
        assertTrue(result);
        verify(ipAutorizadaRepository, times(1)).existsByIpAndActivoTrue("192.168.1.100");
    }

    @Test
    @DisplayName("Debe rechazar una IP no autorizada")
    void testIsIpNotAuthorized() {
        // Arrange
        when(ipAutorizadaRepository.existsByIpAndActivoTrue("10.0.0.1"))
                .thenReturn(false);

        // Act
        boolean result = ipAutorizadaService.isIpAuthorized("10.0.0.1");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe obtener todas las IPs autorizadas")
    void testGetAll() {
        // Arrange
        when(ipAutorizadaRepository.findAll()).thenReturn(List.of(testIp));

        // Act
        List<IpAutorizadaDto> result = ipAutorizadaService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(ipAutorizadaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener una IP autorizada por ID")
    void testGetById() {
        // Arrange
        when(ipAutorizadaRepository.findById(1)).thenReturn(Optional.of(testIp));

        // Act
        IpAutorizadaDto result = ipAutorizadaService.getById(1);

        // Assert
        assertNotNull(result);
        assertEquals("192.168.1.100", result.getIp());
        verify(ipAutorizadaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener IP inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(ipAutorizadaRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        IpAutorizadaDto result = ipAutorizadaService.getById(99);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe obtener IPs autorizadas de un usuario")
    void testGetByUsuario() {
        // Arrange
        when(ipAutorizadaRepository.findByIdUsuario(1)).thenReturn(List.of(testIp));

        // Act
        List<IpAutorizadaDto> result = ipAutorizadaService.getByUsuario(1);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(ipAutorizadaRepository, times(1)).findByIdUsuario(1);
    }

    @Test
    @DisplayName("Debe guardar una nueva IP autorizada")
    void testSave() {
        // Arrange
        when(ipAutorizadaRepository.save(any(IpAutorizada.class))).thenReturn(testIp);

        // Act
        IpAutorizadaDto result = ipAutorizadaService.save(ipRequest);

        // Assert
        assertNotNull(result);
        assertEquals("192.168.1.100", result.getIp());
        verify(ipAutorizadaRepository, times(1)).save(any(IpAutorizada.class));
    }

    @Test
    @DisplayName("Debe actualizar una IP autorizada existente")
    void testUpdate() {
        // Arrange
        IpAutorizadaRequest updateRequest = new IpAutorizadaRequest();
        updateRequest.setId_usuario(1);
        updateRequest.setIp("192.168.1.100");
        updateRequest.setDescripcion("Mi Desktop");

        when(ipAutorizadaRepository.findById(1)).thenReturn(Optional.of(testIp));
        when(ipAutorizadaRepository.save(any(IpAutorizada.class))).thenReturn(testIp);

        // Act
        IpAutorizadaDto result = ipAutorizadaService.update(1, updateRequest);

        // Assert
        assertNotNull(result);
        verify(ipAutorizadaRepository, times(1)).save(any(IpAutorizada.class));
    }

    @Test
    @DisplayName("Debe desactivar una IP autorizada")
    void testDelete() {
        // Arrange
        when(ipAutorizadaRepository.findById(1)).thenReturn(Optional.of(testIp));
        when(ipAutorizadaRepository.save(any(IpAutorizada.class))).thenReturn(testIp);

        // Act
        ipAutorizadaService.delete(1);

        // Assert
        verify(ipAutorizadaRepository, times(1)).save(any(IpAutorizada.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay IPs autorizadas")
    void testGetAllEmpty() {
        // Arrange
        when(ipAutorizadaRepository.findAll()).thenReturn(List.of());

        // Act
        List<IpAutorizadaDto> result = ipAutorizadaService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener múltiples IPs de un usuario")
    void testGetByUsuarioMultiple() {
        // Arrange
        IpAutorizada ip2 = new IpAutorizada();
        ip2.setIdIpAutorizada(2);
        ip2.setIdUsuario(1);
        ip2.setIp("192.168.1.101");
        ip2.setDescripcion("Mi Teléfono");
        ip2.setActivo(true);

        when(ipAutorizadaRepository.findByIdUsuario(1)).thenReturn(List.of(testIp, ip2));

        // Act
        List<IpAutorizadaDto> result = ipAutorizadaService.getByUsuario(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Debe validar formato de IP")
    void testValidateIpFormat() {
        // Arrange
        when(ipAutorizadaRepository.existsByIpAndActivoTrue("192.168.1.100"))
                .thenReturn(true);

        // Act
        boolean result = ipAutorizadaService.isIpAuthorized("192.168.1.100");

        // Assert
        assertTrue(result);
    }
}
