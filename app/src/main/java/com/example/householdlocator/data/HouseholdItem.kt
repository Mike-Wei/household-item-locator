package com.example.householdlocator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "household_items")
data class HouseholdItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val location: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
