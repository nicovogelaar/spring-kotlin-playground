package com.nicovogelaar.playground.persistence

import com.nicovogelaar.playground.model.Store
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class InMemoryStoreRepository : StoreRepository {
    private val stores = mutableListOf<StoreEntity>()
    private val storePets = mutableMapOf<UUID, MutableList<UUID>>()

    override fun createStore(store: Store): Store? {
        val newStore =
            StoreEntity(
                id = store.id,
                name = store.name,
                location = store.location,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
        stores.add(newStore)
        return newStore.toStore()
    }

    override fun updateStore(
        id: UUID,
        store: Store,
    ): Store? {
        val existingStoreEntity = stores.find { it.id == id } ?: return null
        val updatedStore =
            existingStoreEntity.copy(
                name = store.name,
                location = store.location,
                updatedAt = LocalDateTime.now(),
            )
        stores[stores.indexOf(existingStoreEntity)] = updatedStore
        return updatedStore.toStore()
    }

    override fun getStoreById(id: UUID): Store? {
        return stores.find { it.id == id }?.toStore()
    }

    override fun getAllStores(): List<Store> {
        return stores.map { it.toStore() }
    }

    override fun addPetToStore(
        storeId: UUID,
        petId: UUID,
    ): Boolean {
        return if (storePets[storeId]?.contains(petId) == true) {
            false
        } else {
            storePets.computeIfAbsent(storeId) { mutableListOf() }.add(petId)
            true
        }
    }

    override fun removePetFromStore(
        storeId: UUID,
        petId: UUID,
    ): Boolean {
        return storePets[storeId]?.remove(petId) ?: false
    }

    override fun getPetIdsForStore(storeId: UUID): List<UUID> {
        return storePets[storeId]?.toList() ?: emptyList()
    }
}

private data class StoreEntity(
    val id: UUID,
    val name: String,
    val location: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

private fun StoreEntity.toStore(): Store {
    return Store(
        id = this.id,
        name = this.name,
        location = this.location,
        inventory = emptyList(),
    )
}
