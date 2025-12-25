package com.example.cantin.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cantin.screen.*
import com.example.cantin.viewmodel.CartViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    innerPadding: androidx.compose.foundation.layout.PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN_ROUTE,
        modifier = Modifier.padding(innerPadding)
    ) {

        composable(Destinations.LOGIN_ROUTE) {
            LoginScreen(
                cartViewModel = cartViewModel,
                onLoginSuccess = {
                    navController.navigate(Destinations.HOME_ROUTE) {
                        popUpTo(Destinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.HOME_ROUTE) {
            HomeScreen(
                cartViewModel = cartViewModel,
                onProfileClick = { navController.navigate(Destinations.PROFILE_ROUTE) },
                onCartClick = { navController.navigate(Destinations.CART_ROUTE) },
                onProductClick = { menuId ->
                    navController.navigate(Destinations.createDetailMenuRoute(menuId))
                }
            )
        }

        composable(Destinations.FAVORITE_ROUTE) {
            FavoritScreen(cartViewModel, navController)
        }

        composable(Destinations.CART_ROUTE) {
            KeranjangScreen(
                cartViewModel = cartViewModel, 
                onBackClick = { navController.popBackStack() },
                onCheckout = { navController.navigate(Destinations.CONFIRMATION_ROUTE) }
            )
        }

        composable(Destinations.PROFILE_ROUTE) {
            ProfilkuScreen(cartViewModel, navController)
        }
        
        composable(Destinations.EDIT_PROFILE_ROUTE) {
            EditProfileScreen(cartViewModel, navController)
        }

        composable(Destinations.CONFIRMATION_ROUTE) {
            KonfirmasiPesananScreen(cartViewModel, navController)
        }

        composable(Destinations.SEAT_SELECTION_ROUTE) {
            PilihMejaScreen(cartViewModel, navController)
        }

        composable(Destinations.PAYMENT_METHOD_ROUTE) {
            PembayaranScreen(cartViewModel, navController)
        }

        composable(Destinations.PAYMENT_CODE_ROUTE) {
            KodePembayaranScreen(cartViewModel, navController)
        }

        composable(Destinations.ORDER_SUCCESS_ROUTE) {
            PesananDiterimaScreen(navController)
        }

        composable(Destinations.ORDERS_ROUTE) {
            PesananSayaScreen(cartViewModel, navController, initialTab = 1) 
        }

        composable(Destinations.ACTIVITY_ROUTE) {
            AktivitaskuScreen(cartViewModel, navController)
        }

        composable(Destinations.RATE_PRODUCT_ROUTE) {
            PesananSayaScreen(cartViewModel, navController, initialTab = 0) 
        }

        composable(
            route = Destinations.DETAIL_MENU_ROUTE,
            arguments = listOf(navArgument("menuId") { type = NavType.IntType })
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getInt("menuId")
            if (menuId != null) {
                DetailMenuScreen(
                    menuId = menuId,
                    cartViewModel = cartViewModel,
                    navController = navController
                )
            }
        }


        composable(
            route = Destinations.UPDATE_REVIEW_ROUTE,
            arguments = listOf(navArgument("reviewId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId")
            if (reviewId != null) {
                UpdateReviewScreen(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    reviewId = reviewId
                )
            }
        }
    }
}
