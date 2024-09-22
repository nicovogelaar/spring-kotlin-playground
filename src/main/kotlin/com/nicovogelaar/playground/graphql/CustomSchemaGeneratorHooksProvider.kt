package com.nicovogelaar.playground.graphql

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class CustomSchemaGeneratorHooksProvider : SchemaGeneratorHooksProvider {
    @Bean
    override fun hooks(): SchemaGeneratorHooks = CustomSchemaGeneratorHooks(KotlinDirectiveWiringFactory())
}