package com.example.householdlocator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HouseholdItem::class], version = 1, exportSchema = false)
abstract class HouseholdItemDatabase : RoomDatabase() {
    abstract fun householdItemDao(): HouseholdItemDao

    companion object {
        @Volatile
        private var INSTANCE: HouseholdItemDatabase? = null

        fun getInstance(context: Context): HouseholdItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HouseholdItemDatabase::class.java,
                    "household_item_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
