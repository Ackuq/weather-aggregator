# Weather Aggregator

This project is done as a part of the course ID2221 at KTH.

## File structure

This project follows a mono-repo structure. With all the different projects for the system existing in a dedicated folder. The folder structure is as follows:

```
├── README.md
├── Dockerfile.*          // Dockerfile for each subproject
├── docker-compose.yml    // Docker compose file for running all the services
│
├── ui                    // React application for the frontend
│   ├── package.json      // UI dependencies
│   └── src               // Source code for the UI
│       ├── App.tsx       // React component for the frontend
│       └── index.tsx     // Entry point for the frontend
│
├── http-proxy            // HTTP proxy using Play
│   ├── build.sbt         // SBT build file for the http proxy
│   ├── app               // Source code for the http proxy
│   |   ├── consumers     // Kafka consumers
│   |   ├── controllers   // HTTP controllers
│   |   ├── producers     // Kafka producers
│   |   └── utils         // Utility functions
│   └── conf              // Configuration files
│
├── scala-common          // Scala common library
│   ├── build.sbt         // SBT build file for the scala common library
│   └── src               // Source code for the scala common library
│
├── workers               // Workers for fetching data from different APIs
│   ├── build.sbt         // SBT build file for the workers
│   └── src/main/scala/id2221 // Source code for the workers
|       ├── connectors    // Connectors to the different API's
|       ├── models        // Internal abstractions of the API's response bodies
|       ├── exceptions    // Custom exceptions
|       ├── producers     // Kafka producers
|       ├── handlers      // Consumers for incoming requests
|       ├── unmarshallers // Unmarshallers for the API's response bodies
|       └── Main.scala    // Entry point for the workers
|
└── spark                 // Spark application for data processing
    ├── build.sbt         // SBT build file for the spark application
    └── src/main/scala/id2221 // Source code for the spark application
        └── Main.scala    // Entry point for the spark application
```

## Architecture

-   [Scala](https://www.scala-lang.org/) - Main language for the project.
-   [Kafka](https://kafka.apache.org/) - Used for messaging between the different services.
-   [Spark](https://spark.apache.org/) - Used for data processing.
-   [Play](https://www.playframework.com/) - Used for the HTTP proxy.
-   [React](https://reactjs.org/) - Used for the frontend.

## Getting started

### Requirements

-   [Docker](https://www.docker.com/)
-   [Docker Compose](https://docs.docker.com/compose/install/)

### Starting the services

```sh
$ docker compose up -d
```

### Retrieving logs

```sh
$ docker compose logs --follow
```

## Environment variables

Sensitive environment variables should be set inside a `.env` file, that is not committed to the repository. However, a `.env.example` file is provided as a template.

```
$ cp .env.example .env
```

### `OWM_API_KEY`

The OpenWeatherMap API key, used to access the API. If this is not provided, the API will not be used.
