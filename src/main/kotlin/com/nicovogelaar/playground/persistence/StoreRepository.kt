package com.nicovogelaar.playground.persistence

import com.nicovogelaar.playground.model.Store
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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
