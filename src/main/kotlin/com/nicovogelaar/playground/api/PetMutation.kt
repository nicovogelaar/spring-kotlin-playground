package com.nicovogelaar.playground.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.nicovogelaar.playground.api.mapper.PetMapper
import com.nicovogelaar.playground.api.model.PetCreate
import com.nicovogelaar.playground.api.model.PetUpdate
import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.service.PetService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PetMutation(private val petService: PetService) : Mutation {
    @GraphQLDescription("Create a new pet")
    fun createPet(input: PetCreate): Pet? {
        return petService.createPet(input.name, input.category, input.status)
    }

    @GraphQLDescription("Update an existing pet")
    fun updatePet(
        id: UUID,
        input: PetUpdate,
    ): Pet? {
        val pet = PetMapper.toPetModel(id, input)

        return petService.updatePet(id, pet)
    }
}
