package com.example.cantin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cantin.component.MyCantenBottomBar
import com.example.cantin.navigation.Destinations
import com.example.cantin.navigation.NavGraph
import com.example.cantin.ui.theme.CantinTheme
import com.example.cantin.viewmodel.CartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CantinTheme { CantinApp() } }
    }
}

@Composable
fun CantinApp() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Destinations.HOME_ROUTE,
        Destinations.FAVORITE_ROUTE,
        Destinations.ORDERS_ROUTE,
        Destinations.PROFILE_ROUTE
    )
    
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = { 
            if (showBottomBar) {
                MyCantenBottomBar(navController) 
            }
        }
    ) { padding ->
        NavGraph(navController, cartViewModel, padding)
    }
}
