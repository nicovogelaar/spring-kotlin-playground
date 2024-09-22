package com.nicovogelaar.playground.api.mapper

import com.nicovogelaar.playground.model.Store
import java.util.UUID
import com.nicovogelaar.playground.api.model.Store as ApiStore
import com.nicovogelaar.playground.api.model.StoreUpdate as ApiStoreUpdate

object StoreMapper {
    fun toApiStore(store: Store): ApiStore {
        return ApiStore(
            id = store.id,
            name = store.name,
            location = store.location,
            inventory = store.inventory.map { PetMapper.toApiPet(it) },
        )
    }

    fun toStoreModel(
        id: UUID,
        apiStoreUpdate: ApiStoreUpdate,
    ): Store {
        return Store(
            id = id,
            name = apiStoreUpdate.name,
            location = apiStoreUpdate.location,
            inventory = emptyList(),
        )
    }
}
