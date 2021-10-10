# Weather Aggregator

This project is done as a part of the course ID2221 at KTH.

## File structure

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
├── http-proxy            // HTTP proxy for the UI to send requests to the backend
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

## Environment variables

Sensitive environment variables should be set inside a `.env` file, that is not committed to the repository. However, a `.env.example` file is provided as a template.

```
cp .env.example .env
```

### `OWM_API_KEY`

The OpenWeatherMap API key, used to access the API. If this is not provided, the API will not be used.
