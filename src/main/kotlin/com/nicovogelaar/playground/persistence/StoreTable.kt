package com.nicovogelaar.playground.persistence

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object StoreTable : Table() {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 100)
    val location = varchar("location", 100)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}