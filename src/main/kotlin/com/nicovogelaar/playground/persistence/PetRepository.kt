package com.nicovogelaar.playground.persistence

import com.nicovogelaar.playground.model.Pet
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

interface PetReadRepository {
    fun getPetById(id: UUID): Pet?

    fun getPetsByIds(ids: List<UUID>): List<Pet>

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
@Primary
class ExposedPetRepository : PetReadRepository, PetWriteRepository {
    override fun createPet(pet: Pet): Pet? {
        return transaction {
            PetTable.insert {
                it[id] = pet.id
                it[name] = pet.name
                it[category] = pet.category
                it[status] = pet.status
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }

            getPetById(pet.id)
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
                    )
                }
                .singleOrNull()
        }
    }

    override fun getPetsByIds(ids: List<UUID>): List<Pet> {
        return transaction {
            PetTable
                .selectAll().where { PetTable.id inList ids }
                .mapNotNull { row ->
                    Pet(
                        id = row[PetTable.id],
                        name = row[PetTable.name],
                        category = row[PetTable.category],
                        status = row[PetTable.status],
                    )
                }
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
                )
            }
        }
    }
}

@Component
class InMemoryPetRepository : PetReadRepository, PetWriteRepository {
    private val pets = mutableListOf<PetEntity>()

    override fun createPet(pet: Pet): Pet? {
        val newPet = PetEntity(
            id = pet.id,
            name = pet.name,
            category = pet.category,
            status = pet.status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        pets.add(newPet)
        return newPet.toPet()
    }

    override fun updatePet(
        id: UUID,
        pet: Pet,
    ): Pet? {
        val existingPetEntity = pets.find { it.id == id } ?: return null
        val updatedPet = existingPetEntity.copy(
            name = pet.name,
            category = pet.category,
            status = pet.status,
            updatedAt = LocalDateTime.now()
        )

        pets[pets.indexOf(existingPetEntity)] = updatedPet
        return updatedPet.toPet()
    }

    override fun getPetById(id: UUID): Pet? {
        return pets.find { it.id == id }?.toPet()
    }

    override fun getPetsByIds(ids: List<UUID>): List<Pet> {
        return pets.filter { it.id in ids }.map { it.toPet() }
    }

    override fun getAllPets(): List<Pet> {
        return pets.map { it.toPet() }
    }
}

data class PetEntity(
    val id: UUID,
    val name: String,
    val category: String,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

private fun PetEntity.toPet(): Pet {
    return Pet(
        id = this.id,
        name = this.name,
        category = this.category,
        status = this.status
    )
}
