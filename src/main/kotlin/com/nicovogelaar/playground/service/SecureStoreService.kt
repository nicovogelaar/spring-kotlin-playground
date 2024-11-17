package com.nicovogelaar.playground.service

import com.nicovogelaar.playground.model.DraftOrder
import com.nicovogelaar.playground.model.Order
import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.model.Store
import com.nicovogelaar.playground.security.getUserRoles
import org.springframework.context.annotation.Primary
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import java.util.UUID

@Primary
@Service
class SecureStoreService(
    private val storeService: StoreService,
) : StoreGetter, StoreCreator, StoreUpdater, OrderPlacer {
    override suspend fun getStoreById(id: UUID): Store? {
        checkUserRole("ROLE_USER")

        return storeService.getStoreById(id)
    }

    override suspend fun listStores(): List<Store> {
        checkUserRole("ROLE_USER")

        return storeService.listStores()
    }

    override suspend fun createStore(
        name: String,
        location: String,
    ): Store? {
        checkUserRole("ROLE_USER")

        return storeService.createStore(name, location)
    }

    override suspend fun updateStore(
        id: UUID,
        store: Store,
    ): Store? {
        checkUserRole("ROLE_USER")

        return storeService.updateStore(id, store)
    }

    override suspend fun addPetToStore(
        store: Store,
        pet: Pet,
    ): Store? {
        checkUserRole("ROLE_USER")

        return storeService.addPetToStore(store, pet)
    }

    override suspend fun removePetFromStore(
        store: Store,
        pet: Pet,
    ): Store? {
        checkUserRole("ROLE_USER")

        return storeService.removePetFromStore(store, pet)
    }

    override suspend fun placeOrder(order: DraftOrder): Order? {
        checkUserRole("ROLE_USER")

        return storeService.placeOrder(order)
    }

    private suspend fun checkUserRole(requiredRole: String) {
        val roles = ReactiveSecurityContextHolder.getContext().getUserRoles()

        if (requiredRole !in roles) {
            throw SecurityException("User does not have the required role: $requiredRole")
        }
    }
}
