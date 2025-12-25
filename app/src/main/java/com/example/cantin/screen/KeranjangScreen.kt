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
import androidx.compose.ui.unit.dp
import com.example.cantin.component.CartListItem
import com.example.cantin.component.CheckoutSection
import com.example.cantin.viewmodel.CartViewModel
import com.example.cantin.component.*
import com.example.cantin.ui.theme.PrimaryOrangeColor
import androidx.compose.runtime.collectAsState




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeranjangScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onCheckout: () -> Unit 
) {
    val cartItems by cartViewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Keranjangmu masih kosong")
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems, key = { it.menuItem.id }) { cartItem ->
                        CartListItem(
                            item = cartItem.menuItem,
                            quantity = cartItem.quantity,
                            onIncrease = { cartViewModel.increaseQuantity(cartItem.menuItem.id) },
                            onDecrease = { cartViewModel.decreaseQuantity(cartItem.menuItem.id) }
                        )
                    }
                }
                val totalPrice by cartViewModel.totalPrice.collectAsState()
                CheckoutSection(totalPrice = totalPrice, onCheckoutClick = onCheckout)
            }
        }
    }
}
