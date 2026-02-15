# Ruta Consumer

Microservicio consumidor que escucha informacion de rutas desde una cola de RabbitMQ y las persiste en un archivo JSON local.

## Tecnologias

- Java 21
- Spring Boot 4.0.2
- Spring AMQP (RabbitMQ)
- Jackson Databind (con soporte JSR310 para fechas)
- Lombok
- Maven
- Docker

## Arquitectura

Este servicio forma parte de un patron productor-consumidor:

```
[Ruta Producer] --> RabbitMQ --> [Ruta Consumer] --> data/rutas.json
```

## Almacenamiento

Las rutas se persisten en un archivo JSON ubicado en `data/rutas.json`. El archivo contiene un arreglo JSON con el historial de todas las rutas recibidas.

### Formato del archivo

```json
[
  {
    "patente": "BCDF56",
    "rutaInicio": "Santiago",
    "rutaFin": "Valparaiso",
    "horaLlegada": "2026-02-09T14:30:00",
    "horaSalida": "2026-02-09T18:15:00",
    "fechaActualizacion": "2026-02-09T14:54:05.388097936"
  }
]
```

> El metodo de escritura esta sincronizado (`synchronized`) para garantizar seguridad en entornos multihilo.

## Configuracion RabbitMQ

| Propiedad | Valor |
|-----------|-------|
| Queue | `gps.ruta.queue` |
| Exchange | `ruta.exchange` (Topic) |
| Routing Key | `gps.ruta` |
| Host | `rabbitmq` |
| Puerto | `5672` |

El consumidor incluye un `DefaultClassMapper` que mapea la clase del productor (`com.musabeli.ruta_producer.dto.RutaDTO`) a la clase local para la deserializacion correcta de los mensajes.

## Estructura del Proyecto

```
src/main/java/com/musabeli/ruta_consumer/
├── RutaConsumerApplication.java      # Clase principal
├── config/
│   └── RabbitMQConfig.java           # Configuracion de colas y deserializacion
├── dto/
│   └── RutaDTO.java                  # Objeto de transferencia de datos
└── services/
    ├── LogRutaService.java           # Listener RabbitMQ
    └── ArchivoRutaService.java       # Logica de persistencia en archivo JSON
```

## Ejecucion Local

### Prerrequisitos

- Java 21
- Maven 3.9+
- RabbitMQ ejecutandose en `localhost:5672`

### Compilar y ejecutar

```bash
mvn clean package
java -jar target/ruta-consumer-0.0.1-SNAPSHOT.jar
```

El servicio estara disponible en el puerto `8082`.

## Ejecucion con Docker

### Construir imagen

```bash
docker build -t ruta-consumer .
```

### Ejecutar con Docker Compose

```bash
docker compose up -d
```

> Requiere que la red `app-network` exista previamente:
> ```bash
> docker network create app-network
> ```

> El archivo `data/rutas.json` se persiste mediante un volumen Docker (`ruta-data`) montado en `/app/data`, asegurando que los datos sobrevivan reinicios del contenedor.

## Flujo de Procesamiento

1. El servicio se conecta a la cola `gps.ruta.queue` de RabbitMQ
2. Al recibir un mensaje, deserializa el JSON a `RutaDTO`
3. `LogRutaService` delega a `ArchivoRutaService` para persistir la ruta
4. `ArchivoRutaService` lee el archivo JSON existente, agrega la nueva ruta y reescribe el archivo
5. Registra la operacion en los logs del sistema
