package com.nicovogelaar.playground.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.nicovogelaar.playground.api.mapper.OrderMapper
import com.nicovogelaar.playground.api.mapper.StoreMapper
import com.nicovogelaar.playground.api.model.Order
import com.nicovogelaar.playground.api.model.Store
import com.nicovogelaar.playground.api.model.StoreCreate
import com.nicovogelaar.playground.api.model.StoreUpdate
import com.nicovogelaar.playground.service.OrderPlacer
import com.nicovogelaar.playground.service.PetGetter
import com.nicovogelaar.playground.service.StoreCreator
import com.nicovogelaar.playground.service.StoreGetter
import com.nicovogelaar.playground.service.StoreUpdater
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class StoreMutation(
    private val storeGetter: StoreGetter,
    private val storeCreator: StoreCreator,
    private val storeUpdater: StoreUpdater,
    private val orderPlacer: OrderPlacer,
    private val petGetter: PetGetter,
) : Mutation {
    @GraphQLDescription("Create a new store")
    suspend fun createStore(input: StoreCreate): Store? {
        return storeCreator.createStore(input.name, input.location)?.let { StoreMapper.toApiStore(it) }
    }

    @GraphQLDescription("Update an existing store")
    suspend fun updateStore(
        id: UUID,
        input: StoreUpdate,
    ): Store? {
        val store = StoreMapper.toStoreModel(id, input)

        return storeUpdater.updateStore(id, store)?.let { StoreMapper.toApiStore(it) }
    }

    @GraphQLDescription("Add a pet to a store")
    suspend fun addPetToStore(
        storeId: UUID,
        petId: UUID,
    ): Store? {
        val store = storeGetter.getStoreById(storeId) ?: return null
        val pet = petGetter.getPetById(petId) ?: return null

        return storeUpdater.addPetToStore(store, pet)?.let { StoreMapper.toApiStore(it) }
    }

    @GraphQLDescription("Remove a pet from a store")
    suspend fun removePetFromStore(
        storeId: UUID,
        petId: UUID,
    ): Store? {
        val store = storeGetter.getStoreById(storeId) ?: return null
        val pet = petGetter.getPetById(petId) ?: return null

        return storeUpdater.removePetFromStore(store, pet)?.let { StoreMapper.toApiStore(it) }
    }

    @GraphQLDescription("Place an order for a pet in a store")
    suspend fun placeOrder(
        storeId: UUID,
        petId: UUID,
    ): Order? {
        val store = storeGetter.getStoreById(storeId) ?: return null
        val pet = petGetter.getPetById(petId) ?: return null

        val draftOrder = OrderMapper.toDraftOrder(store, pet)

        return orderPlacer.placeOrder(draftOrder)?.let { OrderMapper.toApiOrder(it) }
    }
}
