package com.nicovogelaar.playground.persistence

import org.jetbrains.exposed.sql.Table

object StorePetTable : Table() {
    val storeId = uuid("store_id")
    val petId = uuid("pet_id")

    override val primaryKey = PrimaryKey(storeId, petId)
}
