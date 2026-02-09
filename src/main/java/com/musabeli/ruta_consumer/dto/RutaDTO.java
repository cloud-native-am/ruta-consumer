package com.musabeli.ruta_consumer.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutaDTO {
    private String patente;
    private String rutaInicio;
    private String rutaFin;
    private LocalDateTime horaLlegada;
    private LocalDateTime horaSalida;
    private LocalDateTime fechaActualizacion;
}
