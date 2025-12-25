package com.example.cantin.network

import com.example.cantin.model.MenuItem
import com.example.cantin.model.Order
import com.example.cantin.model.LoginResponse
import com.example.cantin.model.LoginRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("get_menu.php")
    suspend fun getMenu(): List<MenuItem>

    @POST("place_order.php")
    suspend fun placeOrder(@Body order: Order): Map<String, String>

    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @retrofit2.http.FormUrlEncoded
    @POST("update_profile.php")
    suspend fun updateProfile(
        @retrofit2.http.Field("user_id") userId: String,
        @retrofit2.http.Field("name") name: String,
        @retrofit2.http.Field("phone") phone: String,
        @retrofit2.http.Field("email") email: String
    ): Map<String, String>

    @retrofit2.http.Multipart
    @POST("submit_review.php")
    suspend fun submitReview(
        @retrofit2.http.Part("id") id: RequestBody,
        @retrofit2.http.Part("menu_id") menuId: RequestBody,
        @retrofit2.http.Part("rating") rating: RequestBody,
        @retrofit2.http.Part("review_text") reviewText: RequestBody,
        @retrofit2.http.Part("user_name") userName: RequestBody,
        @retrofit2.http.Part image: MultipartBody.Part?,
        @retrofit2.http.Part video: MultipartBody.Part?
    ): Map<String, String>
    
    @GET("get_history.php")
    suspend fun getOrderHistory(@Query("user_id") userId: Int): List<Order>

    @GET("get_reviews.php")
    suspend fun getReviews(@Query("menu_id") menuId: Int): List<com.example.cantin.model.Review>

    @GET("get_profile.php")
    suspend fun getUserProfile(@Query("user_id") userId: Int): com.example.cantin.model.UserProfile

    @GET("get_user_reviews.php")
    suspend fun getUserReviews(@Query("user_id") userId: Int): List<com.example.cantin.model.Review>

    @retrofit2.http.Multipart
    @POST("update_review.php")
    suspend fun updateReview(
        @retrofit2.http.Part("id") id: RequestBody,
        @retrofit2.http.Part("rating") rating: RequestBody,
        @retrofit2.http.Part("review_text") reviewText: RequestBody,
        @retrofit2.http.Part image: MultipartBody.Part?,
        @retrofit2.http.Part video: MultipartBody.Part?
    ): Map<String, String>
}
