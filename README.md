# RepoChecker
This project provides an API to list all non-fork GitHub repositories for a given user, including information on each branch and its last commit SHA. If the user does not exist, it returns a 404 response with a specified format.


## Technologies Used

- Java 21
- Gradle 8.7 (Groovy)
- Spring Boot 3.28
- GitHub REST API

## Prerequisites

- JDK 21
- Gradle 8.7
- Internet connection to access GitHub API

## Setup

1. Clone the repository:
    ```sh
    git clone https://github.com/keeeper85/RepoChecker.git
    cd RepoChecker
    ```

2. Build the project:
    ```sh
    ./gradlew build
    ```

3. Run the application:
    ```sh
    ./gradlew bootRun
    ```

## Usage

### Get Non-Fork Repositories

- **Endpoint**: `/api/github/{username}/repositories`
- **Method**: GET
- **Headers**: `Accept: application/json`

#### Request

```http
GET /api/github/{username}/repositories HTTP/1.1
Host: localhost:8080
Accept: application/json

