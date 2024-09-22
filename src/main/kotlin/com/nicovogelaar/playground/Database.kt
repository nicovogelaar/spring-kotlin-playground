package com.nicovogelaar.playground

import com.nicovogelaar.playground.persistence.PetTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    fun connect() {
        Database.connect(
            url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            driver = "org.h2.Driver",
            user = "sa",
            password = "",
        )
    }

    fun createTables() {
        transaction {
            SchemaUtils.create(PetTable)
        }
    }
}
