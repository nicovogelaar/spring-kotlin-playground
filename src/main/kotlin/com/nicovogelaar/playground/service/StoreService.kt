package com.nicovogelaar.playground.service

import com.nicovogelaar.playground.model.DraftOrder
import com.nicovogelaar.playground.model.Order
import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.model.Store
import com.nicovogelaar.playground.persistence.PetReadRepository
import com.nicovogelaar.playground.persistence.StoreRepository
import com.nicovogelaar.playground.security.getUserRoles
import mu.KotlinLogging
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import java.util.UUID

interface StoreGetter {
    suspend fun getStoreById(id: UUID): Store?

    suspend fun listStores(): List<Store>
}

interface StoreCreator {
    suspend fun createStore(
        name: String,
        location: String,
    ): Store?
}

interface StoreUpdater {
    suspend fun updateStore(
        id: UUID,
        store: Store,
    ): Store?

    suspend fun addPetToStore(
        store: Store,
        pet: Pet,
    ): Store?

    suspend fun removePetFromStore(
        store: Store,
        pet: Pet,
    ): Store?
}

interface OrderPlacer {
    suspend fun placeOrder(order: DraftOrder): Order?
}

private val logger = KotlinLogging.logger {}

@Service
class StoreService(
    private val storeRepo: StoreRepository,
    private val petRepo: PetReadRepository,
) : StoreGetter, StoreCreator, StoreUpdater, OrderPlacer {
    override suspend fun getStoreById(id: UUID): Store? = storeRepo.getStoreById(id)?.copy(inventory = getInventoryForStore(id))

    override suspend fun listStores(): List<Store> {
        val roles = ReactiveSecurityContextHolder.getContext().getUserRoles()

        logger.debug { "roles: $roles" }

        val stores = storeRepo.getAllStores()

        return stores.map { store ->
            val petIds = storeRepo.getPetIdsForStore(store.id)
            val pets = petRepo.getPetsByIds(petIds)
            store.copy(inventory = pets)
        }
    }

    override suspend fun createStore(
        name: String,
        location: String,
    ): Store? {
        val newStore =
            Store(
                id = UUID.randomUUID(),
                name = name,
                location = location,
                inventory = emptyList(),
            )

        return storeRepo.createStore(newStore)
    }

    override suspend fun updateStore(
        id: UUID,
        store: Store,
    ): Store? {
        val existingStore = storeRepo.getStoreById(id) ?: return null

        val updatedStore =
            existingStore.copy(
                name = store.name,
                location = store.location,
            )

        return storeRepo.updateStore(id, updatedStore)
    }

    override suspend fun addPetToStore(
        store: Store,
        pet: Pet,
    ): Store? {
        storeRepo.addPetToStore(store.id, pet.id)

        return getStoreById(store.id)
    }

    override suspend fun removePetFromStore(
        store: Store,
        pet: Pet,
    ): Store? {
        storeRepo.removePetFromStore(store.id, pet.id)

        return getStoreById(store.id)
    }

    override suspend fun placeOrder(order: DraftOrder): Order? {
        val inventory = getInventoryForStore(order.store.id)

        if (!inventory.any { it.id == order.pet.id }) {
            return null
        }

        removePetFromStore(order.store, order.pet)

        return Order(UUID.randomUUID(), order.store, order.pet)
    }

    private fun getInventoryForStore(storeId: UUID): List<Pet> {
        val petIds = storeRepo.getPetIdsForStore(storeId)
        return petRepo.getPetsByIds(petIds)
    }
}
