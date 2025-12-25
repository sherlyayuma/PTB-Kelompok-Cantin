package com.example.cantin.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cantin.navigation.Destinations
import com.example.cantin.ui.theme.PrimaryOrangeColor

@Composable
fun MyCantenBottomBar(navController: NavController) { 
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {

        BottomItem(
            label = "Home",
            icon = Icons.Default.Home,
            selected = currentRoute == Destinations.HOME_ROUTE
        ) {
            navController.navigate(Destinations.HOME_ROUTE)
        }

        BottomItem(
            label = "Favorit",
            icon = Icons.Default.Favorite,
            selected = currentRoute == Destinations.FAVORITE_ROUTE
        ) {
            navController.navigate(Destinations.FAVORITE_ROUTE)
        }

        BottomItem(
            label = "Keranjang",
            icon = Icons.Default.ShoppingCart,
            selected = currentRoute == Destinations.CART_ROUTE
        ) {
            navController.navigate(Destinations.CART_ROUTE)
        }

        BottomItem(
            label = "Pesanan",
            icon = Icons.Default.Receipt,
            selected = currentRoute == Destinations.ORDERS_ROUTE
        ) {
            navController.navigate(Destinations.ORDERS_ROUTE)
        }

        BottomItem(
            label = "Profil",
            icon = Icons.Default.Person,
            selected = currentRoute == Destinations.PROFILE_ROUTE
        ) {
            navController.navigate(Destinations.PROFILE_ROUTE)
        }
    }
} 

@Composable
private fun RowScope.BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) PrimaryOrangeColor else Color.Gray
            )
        },
        label = { Text(label) }
    )
}
