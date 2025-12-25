package com.example.cantin.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("menu_id") val menuId: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("review_text") val reviewText: String,
    @SerializedName("image_url") val imageUrl: String? = null, 
    val imageUri: Uri? = null, 
    val videoUri: Uri? = null,
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("user_name") val userName: String = "User"
)
