package com.musabeli.ruta_consumer.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.musabeli.ruta_consumer.dto.RutaDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogRutaService {

    private final ArchivoRutaService archivoRutaService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumeRuta(RutaDTO ruta){
        log.info("========================================");
        log.info("MENSAJE RECIBIDO DE RABBITMQ");
        log.info("Patente: {}", ruta.getPatente());
        log.info("Ruta: ({}, {})", ruta.getRutaInicio(), ruta.getRutaFin());
        log.info("Hora salida-llegada: ({}, {})", ruta.getHoraSalida(), ruta.getHoraLlegada());
        log.info("========================================");

        archivoRutaService.guardarRuta(ruta);
    }

}
