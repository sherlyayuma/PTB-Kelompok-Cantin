package com.example.cantin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val menuId: Int,
    val rating: Int,
    val reviewText: String,
    val imageUri: String?, 
    val videoUri: String?,
    val userName: String,
    val timestamp: Long
)
