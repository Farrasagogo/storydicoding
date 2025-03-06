package com.taqiyuddin.storyappdicoding.submissionakhir.data.local.keys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paging_keys")
data class PagingKeys(
    @PrimaryKey
    val itemId: String,
    val previousPageKey: Int?,
    val nextPageKey: Int?
)
