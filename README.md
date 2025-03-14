# BLACKJACK API Backend

API para administrar un sistema de Blackjack, permitiendo a los jugadores registrarse, jugar y realizar diferentes acciones en el juego. Desarrollado con **Spring Boot**.

---

## Tabla de Contenidos
 
1. [Estructura del Proyecto](#estructura-del-proyecto)
2. [Requisitos Previos](#requisitos-previos)
3. [Dependencias Principales](#dependencias-principales)
4. [Instalación](#instalación).
5. [Configuración](#configuración).
6. [Ejecución](#ejecución)
   - [Con Maven](#usando-maven)
   - [Con Docker](#usando-docker)
7. [Documentación de la API](#documentación-de-la-api)
8. [Endpoints Principales](#endpoints-principales)

---

## Estructura del Proyecto

```plaintext
S5T1/
  ├── src/
  │   ├── main/
  │   │   ├── java/s05/t01/
  │   │   │   ├── exception/        // Manejo de excepciones personalizadas
  │   │   │   ├── handler/          // Controladores para las peticiones
  │   │   │   ├── model/            // Entidades principales
  │   │   │   ├── repository/       // Persistencia de datos con JPA
  │   │   │   ├── router/           // Definición de rutas
  │   │   │   ├── service/          // Lógica de negocio
  │   │   │   ├── swagger/          // Configuración de Swagger
  │   │   ├── resources/
  │   │   │   └── application.properties
  │   ├── test/                     // Pruebas unitarias
  ├── Dockerfile                     // Configuración de Docker
  ├── docker-compose.yml              // Orquestación de contenedores
  ├── pom.xml                         // Dependencias del proyecto
```
---

## Requisitos Previos

-**Java JDK 23**

-**Maven**

-**Docker**

-**Base de datos MySQL y MongoDB**

---

## Dependencias Principales

| Dependencia        | Descripción                          |
|--------------------|--------------------------------------|
| Spring Boot        | Framework principal del proyecto    |
| Swagger            | Documentación de la API            |
| Spring Data JPA    | Interacción con la base de datos SQL |
| Spring Data MongoDB | Interacción con la base de datos NoSQL |

---

## Instalación

Para clonar y ejecutar este proyecto en tu entorno local:

### Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/blackjack-api.git
cd blackjack-api
```

---

## Configuración

### Base de Datos

Edita el archivo `application.properties` con las credenciales de tu base de datos:

#### MYSQL
```bash
spring.r2dbc.url=r2dbc:pool:mysql://localhost:3306/blackjack
spring.r2dbc.username=root
spring.r2dbc.password=raul
```
#### MongoDB
```bash
spring.data.mongodb.uri=mongodb://localhost:27017/blackjack
```
---

## Ejecución

### Usando Maven

1. Compila e instala el proyecto:

   ```bash
   mvn clean install
   ```

2. Ejecuta la aplicación:

   ```bash
   mvn spring-boot:run
   ```

   ---

### Usando Docker

 1.  Construye y ejecuta el contenedor:

     ```bash
     docker-compose up --build
     ```
    
 2. Accede a la API en:

    ```bash
    http://localhost:8080
    ```
    ---

    ## Documentación de la API

    Accede a la documentación Swagger en:

    ```bash
    http://localhost:8080/webjars/swagger-ui/index.html
    ```

    ---

    ## Endpoints Principales

    ### Blackjack

 | Método | Endpoint                 | Descripción                                |
|--------|---------------------------|--------------------------------------------|
| POST   | `/game/new`               | Crea una nueva partida de Blackjack.      |
| GET    | `/game/{id}`              | Obtiene los detalles de una partida.      |
| POST   | `/game/{id}/play`         | Realiza una jugada en una partida.        |
| DELETE | `/game/{id}/delete`       | Elimina una partida específica.           |
| GET    | `/ranking`                | Obtiene el ranking de jugadores.          |
| PUT    | `/player/{playerId}`      | Modifica la información de un jugador.    |
| GET    | `/`                       | Manejo de la raíz de la API.              |

---

    




