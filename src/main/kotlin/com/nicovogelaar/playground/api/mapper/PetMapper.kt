package com.nicovogelaar.playground.api.mapper

import com.nicovogelaar.playground.model.Pet
import java.util.UUID
import com.nicovogelaar.playground.api.model.Pet as ApiPet
import com.nicovogelaar.playground.api.model.PetUpdate as ApiPetUpdate

object PetMapper {
    fun toApiPet(pet: Pet): ApiPet {
        return ApiPet(
            id = pet.id,
            name = pet.name,
            category = pet.category,
            status = pet.status,
        )
    }

    fun toPetModel(
        id: UUID,
        apiPetUpdate: ApiPetUpdate,
    ): Pet {
        return Pet(
            id = id,
            name = apiPetUpdate.name,
            category = apiPetUpdate.category,
            status = apiPetUpdate.status,
        )
    }
}
