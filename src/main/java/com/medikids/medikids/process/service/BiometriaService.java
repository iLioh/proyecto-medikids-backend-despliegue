package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.Biometria;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.repository.BiometriaRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BiometriaService {

    private static final double UMBRAL_DISTANCIA = 0.55;

    @Autowired
    private BiometriaRepository biometriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void enroll(int idUsuario, List<com.medikids.medikids.expose.model.request.BiometriaEnrollRequest.DescriptorEntry> descriptors) {
        biometriaRepository.deleteByUsuarioId(idUsuario);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        int muestraFrente = 1;
        int muestraIzquierda = 1;
        int muestraDerecha = 1;

        for (var entry : descriptors) {
            String jsonDescriptor = serializeDescriptor(entry.getDescriptor());
            int muestra = switch (entry.getTipo()) {
                case "FRENTE" -> muestraFrente++;
                case "IZQUIERDA" -> muestraIzquierda++;
                case "DERECHA" -> muestraDerecha++;
                default -> 1;
            };

            Biometria bio = Biometria.builder()
                    .usuario(usuario)
                    .face_descriptor(jsonDescriptor)
                    .tipo(entry.getTipo())
                    .muestra(muestra - 1)
                    .build();

            biometriaRepository.save(bio);
        }
    }

    public boolean verify(int idUsuario, List<Double> descriptorCapturado) {
        List<Biometria> biometrics = biometriaRepository.findByUsuarioIdAndActivoTrue(idUsuario);

        if (biometrics.isEmpty()) {
            return false;
        }

        double[] captured = descriptorCapturado.stream().mapToDouble(Double::doubleValue).toArray();

        for (Biometria bio : biometrics) {
            double[] stored = deserializeDescriptor(bio.getFace_descriptor());
            double distancia = euclideanDistance(captured, stored);
            if (distancia < UMBRAL_DISTANCIA) {
                return true;
            }
        }

        return false;
    }

    public boolean existsByUsuarioId(Integer idUsuario) {
        return biometriaRepository.existsByUsuarioIdAndActivoTrue(idUsuario);
    }

    @Transactional
    public void deleteByUsuarioId(Integer idUsuario) {
        biometriaRepository.deleteByUsuarioId(idUsuario);
    }

    public double euclideanDistance(double[] a, double[] b) {
        if (a.length != b.length) {
            return Double.MAX_VALUE;
        }
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    private String serializeDescriptor(List<Double> descriptor) {
        try {
            return objectMapper.writeValueAsString(descriptor);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar descriptor facial", e);
        }
    }

    private double[] deserializeDescriptor(String json) {
        try {
            List<Double> list = objectMapper.readValue(json, new TypeReference<List<Double>>() {});
            return list.stream().mapToDouble(Double::doubleValue).toArray();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al deserializar descriptor facial", e);
        }
    }
}
