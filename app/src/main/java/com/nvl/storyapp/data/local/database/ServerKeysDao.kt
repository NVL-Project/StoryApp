package com.nvl.storyapp.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServerKeysDao : StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ServerKeys>)

    @Query("SELECT * FROM server_keys WHERE id = :id")
    suspend fun getServerKeysId(id: String): ServerKeys?

    @Query("DELETE FROM server_keys")
    suspend fun deleteServerKeys()
}