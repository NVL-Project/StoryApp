package com.nvl.storyapp.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_keys")
data class ServerKeys (
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)