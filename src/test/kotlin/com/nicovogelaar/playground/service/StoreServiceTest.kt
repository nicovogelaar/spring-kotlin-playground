package com.nicovogelaar.playground.service

import com.nicovogelaar.playground.model.DraftOrder
import com.nicovogelaar.playground.model.Pet
import com.nicovogelaar.playground.persistence.InMemoryPetRepository
import com.nicovogelaar.playground.persistence.InMemoryStoreRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import reactor.core.publisher.Mono
import java.util.UUID

class StoreServiceTest {
    private lateinit var storeRepo: InMemoryStoreRepository
    private lateinit var petRepo: InMemoryPetRepository
    private lateinit var storeService: StoreService

    private lateinit var authorities: List<GrantedAuthority>
    private lateinit var authentication: Authentication
    private lateinit var securityContext: SecurityContext

    @BeforeEach
    fun setUp() {
        storeRepo = InMemoryStoreRepository()
        petRepo = InMemoryPetRepository()
        storeService = StoreService(storeRepo, petRepo)

        authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        authentication = UsernamePasswordAuthenticationToken("test-user", "password", authorities)
        securityContext = SecurityContextImpl(authentication)
    }

    @Test
    fun `should create store, add pet, and verify inventory with roles`() =
        runTest {
            ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)).let {
                // Given
                val petId = UUID.randomUUID()
                val pet = Pet(id = petId, name = "Buddy", category = "Dog", status = "Available")
                petRepo.createPet(pet)

                val createdStore = storeService.createStore("Pet Store", "123 Main St")
                assertNotNull(createdStore) { "Store creation failed" }

                storeService.addPetToStore(createdStore!!, pet)

                // When
                val retrievedStore = storeService.getStoreById(createdStore.id)

                // Then
                assertNotNull(retrievedStore)
                assertEquals(retrievedStore?.inventory?.first()?.id, petId)
            }
        }

    @Test
    fun `should place order and remove pet from store`() =
        runTest {
            ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)).let {
                // Given
                val petId = UUID.randomUUID()
                val pet = Pet(id = petId, name = "Buddy", category = "Dog", status = "Available")
                petRepo.createPet(pet)

                val createdStore = storeService.createStore("Pet Store", "123 Main St")
                assertNotNull(createdStore) { "Store creation failed" }

                storeService.addPetToStore(createdStore!!, pet)

                // When
                val order = DraftOrder(createdStore, pet)
                val orderPlaced = storeService.placeOrder(order)

                // Then
                assertNotNull(orderPlaced, "Order should be placed successfully")

                // Verify the pet is removed from the store's inventory
                val updatedStore = storeService.getStoreById(createdStore.id)
                assertNotNull(updatedStore)
                assertTrue(updatedStore?.inventory?.none { it.id == petId } == true, "Pet should be removed from inventory")
            }
        }
}
