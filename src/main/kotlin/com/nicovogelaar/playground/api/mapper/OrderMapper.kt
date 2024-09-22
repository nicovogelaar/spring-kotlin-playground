package com.nicovogelaar.playground.api.mapper

import com.nicovogelaar.playground.model.DraftOrder
import com.nicovogelaar.playground.model.Order
import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.model.Store
import com.nicovogelaar.playground.api.model.Order as ApiOrder

object OrderMapper {
    fun toApiOrder(order: Order): ApiOrder {
        return ApiOrder(
            id = order.id,
            store = StoreMapper.toApiStore(order.store),
            pet = PetMapper.toApiPet(order.pet),
        )
    }

    fun toDraftOrder(
        store: Store,
        pet: Pet,
    ): DraftOrder {
        return DraftOrder(store, pet)
    }
}
