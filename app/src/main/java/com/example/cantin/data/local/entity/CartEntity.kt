package com.example.cantin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cantin.model.MenuCategory

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val menuId: Int,
    val quantity: Int,
    
    val name: String,
    val price: Int,
    val imageResId: Int,
    val imageUrl: String?,
    val category: String, 
    val description: String
)
