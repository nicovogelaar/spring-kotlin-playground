package com.nicovogelaar.playground.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.nicovogelaar.playground.api.mapper.StoreMapper
import com.nicovogelaar.playground.api.model.Store
import com.nicovogelaar.playground.service.StoreGetter
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class StoreQuery(private val storeGetter: StoreGetter) : Query {
    @GraphQLDescription("Fetch a store by its ID")
    fun store(id: UUID): Store? {
        return storeGetter.getStoreById(id)?.let { StoreMapper.toApiStore(it) }
    }

    @GraphQLDescription("Get a list of all stores")
    fun stores(): List<Store> {
        return storeGetter.listStores().map { StoreMapper.toApiStore(it) }
    }
}
