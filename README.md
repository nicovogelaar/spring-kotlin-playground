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

## Database Configuration

This project supports using H2 in-memory database for local testing and PostgreSQL for production. You can easily switch between them by modifying the `application.yml` file.

### H2 Database (Local Testing)

To use the H2 database for local testing, ensure the following configuration is set in your `application.yml`:

```yaml
datasource:
  url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  driver-class-name: org.h2.Driver
  username: sa
  password:
```

### PostgreSQL Database (Production)

For production use, you can configure PostgreSQL in your `application.yml` as follows:

```yaml
datasource:
  url: jdbc:postgresql://localhost:5432/postgres
  driver-class-name: org.postgresql.Driver
  username: postgres
  password: postgres
```

Make sure to replace the placeholder values (localhost, 5432, postgres, postgres) with your actual PostgreSQL connection details.

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

## Get Store

```shell
curl -s -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "query { store(id: \"<STORE_ID>\") { id name location inventory { id name} } }"
}' | jq .
```

## List All Stores

```shell
curl -s -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "query { stores { id name location inventory { id name} } }"
}' | jq .
```

## Create a New Store

```shell
curl -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { createStore(input: { name: \"My Store\", location: \"123 Main St\" }) { id name location } }"
}'
```

## Update an Existing Store

```shell
curl -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { updateStore(id: \"<STORE_ID>\", input: { name: \"Updated Store\", location: \"456 Elm St\" }) { id name location } }"
}'
```

## Add a Pet to a Store

```shell
curl -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { addPetToStore(storeId: \"<STORE_ID>\", petId: \"<PET_ID>\") { id name location } }"
}'
```

## Remove a Pet from a Store

```shell
curl -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { removePetFromStore(storeId: \"<STORE_ID>\", petId: \"<PET_ID>\") { id name location } }"
}'
```

## Place Order

```shell
curl -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { placeOrder(storeId: \"<STORE_ID>\", petId: \"<PET_ID>\") { id store { id name location } pet { id name category } } }"
}'
```
