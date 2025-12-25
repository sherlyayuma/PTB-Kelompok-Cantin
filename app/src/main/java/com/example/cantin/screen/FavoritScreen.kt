package com.example.cantin.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.cantin.viewmodel.CartViewModel
import com.example.cantin.component.MenuItemCard
import com.example.cantin.ui.theme.PrimaryOrangeColor
import androidx.compose.runtime.collectAsState



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritScreen(
    cartViewModel: CartViewModel,
    navController: NavController
) {
    val menuItems by cartViewModel.menuItems.collectAsState()
    val favoriteItems = menuItems.filter { it.isFavorite }
    val cartItems by cartViewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorit") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryOrangeColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (favoriteItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada item favorit")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(favoriteItems, key = { it.id }) { item ->
                    val cartItem = cartItems.find { it.menuItem.id == item.id }
                    MenuItemCard(
                        item = item,
                        quantity = cartItem?.quantity ?: 0,
                        isFavorite = item.isFavorite,
                        onAddToCart = { cartViewModel.addToCart(item) },
                        onIncrease = { cartViewModel.increaseQuantity(item.id) },
                        onDecrease = { cartViewModel.decreaseQuantity(item.id) },
                        onToggleFavorite = { cartViewModel.toggleFavorite(item.id) }
                    )
                }
            }
        }
    }
}
