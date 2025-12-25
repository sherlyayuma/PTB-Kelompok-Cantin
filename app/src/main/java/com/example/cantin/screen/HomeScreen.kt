package com.example.cantin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cantin.NotificationHelper
import com.example.cantin.component.*
import com.example.cantin.model.MenuCategory
import com.example.cantin.viewmodel.CartViewModel
import androidx.compose.runtime.collectAsState


@Composable
fun HomeScreen(
    cartViewModel: CartViewModel,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    onProductClick: (Int) -> Unit 
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val menuItems by cartViewModel.menuItems.collectAsState()
    val userProfile by cartViewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf(MenuCategory.MAKANAN) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredMenu = menuItems.filter { item ->
        item.category == selectedCategory &&
                item.name.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = if (cartItems.isNotEmpty()) 100.dp else 16.dp 
            )
        ) {

            item {
                HeaderHome(
                    name = userProfile.name,
                    onProfileClick = onProfileClick
                )
            }

            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            item {
                KategoriChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            items(filteredMenu, key = { it.id }) { item ->
                val cartItem = cartItems.find { it.menuItem.id == item.id }

                MenuItemCard(
                    item = item,
                    quantity = cartItem?.quantity ?: 0,
                    isFavorite = item.isFavorite,
                    onAddToCart = { cartViewModel.addToCart(item) },
                    onIncrease = { cartViewModel.increaseQuantity(item.id) },
                    onDecrease = { cartViewModel.decreaseQuantity(item.id) },
                    onToggleFavorite = {
                        cartViewModel.toggleFavorite(item.id)
                        if (!item.isFavorite) {
                            NotificationHelper.showFavoriteNotification(context)
                        }
                    },
                    onItemClick = {
                        onProductClick(item.id)
                    }
                )
            }
        }
        
        if (cartItems.isNotEmpty()) {
            val totalCount = cartItems.sumOf { it.quantity }
            val totalPrice = cartItems.sumOf { it.quantity * it.menuItem.price }
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp) 
            ) {
                FloatingCartBar(
                    itemCount = totalCount,
                    totalPrice = totalPrice,
                    onClick = onCartClick
                )
            }
        }
    }
}


