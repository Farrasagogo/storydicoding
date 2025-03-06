package com.taqiyuddin.storyappdicoding.submissionakhir.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.dao.ContentDao
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.keys.PagingKeys
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.dao.PagingKeysDao
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory

@Database(
    entities = [DetailedStory::class, PagingKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ContentDatabase : RoomDatabase() {

    abstract fun contentDao(): ContentDao
    abstract fun pagingKeysDao(): PagingKeysDao

    companion object {
        @Volatile
        private var databaseInstance: ContentDatabase? = null

        fun getDatabase(context: Context): ContentDatabase {
            return databaseInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContentDatabase::class.java,
                    "content_paging_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                databaseInstance = instance
                instance
            }
        }
    }
}