package com.nicovogelaar.playground.service

import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.persistence.inmemory.InMemoryPetRepository
import com.nicovogelaar.playground.persistence.inmemory.InMemoryStoreRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class StoreServiceTest {
    private lateinit var storeRepo: InMemoryStoreRepository
    private lateinit var petRepo: InMemoryPetRepository
    private lateinit var storeService: StoreService

    @BeforeEach
    fun setUp() {
        storeRepo = InMemoryStoreRepository()
        petRepo = InMemoryPetRepository()
        storeService = StoreService(storeRepo, petRepo)
    }

    @Test
    fun `should create store, add pet, get store and list stores`() {
        // Given
        val petId = UUID.randomUUID()
        val pet = Pet(id = petId, name = "Buddy", category = "Dog", status = "Available")
        petRepo.createPet(pet)

        // When
        val createdStore = storeService.createStore("Pet Store", "123 Main St")
        assertNotNull(createdStore) { "Store creation failed" }

        storeService.addPetToStore(createdStore!!, pet)

        // Then
        val retrievedStore = storeService.getStoreById(createdStore.id)
        assertNotNull(retrievedStore)

        assertEquals(retrievedStore?.name, createdStore.name)
        assertEquals(retrievedStore?.location, createdStore.location)

        // Check inventory
        assertTrue(retrievedStore?.inventory?.isNotEmpty() ?: false)
        assertEquals(retrievedStore?.inventory?.first()?.id, petId)

        // List stores
        val allStores = storeService.listStores()
        assertEquals(1, allStores.size)
        assertEquals(allStores.first().id, createdStore.id)
    }
}
