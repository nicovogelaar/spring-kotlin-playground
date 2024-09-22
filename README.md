# spring-kotlin-playground

## Lint

```shell
./gradlew ktlintFormat
```

## Create Pet

```shell
curl -s -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { createPet(name: \"Buddy\", category: \"Dog\", status: \"Available\") { id name category status } }"
}' --verbose
```

## Update Pet

```shell
curl -s -X POST http://localhost:8080/graphql \
-H "Content-Type: application/json" \
-d '{
  "query": "mutation { updatePet(id: \"PET_ID\", name: \"Max\", category: \"Dog\", status: \"Adopted\") { id name category status } }"
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