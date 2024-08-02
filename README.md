# RepoChecker

RepoChecker is a Spring Boot application designed to search for GitHub users and their repositories. It provides endpoints to fetch repositories and supports both caching and error handling for user-friendly responses.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech](#tech)
- [API Endpoints](#api-endpoints)
- [Logging](#logging)
- [Running Tests](#running-tests)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## Features

- Fetch GitHub/GitLab repositories for a given user.
- Handles errors with custom error messages.
- Caching support for improved performance.
- Uses personal access tokens for free api limitation bypass
- Dockerized for easy deployment.

## Architecture

This project follows a Hexagonal Architecture and is divided into four modules:
- `monolith`
- `domain`
- `app`
- `adapter`

## Tech

- Java 21, Gradle 8.8, Spring Boot 3.2.8
- Spring Data + H2 (in memory)
- Docker, Docker Compose
- Tested with Spock
- Front: SPA made with HTML, CSS, HTMX

## API Endpoints

ApiController serves an endpoint at `/api/github/{user}` with user as Spring path variable. Works only with GitHub,
optionally accepts tokens to bypass request limitations for anonymous users. Returns JSON objects.

ViewController serves an endpoint at `/search` and returns chunks of HTML code for HTMX framework to display. 
Works with GitHub and GitLab, accepts tokens.

API endpoints are exposed with Swagger at `/swagger-ui/index.html`

## Logging

The application uses Logback for logging. By default, logs are printed to the console. To change the logging configuration, modify the logback.xml file.

## Running Tests

Run the unit tests using Gradle:

`./gradlew test`

Unit tests are written in Groovy with Spock, integration tests use JUnit.

## Deployment

App is currently deployed at AWS EC2 instance with Docker and available at:

`http://ec2-13-60-35-202.eu-north-1.compute.amazonaws.com:8080` (or http://tinyurl.com/repo-checker)

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.

