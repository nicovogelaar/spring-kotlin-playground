package com.nicovogelaar.playground.service

import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.persistence.PetReadRepository
import com.nicovogelaar.playground.persistence.PetWriteRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PetService(
    // Using smaller interfaces for demo/testing purposes
    private val petReadRepo: PetReadRepository,
    private val petWriteRepo: PetWriteRepository,
) {
    fun createPet(
        name: String,
        category: String,
        status: String,
    ): Pet? {
        val pet =
            Pet(
                id = UUID.randomUUID(),
                name = name,
                category = category,
                status = status,
            )
        return petWriteRepo.createPet(pet)
    }

    fun getPetById(id: UUID): Pet? {
        return petReadRepo.getPetById(id)
    }

    fun getAllPets(): List<Pet> {
        return petReadRepo.getAllPets()
    }

    fun updatePet(
        id: UUID,
        pet: Pet,
    ): Pet? {
        val existingPet = petReadRepo.getPetById(id) ?: return null

        val updatedPet =
            Pet(
                id = existingPet.id,
                name = pet.name,
                category = pet.category,
                status = pet.status,
            )

        return petWriteRepo.updatePet(id, updatedPet)
    }
}
