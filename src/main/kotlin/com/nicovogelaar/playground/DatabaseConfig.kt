package com.nicovogelaar.playground

import com.nicovogelaar.playground.persistence.PetTable
import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatabaseConfig {
    @Autowired
    private lateinit var dataSource: DataSource

    @PostConstruct
    fun init() {
        connect()
        createTables()
    }

    fun connect() {
        Database.connect(dataSource)
    }

    fun createTables() {
        transaction {
            SchemaUtils.create(PetTable)
        }
    }
}
