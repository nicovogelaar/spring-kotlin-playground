package com.nicovogelaar.playground.service

import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.model.Store
import com.nicovogelaar.playground.persistence.PetReadRepository
import com.nicovogelaar.playground.persistence.StoreRepository
import org.springframework.stereotype.Service
import java.util.UUID

interface StoreGetter {
    fun getStoreById(id: UUID): Store?

    fun listStores(): List<Store>
}

interface StoreCreator {
    fun createStore(
        name: String,
        location: String,
    ): Store?
}

interface StoreUpdater {
    fun updateStore(
        id: UUID,
        store: Store,
    ): Store?

    fun addPetToStore(
        store: Store,
        pet: Pet,
    ): Store?

    fun removePetFromStore(
        store: Store,
        pet: Pet,
    ): Store?
}

@Service
class StoreService(
    private val storeRepo: StoreRepository,
    private val petReadRepo: PetReadRepository,
) : StoreGetter, StoreCreator, StoreUpdater {
    override fun getStoreById(id: UUID): Store? =
        storeRepo.getStoreById(id) // Fetch the store
            ?.let { store ->
                storeRepo.getPetIdsForStore(id)
                    .let { petIds -> petReadRepo.getPetsByIds(petIds) }
                    .let { pets -> store.copy(inventory = pets) }
            }

    override fun listStores(): List<Store> {
        val stores = storeRepo.getAllStores()

        return stores.map { store ->
            val petIds = storeRepo.getPetIdsForStore(store.id)
            val pets = petReadRepo.getPetsByIds(petIds)
            store.copy(inventory = pets)
        }
    }

    override fun createStore(
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

    override fun updateStore(
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

    override fun addPetToStore(
        store: Store,
        pet: Pet,
    ): Store? {
        storeRepo.addPetToStore(store.id, pet.id)

        return getStoreById(store.id)
    }

    override fun removePetFromStore(
        store: Store,
        pet: Pet,
    ): Store? {
        storeRepo.removePetFromStore(store.id, pet.id)

        return getStoreById(store.id)
    }
}
