package com.taqiyuddin.storyappdicoding.submissionakhir.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.keys.PagingKeys

@Dao
interface PagingKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePagingKeys(keys: List<PagingKeys>)

    @Query("DELETE FROM paging_keys")
    suspend fun clearPagingKeys()

    @Query("SELECT * FROM paging_keys WHERE itemId = :id")
    suspend fun getPagingKey(id: String): PagingKeys?
}