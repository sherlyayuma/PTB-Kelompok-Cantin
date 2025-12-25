package com.example.cantin.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("name") val name: String,
    @SerializedName("phone", alternate = ["phone_number", "phoneNumber"]) val phoneNumber: String,
    @SerializedName("email") val email: String
)
