# spring-kotlin-playground

## Project Structure

| Folder                 | Description                                                                                     |
|-----------------------|-------------------------------------------------------------------------------------------------|
| `api/`                | Contains GraphQL queries and mutations, serving as the entry point for API requests.           |
| `api/model/`         | Defines data transfer objects (DTOs) for creating and updating pets, ensuring structured data handling. |
| `api/mapper/`        | Contains mappers that convert between domain models and API models for consistent data representation. |
| `model/`             | Contains the domain models representing the business logic of the application, such as `Pet`.        |
| `service/`           | Contains service classes that encapsulate business logic, such as handling pet creation, updates, and retrieval. |
| `persistence/`       | Manages data access, including repositories and database table definitions for interacting with the underlying database. |

## Data Flow: API to Database

```plaintext
+------------------+              +------------------+              +---------------------+              +---------------------+
|     API Layer    |              |  Service Layer   |              |  Persistence Layer  |              |     Database        |
|  (GraphQL Query, |              |   (PetService)   |              |  (PetRepository,    |              |   (PetTable)        |
|  Mutation)       |              |                  |              |   PetTable)         |              |                     |
|------------------|              |------------------|              |---------------------|              |---------------------|
|  1. Incoming     |   invokes    | 2. Business      |   invokes    | 3. Repository       |  executes    | 4. Data persisted   |
|     API request  +------------> |    Logic         +------------> |    interacts with   +------------> |    in the database  |
|     (createPet,  |              |    (PetService)  |              |    database entity  |              |  (INSERT/UPDATE)    |
|     updatePet)   |              |                  |              |                     |              |                     |
+------------------+              +------------------+              +---------------------+              +---------------------+
```

## Create Pet

```shell
curl -s -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { createPet(input: { name: \"Buddy\", category: \"Dog\", status: \"Available\" }) { id name category status } }"
}' --verbose
```

## Update Pet

```shell
curl -s -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { updatePet(id: \"PET_ID\", input: { name: \"Max\", category: \"Dog\", status: \"Adopted\" }) { id name category status } }"
}' --verbose
```

## Get Pet

```shell
curl -s -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "query { pet(id: \"PET_ID\") { id name category status } }"
}' | jq .
```

## List All Pets

```shell
curl -s -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "query { pets { id name category status } }"
}' | jq .
```