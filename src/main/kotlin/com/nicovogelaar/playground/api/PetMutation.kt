package com.nicovogelaar.playground.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.nicovogelaar.playground.api.mapper.PetMapper
import com.nicovogelaar.playground.api.model.Pet
import com.nicovogelaar.playground.api.model.PetCreate
import com.nicovogelaar.playground.api.model.PetUpdate
import com.nicovogelaar.playground.service.PetCreator
import com.nicovogelaar.playground.service.PetUpdater
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PetMutation(
    private val petCreator: PetCreator,
    private val petUpdater: PetUpdater,
) : Mutation {
    @GraphQLDescription("Create a new pet")
    fun createPet(input: PetCreate): Pet? {
        return petCreator.createPet(
            input.name,
            input.category,
            input.status,
        )?.let { PetMapper.toApiPet(it) }
    }

    @GraphQLDescription("Update an existing pet")
    fun updatePet(
        id: UUID,
        input: PetUpdate,
    ): Pet? {
        val pet = PetMapper.toPetModel(id, input)

        return petUpdater.updatePet(id, pet)?.let { PetMapper.toApiPet(it) }
    }
}
