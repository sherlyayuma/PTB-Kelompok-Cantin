package com.example.cantin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantin.data.DUMMY_MENU_ITEMS
import com.example.cantin.data.local.AppDatabase
import com.example.cantin.data.local.entity.CartEntity
import com.example.cantin.model.CartItem
import com.example.cantin.model.MenuItem
import com.example.cantin.model.Order
import com.example.cantin.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartDao = AppDatabase.getDatabase(application).cartDao()
    private val favoriteDao = AppDatabase.getDatabase(application).favoriteDao()
    private val sessionManager = com.example.cantin.utils.SessionManager(application)

    init {
        loadUserProfile()
        loadOrderHistory()
    }

    private val _rawMenuItems = MutableStateFlow(DUMMY_MENU_ITEMS)
    
    val menuItems: StateFlow<List<MenuItem>> = kotlinx.coroutines.flow.combine(
        _rawMenuItems,
        favoriteDao.getAllFavorites()
    ) { items, favorites ->
        val favoriteIds = favorites.map { it.menuId }.toSet()
        items.map { item ->
            item.copy(isFavorite = favoriteIds.contains(item.id))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DUMMY_MENU_ITEMS)

    val cartItems: StateFlow<List<CartItem>> = cartDao.getAllCartItems()
        .map { entities ->
            entities.map { entity ->
                CartItem(
                    menuItem = MenuItem(
                        id = entity.menuId,
                        name = entity.name,
                        price = entity.price,
                        imageResId = entity.imageResId,
                        imageUrl = entity.imageUrl,
                        category = try {
                            com.example.cantin.model.MenuCategory.valueOf(entity.category)
                        } catch (e: Exception) {
                            com.example.cantin.model.MenuCategory.MAKANAN
                        },
                        description = entity.description
                    ),
                    quantity = entity.quantity
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = sessionManager.getUserName() ?: "User", 
            phoneNumber = "",
            email = ""
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            val currentFavorites = favoriteDao.getAllFavorites()
            val item = menuItems.value.find { it.id == id }
            if (item != null) {
                if (item.isFavorite) {
                    favoriteDao.deleteFavoriteById(id)
                } else {
                    favoriteDao.insertFavorite(com.example.cantin.data.local.entity.FavoriteEntity(id))
                }
            }
        }
    }

    fun addToCart(item: MenuItem) {
        viewModelScope.launch {
            val existingItem = cartDao.getCartItemByMenuId(item.id)
            if (existingItem != null) {
                cartDao.updateCartItem(existingItem.copy(quantity = existingItem.quantity + 1))
            } else {
                cartDao.insertCartItem(
                    CartEntity(
                        menuId = item.id,
                        quantity = 1,
                        name = item.name,
                        price = item.price,
                        imageResId = item.imageResId,
                        imageUrl = item.imageUrl,
                        category = item.category.name,
                        description = item.description
                    )
                )
            }
            scheduleCartReminder()
        }
    }

    private var cartReminderJob: kotlinx.coroutines.Job? = null

    private fun scheduleCartReminder() {
        if (cartReminderJob?.isActive == true) return 

        cartReminderJob = viewModelScope.launch {
            kotlinx.coroutines.delay(60_000L)
            
            if (cartItems.value.isNotEmpty()) {
                com.example.cantin.utils.NotificationUtils.showNotification(
                    context = getApplication(),
                    title = "Jangan Lupa Checkout!",
                    message = "Ada makanan enak di keranjangmu nih. Yuk pesan sekarang sebelum kehabisan! 🍜"
                )
            }
        }
    }

    fun increaseQuantity(id: Int) {
        viewModelScope.launch {
            val existingItem = cartDao.getCartItemByMenuId(id)
            existingItem?.let {
                cartDao.updateCartItem(it.copy(quantity = it.quantity + 1))
            }
        }
    }

    fun decreaseQuantity(id: Int) {
        viewModelScope.launch {
            val existingItem = cartDao.getCartItemByMenuId(id)
            existingItem?.let {
                if (it.quantity > 1) {
                    cartDao.updateCartItem(it.copy(quantity = it.quantity - 1))
                } else {
                    cartDao.deleteCartItem(it)
                }
            }
        }
    }

    val totalItemCount: StateFlow<Int> = cartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPrice: StateFlow<Int> = cartItems.map { items ->
        items.sumOf { it.quantity * it.menuItem.price }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _selectedTable = MutableStateFlow<String?>(null)
    val selectedTable: StateFlow<String?> = _selectedTable

    private val _occupiedTables = MutableStateFlow<Set<String>>(setOf("A2", "D7"))
    val occupiedTables: StateFlow<Set<String>> = _occupiedTables

    fun selectTable(tableId: String) {
        if (!_occupiedTables.value.contains(tableId)) {
            if (_selectedTable.value == tableId) {
                _selectedTable.value = null
            } else {
                _selectedTable.value = tableId
            }
        }
    }

    private val _isTakeAway = MutableStateFlow(false)
    val isTakeAway: StateFlow<Boolean> = _isTakeAway

    fun toggleTakeAway() {
        _isTakeAway.update { !it }
    }

    private val _myReviews = MutableStateFlow<List<com.example.cantin.model.Review>>(emptyList())
    val myReviews: StateFlow<List<com.example.cantin.model.Review>> = _myReviews

    private val _activeOrders = MutableStateFlow<List<Order>>(emptyList())
    val activeOrders: StateFlow<List<Order>> = _activeOrders

    private val _completedOrders = MutableStateFlow<List<CartItem>>(emptyList())
    val completedOrders: StateFlow<List<CartItem>> = kotlinx.coroutines.flow.combine(
        _completedOrders,
        myReviews
    ) { orders, reviews ->
        val reviewedMenuIds = reviews.map { it.menuId }.toSet()
        orders.filter { it.menuItem.id !in reviewedMenuIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun confirmOrder() {
        val currentItems = cartItems.value
        val isTakeAwayState = _isTakeAway.value
        val userId = sessionManager.getUserId()

        if (userId == -1) {
        }

        if (currentItems.isNotEmpty()) {
            val newOrder = Order(
                userId = if (userId != -1) userId else 0,
                items = currentItems,
                timestamp = System.currentTimeMillis(),
                isTakeAway = isTakeAwayState
            )
            

            _activeOrders.update { it + newOrder }

            viewModelScope.launch {

                cartDao.clearCart()
                cartReminderJob?.cancel()
                
                try {

                    com.example.cantin.network.RetrofitClient.instance.placeOrder(newOrder)
                    

                     _selectedTable.value?.let { tableId ->
                        _occupiedTables.update { it + tableId }
                        _selectedTable.value = null
                    }
                    
                } catch (e: Exception) {
                    e.printStackTrace()

                }
            }
        }
    }

    fun markOrderAsComplete(orderId: String) {
        val currentList = _activeOrders.value
        val orderToComplete = currentList.find { it.id == orderId }

        if (orderToComplete != null) {
            _completedOrders.update { it + orderToComplete.items }

            _activeOrders.update { list -> list.filter { it.id != orderId } }
        }
    }

    fun cancelOrder(orderId: String) {

        _activeOrders.update { list -> list.filter { it.id != orderId } }
    }

    fun updateUserProfile(name: String, phone: String, email: String) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                val idAsString = if (userId != -1) userId.toString() else "1"

                _userProfile.value = com.example.cantin.model.UserProfile(
                    name = name,
                    email = email,
                    phoneNumber = phone
                )
                
                sessionManager.saveUserSession(userId, name)

                val response = com.example.cantin.network.RetrofitClient.instance.updateProfile(
                    userId = idAsString,
                    name = name,
                    phone = phone,
                    email = email
                )
                
                android.util.Log.d("CartViewModel", "Update Response: $response")

                loadUserProfile()
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                android.util.Log.d("CartViewModel", "Loading Profile for UserID: $userId")
                
                if (userId != -1) {
                    val profile = com.example.cantin.network.RetrofitClient.instance.getUserProfile(userId)
                    android.util.Log.d("CartViewModel", "Profile Loaded: $profile")
                    _userProfile.value = profile
                } else {
                    android.util.Log.d("CartViewModel", "No User Logged In")
                }
            } catch (e: Exception) {
                android.util.Log.e("CartViewModel", "Error Loading Profile", e)
                e.printStackTrace()
            }
        }
    }


    private val _reviews =
        MutableStateFlow<Map<Int, List<com.example.cantin.model.Review>>>(emptyMap())
    val reviews: StateFlow<Map<Int, List<com.example.cantin.model.Review>>> = _reviews



    fun loadMyReviews() {
        viewModelScope.launch {
            try {
                val userId = 1 
                val result = com.example.cantin.network.RetrofitClient.instance.getUserReviews(userId)
                _myReviews.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateReview(review: com.example.cantin.model.Review) {
         viewModelScope.launch {
            try {
                _myReviews.update { currentList ->
                    currentList.map { if (it.id == review.id) review else it }
                }
                
                _reviews.update { currentMap ->
                    val menuId = review.menuId
                    val currentList = currentMap[menuId] ?: emptyList()
                    val updatedList = currentList.map { if (it.id == review.id) review else it }
                    currentMap + (menuId to updatedList)
                }

                val idPart = review.id.toRequestBody(okhttp3.MultipartBody.FORM)
                val ratingPart = review.rating.toString().toRequestBody(okhttp3.MultipartBody.FORM)
                val reviewTextPart = review.reviewText.toRequestBody(okhttp3.MultipartBody.FORM)

                var imagePart: okhttp3.MultipartBody.Part? = null
                review.imageUri?.let { uri ->
                     val contentResolver = getApplication<Application>().contentResolver
                     val type = contentResolver.getType(uri) ?: "image/jpeg"
                     val mediaType = type.toMediaTypeOrNull()
                     val tempFile = java.io.File.createTempFile("update_img", ".jpg", getApplication<Application>().cacheDir)
                     contentResolver.openInputStream(uri)?.use { input -> java.io.FileOutputStream(tempFile).use { output -> input.copyTo(output) } }
                     if (tempFile.exists()) {
                         val requestFile = tempFile.asRequestBody(mediaType)
                         imagePart = okhttp3.MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
                     }
                }

                var videoPart: okhttp3.MultipartBody.Part? = null
                review.videoUri?.let { uri ->
                     val contentResolver = getApplication<Application>().contentResolver
                     val type = contentResolver.getType(uri) ?: "video/mp4"
                     val mediaType = type.toMediaTypeOrNull()
                     val tempFile = java.io.File.createTempFile("update_vid", ".mp4", getApplication<Application>().cacheDir)
                     contentResolver.openInputStream(uri)?.use { input -> java.io.FileOutputStream(tempFile).use { output -> input.copyTo(output) } }
                     if (tempFile.exists()) {
                         val requestFile = tempFile.asRequestBody(mediaType)
                         videoPart = okhttp3.MultipartBody.Part.createFormData("video", tempFile.name, requestFile)
                     }
                }

                com.example.cantin.network.RetrofitClient.instance.updateReview(
                    idPart, ratingPart, reviewTextPart, imagePart, videoPart
                )
                
                loadMyReviews()

                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun submitReview(review: com.example.cantin.model.Review) {
        viewModelScope.launch {
            try {
                val idPart = review.id.toRequestBody(okhttp3.MultipartBody.FORM)
                val menuIdPart = review.menuId.toString().toRequestBody(okhttp3.MultipartBody.FORM)
                val ratingPart = review.rating.toString().toRequestBody(okhttp3.MultipartBody.FORM)
                val reviewTextPart = review.reviewText.toRequestBody(okhttp3.MultipartBody.FORM)
                val userNamePart = review.userName.toRequestBody(okhttp3.MultipartBody.FORM)

                var imagePart: okhttp3.MultipartBody.Part? = null
                review.imageUri?.let { uri ->
                    val contentResolver = getApplication<Application>().contentResolver
                    val type = contentResolver.getType(uri) ?: "image/jpeg"
                    val mediaType = type.toMediaTypeOrNull()
                    val tempFile = java.io.File.createTempFile("upload_image", ".jpg", getApplication<Application>().cacheDir)
                    contentResolver.openInputStream(uri)?.use { input -> java.io.FileOutputStream(tempFile).use { output -> input.copyTo(output) } }
                    if (tempFile.exists()) {
                        val requestFile = tempFile.asRequestBody(mediaType)
                        imagePart = okhttp3.MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
                    }
                }

                var videoPart: okhttp3.MultipartBody.Part? = null
                review.videoUri?.let { uri ->
                    val contentResolver = getApplication<Application>().contentResolver
                    val type = contentResolver.getType(uri) ?: "video/mp4"
                    val mediaType = type.toMediaTypeOrNull()
                    val tempFile = java.io.File.createTempFile("upload_video", ".mp4", getApplication<Application>().cacheDir)
                    contentResolver.openInputStream(uri)?.use { input -> java.io.FileOutputStream(tempFile).use { output -> input.copyTo(output) } }
                    if (tempFile.exists()) {
                        val requestFile = tempFile.asRequestBody(mediaType)
                        videoPart = okhttp3.MultipartBody.Part.createFormData("video", tempFile.name, requestFile)
                    }
                }

                com.example.cantin.network.RetrofitClient.instance.submitReview(
                    idPart, menuIdPart, ratingPart, reviewTextPart, userNamePart, imagePart, videoPart
                )

                _reviews.update { currentReviews ->
                    val menuId = review.menuId
                    val existingReviews = currentReviews[menuId] ?: emptyList()
                    currentReviews + (menuId to (existingReviews + review))
                }
                
                _myReviews.update { it + review }
                loadMyReviews() 
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadReviews(menuId: Int) {
        viewModelScope.launch {
            try {
                val fetchedReviews = com.example.cantin.network.RetrofitClient.instance.getReviews(menuId)
                
                val myLocalReviews = _myReviews.value.associateBy { it.id }
                
                val mergedReviews = fetchedReviews.map { review ->
                    myLocalReviews[review.id] ?: review
                }
                
                _reviews.update { current ->
                    current + (menuId to mergedReviews)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadOrderHistory() {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId != -1) {
                    val orders =
                        com.example.cantin.network.RetrofitClient.instance.getOrderHistory(userId)

                    val (completed, active) = orders.partition { it.isCompleted }

                    _activeOrders.value = active
                    _completedOrders.value = completed.flatMap { it.items }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
