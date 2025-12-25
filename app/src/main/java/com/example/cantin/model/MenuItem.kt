package com.example.cantin.model

import com.google.gson.annotations.SerializedName

enum class MenuCategory {
    MAKANAN,
    MINUMAN
}

data class MenuItem(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int,
    val imageResId: Int = 0, 
    @SerializedName("image_url") val imageUrl: String? = null, 
    @SerializedName("category") val category: MenuCategory = MenuCategory.MAKANAN,
    @SerializedName("description") val description: String = "", 
    @SerializedName("is_favorite") val isFavorite: Boolean = false
) {

}


