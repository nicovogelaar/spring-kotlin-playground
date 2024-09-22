package com.nicovogelaar.playground.persistence

import com.nicovogelaar.playground.model.Store
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

interface StoreRepository {
    fun getStoreById(id: UUID): Store?

    fun getAllStores(): List<Store>

    fun createStore(store: Store): Store?

    fun updateStore(
        id: UUID,
        store: Store,
    ): Store?

    fun addPetToStore(
        storeId: UUID,
        petId: UUID,
    ): Boolean

    fun removePetFromStore(
        storeId: UUID,
        petId: UUID,
    ): Boolean

    fun getPetIdsForStore(storeId: UUID): List<UUID>
}

@Component
@Primary
class ExposedStoreRepository : StoreRepository {
    override fun createStore(store: Store): Store? {
        return transaction {
            val newId = UUID.randomUUID()

            StoreTable.insert {
                it[id] = newId
                it[name] = store.name
                it[location] = store.location
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }

            getStoreById(newId)
        }
    }

    override fun updateStore(
        id: UUID,
        store: Store,
    ): Store? {
        return transaction {
            StoreTable.update({ StoreTable.id eq id }) {
                it[name] = store.name
                it[location] = store.location
                it[updatedAt] = LocalDateTime.now()
            }

            getStoreById(id)
        }
    }

    override fun getStoreById(id: UUID): Store? {
        return transaction {
            StoreTable.selectAll().where { StoreTable.id eq id }
                .mapNotNull { row ->
                    Store(
                        id = row[StoreTable.id],
                        name = row[StoreTable.name],
                        location = row[StoreTable.location],
                        inventory = emptyList(),
                    )
                }
                .singleOrNull()
        }
    }

    override fun getAllStores(): List<Store> {
        return transaction {
            StoreTable.selectAll().mapNotNull { row ->
                Store(
                    id = row[StoreTable.id],
                    name = row[StoreTable.name],
                    location = row[StoreTable.location],
                    inventory = emptyList(),
                )
            }
        }
    }

    override fun addPetToStore(
        storeId: UUID,
        petId: UUID,
    ): Boolean {
        return transaction {
            StorePetTable.selectAll().where {
                (StorePetTable.storeId eq storeId) and
                    (StorePetTable.petId eq petId)
            }.singleOrNull()?.let {
                false
            } ?: run {
                StorePetTable.insert {
                    it[StorePetTable.storeId] = storeId
                    it[StorePetTable.petId] = petId
                }
                true
            }
        }
    }

    override fun removePetFromStore(
        storeId: UUID,
        petId: UUID,
    ): Boolean {
        return transaction {
            val rowsAffected =
                StorePetTable.deleteWhere {
                    (StorePetTable.storeId eq storeId) and (StorePetTable.petId eq petId)
                }
            rowsAffected > 0
        }
    }

    override fun getPetIdsForStore(storeId: UUID): List<UUID> {
        return transaction {
            StorePetTable
                .selectAll().where { StorePetTable.storeId eq storeId }
                .map { it[StorePetTable.petId] }
        }
    }
}

@Component
class InMemoryStoreRepository : StoreRepository {
    private val stores = mutableListOf<StoreEntity>()
    private val storePets = mutableMapOf<UUID, MutableList<UUID>>()

    override fun createStore(store: Store): Store? {
        val newStore = StoreEntity(
            id = store.id,
            name = store.name,
            location = store.location,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        stores.add(newStore)
        return newStore.toStore()
    }

    override fun updateStore(id: UUID, store: Store): Store? {
        val existingStoreEntity = stores.find { it.id == id } ?: return null
        val updatedStore = existingStoreEntity.copy(
            name = store.name,
            location = store.location,
            updatedAt = LocalDateTime.now()
        )
        stores[stores.indexOf(existingStoreEntity)] = updatedStore
        return updatedStore.toStore()
    }

    override fun getStoreById(id: UUID): Store? {
        return stores.find { it.id == id }?.let { it.toStore() }
    }

    override fun getAllStores(): List<Store> {
        return stores.map { it.toStore() }
    }

    override fun addPetToStore(storeId: UUID, petId: UUID): Boolean {
        return if (storePets[storeId]?.contains(petId) == true) {
            false
        } else {
            storePets.computeIfAbsent(storeId) { mutableListOf() }.add(petId)
            true
        }
    }

    override fun removePetFromStore(storeId: UUID, petId: UUID): Boolean {
        return storePets[storeId]?.remove(petId) ?: false
    }

    override fun getPetIdsForStore(storeId: UUID): List<UUID> {
        return storePets[storeId]?.toList() ?: emptyList()
    }
}

data class StoreEntity (
    val id: UUID,
    val name: String,
    val location: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

private fun StoreEntity.toStore(): Store {
    return Store(
        id = this.id,
        name = this.name,
        location = this.location,
        inventory = emptyList(),
    )
}
