package com.example.householdlocator.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseholdItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HouseholdItem): Long

    @Update
    suspend fun update(item: HouseholdItem)

    @Query("SELECT * FROM household_items ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): Flow<List<HouseholdItem>>

    @Query("SELECT * FROM household_items WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun getByName(name: String): HouseholdItem?

    @Query("SELECT * FROM household_items WHERE location LIKE '%' || :location || '%' COLLATE NOCASE ORDER BY name COLLATE NOCASE ASC")
    suspend fun findByLocationOnce(location: String): List<HouseholdItem>
}
