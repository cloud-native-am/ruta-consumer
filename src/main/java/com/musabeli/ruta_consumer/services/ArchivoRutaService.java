package com.musabeli.ruta_consumer.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.musabeli.ruta_consumer.dto.RutaDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ArchivoRutaService {

    private final ObjectMapper objectMapper;
    private final File archivo;

    public ArchivoRutaService(@Value("${ruta.archivo.json}") String rutaArchivo) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
        this.archivo = new File(rutaArchivo);
    }

    public synchronized void guardarRuta(RutaDTO ruta) {
        try {
            Files.createDirectories(Path.of(archivo.getParent()));

            List<RutaDTO> rutas;
            if (archivo.exists() && archivo.length() > 0) {
                rutas = objectMapper.readValue(archivo, new TypeReference<List<RutaDTO>>() {});
            } else {
                rutas = new ArrayList<>();
            }

            rutas.add(ruta);
            objectMapper.writeValue(archivo, rutas);

            log.info("Ruta guardada en archivo JSON. Total registros: {}", rutas.size());
        } catch (IOException e) {
            log.error("Error al guardar ruta en archivo JSON: {}", e.getMessage());
        }
    }
}
