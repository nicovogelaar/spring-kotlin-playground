package com.nicovogelaar.playground.persistence.inmemory

import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.persistence.PetReadRepository
import com.nicovogelaar.playground.persistence.PetWriteRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class InMemoryPetRepository : PetReadRepository, PetWriteRepository {
    private val pets = mutableListOf<PetEntity>()

    override fun createPet(pet: Pet): Pet? {
        val newPet =
            PetEntity(
                id = pet.id,
                name = pet.name,
                category = pet.category,
                status = pet.status,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

        pets.add(newPet)
        return newPet.toPet()
    }

    override fun updatePet(
        id: UUID,
        pet: Pet,
    ): Pet? {
        val existingPetEntity = pets.find { it.id == id } ?: return null
        val updatedPet =
            existingPetEntity.copy(
                name = pet.name,
                category = pet.category,
                status = pet.status,
                updatedAt = LocalDateTime.now(),
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
        status = this.status,
    )
}
