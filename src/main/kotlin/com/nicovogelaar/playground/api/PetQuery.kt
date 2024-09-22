package com.nicovogelaar.playground.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.nicovogelaar.playground.api.mapper.PetMapper
import com.nicovogelaar.playground.api.model.Pet
import com.nicovogelaar.playground.service.PetService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PetQuery(private val petService: PetService) : Query {
    @GraphQLDescription("Fetch a pet by its ID")
    fun pet(id: UUID): Pet? {
        return petService.getPetById(id)?.let { PetMapper.toApiPet(it) }
    }

    @GraphQLDescription("Get a list of all pets")
    fun pets(): List<Pet> {
        return petService.getAllPets().map { PetMapper.toApiPet(it) }
    }
}
