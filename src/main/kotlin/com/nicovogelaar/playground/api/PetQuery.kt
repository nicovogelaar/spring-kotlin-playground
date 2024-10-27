package com.nicovogelaar.playground.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.nicovogelaar.playground.api.mapper.PetMapper
import com.nicovogelaar.playground.api.model.Pet
import com.nicovogelaar.playground.service.PetGetter
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class PetQuery(private val petGetter: PetGetter) : Query {
    @GraphQLDescription("Fetch a pet by its ID")
    fun pet(id: UUID): Pet? {
        return petGetter.getPetById(id)?.let { PetMapper.toApiPet(it) }
    }

    @GraphQLDescription("Get a list of all pets")
    suspend fun pets(): List<Pet> {
        logger.info { "Fetching all pets..." }
        delay(10000)
        return petGetter.listPets().map { PetMapper.toApiPet(it) }
    }
}
