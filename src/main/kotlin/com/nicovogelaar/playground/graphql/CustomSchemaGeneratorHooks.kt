package com.nicovogelaar.playground.graphql

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLType
import org.springframework.beans.factory.BeanFactoryAware
import reactor.core.publisher.Mono
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

/**
 * Schema generator hook that adds additional scalar types.
 */
class CustomSchemaGeneratorHooks(override val wiringFactory: KotlinDirectiveWiringFactory) : SchemaGeneratorHooks {
    /**
     * Register additional GraphQL scalar types.
     */
    override fun willGenerateGraphQLType(type: KType): GraphQLType? =
        when (type.classifier) {
            UUID::class -> graphqlUUIDType
            else -> super.willGenerateGraphQLType(type)
        }

    /**
     * Register Reactor Mono monad type.
     */
    override fun willResolveMonad(type: KType): KType =
        when (type.classifier) {
            Mono::class -> type.arguments.first().type ?: type
            Set::class -> List::class.createType(type.arguments)
            else -> type
        }

    /**
     * Exclude the Spring bean factory interface
     */
    override fun isValidSuperclass(kClass: KClass<*>): Boolean {
        return when {
            kClass.isSubclassOf(BeanFactoryAware::class) -> false
            else -> super.isValidSuperclass(kClass)
        }
    }
}
