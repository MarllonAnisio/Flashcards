package com.ifpb.marllon_anisio.flashcards.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DeckEntity::class, FlashcardEntity::class], version = 2)
abstract class FlashcardDatabase : RoomDatabase() {
    abstract fun dao(): FlashcardDao

    companion object {
        @Volatile
        private var INSTANCE: FlashcardDatabase? = null

        fun getDatabase(context: Context): FlashcardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashcardDatabase::class.java,
                    "flashcard_database"
                )
                .fallbackToDestructiveMigration() // Facilitate dev phase version 2 migration
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
