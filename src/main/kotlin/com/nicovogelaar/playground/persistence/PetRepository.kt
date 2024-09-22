package com.nicovogelaar.playground.persistence

import com.nicovogelaar.playground.model.Pet
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

interface PetReadRepository {
    fun getPetById(id: UUID): Pet?

    fun getAllPets(): List<Pet>
}

interface PetWriteRepository {
    fun createPet(pet: Pet): Pet?

    fun updatePet(
        id: UUID,
        pet: Pet,
    ): Pet?
}

@Component
class ExposedPetRepository : PetReadRepository, PetWriteRepository {
    override fun createPet(pet: Pet): Pet? {
        return transaction {
            val newId = UUID.randomUUID()

            PetTable.insert {
                it[id] = newId
                it[name] = pet.name
                it[category] = pet.category
                it[status] = pet.status
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }

            getPetById(newId)
        }
    }

    override fun updatePet(
        id: UUID,
        pet: Pet,
    ): Pet? {
        return transaction {
            PetTable.update({ PetTable.id eq id }) {
                it[name] = pet.name
                it[category] = pet.category
                it[status] = pet.status
                it[updatedAt] = LocalDateTime.now()
            }

            getPetById(id)
        }
    }

    override fun getPetById(id: UUID): Pet? {
        return transaction {
            PetTable.selectAll().where { PetTable.id eq id }
                .mapNotNull { row ->
                    Pet(
                        id = row[PetTable.id],
                        name = row[PetTable.name],
                        category = row[PetTable.category],
                        status = row[PetTable.status],
                        createdAt = row[PetTable.createdAt],
                        updatedAt = row[PetTable.updatedAt],
                    )
                }
                .singleOrNull()
        }
    }

    override fun getAllPets(): List<Pet> {
        return transaction {
            PetTable.selectAll().mapNotNull { row ->
                Pet(
                    id = row[PetTable.id],
                    name = row[PetTable.name],
                    category = row[PetTable.category],
                    status = row[PetTable.status],
                    createdAt = row[PetTable.createdAt],
                    updatedAt = row[PetTable.updatedAt],
                )
            }
        }
    }
}
