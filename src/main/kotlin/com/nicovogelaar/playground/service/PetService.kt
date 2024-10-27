package com.nicovogelaar.playground.service

import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.persistence.PetReadRepository
import com.nicovogelaar.playground.persistence.PetWriteRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID

interface PetGetter {
    fun getPetById(id: UUID): Pet?

    fun listPets(): List<Pet>
}

interface PetCreator {
    fun createPet(
        name: String,
        category: String,
        status: String,
    ): Pet?
}

interface PetUpdater {
    fun updatePet(
        id: UUID,
        pet: Pet,
    ): Pet?
}

private val logger = KotlinLogging.logger {}

@Service
class PetService(
    // Using smaller interfaces for demo/testing purposes
    private val petReadRepo: PetReadRepository,
    private val petWriteRepo: PetWriteRepository,
) : PetGetter, PetCreator, PetUpdater {
    override fun createPet(
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

    override fun getPetById(id: UUID): Pet? {
        return petReadRepo.getPetById(id)
    }

    override fun listPets(): List<Pet> {
        logger.info { "Get all pets..." }
        return petReadRepo.getAllPets()
    }

    override fun updatePet(
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
