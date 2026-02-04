# Travel Microservices – Sistema de Reserva de Vuelos

Proyecto backend de aprendizaje basado en una arquitectura de microservicios,
orientado a un sistema sencillo de reserva de vuelos.

El foco del proyecto está en la parte backend y en la comprensión de cómo
estructurar y comunicar distintos microservicios utilizando Spring Boot
y Spring Cloud.

## Descripción general
El sistema simula un escenario de reserva de vuelos donde un microservicio
de reservas consume información de un microservicio de búsqueda de vuelos.
No cuenta con de momento con frontend, ya que el objetivo principal es trabajar la arquitectura,
la comunicación entre servicios y los aspectos transversales del backend.

## Arquitectura
El proyecto está compuesto por varios microservicios independientes:

- **Flight Search Service**: expone información relacionada con vuelos
- **Booking Service**: gestiona las reservas y consume el servicio de vuelos
- **API Gateway**: punto de entrada único para las peticiones
- **Service Discovery**: descubrimiento de servicios mediante Eureka

La comunicación entre servicios se realiza a través de **APIs REST**.

## Seguridad
Los servicios están protegidos con **Spring Security** actuando como
**OAuth2 Resource Server**, validando **JWT** emitidos por un proveedor
de identidad (Keycloak).

## Contenerización
El proyecto incluye un primer acercamiento a la contenerización con **Docker**,
con el objetivo de entender cómo levantar y coordinar los distintos servicios
en un entorno distribuido.

## Tecnologías utilizadas
- Java
- Spring Boot
- Spring Cloud (Eureka, Gateway)
- APIs REST
- Spring Security (OAuth2, JWT)
- Docker (nivel introductorio)
