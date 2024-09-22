.PHONY: run-postgres stop-postgres lint

lint:
	./gradlew ktlintFormat

run-postgres:
	docker run --name postgres-spring-kotlin-playground \
		-e POSTGRES_USER=postgres \
		-e POSTGRES_PASSWORD=postgres \
		-p 5432:5432 --rm -d \
		postgres:16

stop-postgres:
	docker stop postgres-spring-kotlin-playground