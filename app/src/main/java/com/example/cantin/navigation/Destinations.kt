package com.example.cantin.navigation


object Destinations {
    const val HOME_ROUTE = "home"
    const val LOGIN_ROUTE = "login"
    const val PROFILE_ROUTE = "profile"
    const val FAVORITE_ROUTE = "favorite"
    const val ORDERS_ROUTE = "orders"
    const val EDIT_PROFILE_ROUTE = "edit_profile"
    const val ACTIVITY_ROUTE = "activity"
    const val RATE_PRODUCT_ROUTE = "rate_product"
    const val CONFIRMATION_ROUTE = "confirmation"
    const val SEAT_SELECTION_ROUTE = "seat_selection"
    const val PAYMENT_METHOD_ROUTE = "payment_method"
    const val PAYMENT_CODE_ROUTE = "payment_code"
    const val CART_ROUTE = "cart"
    const val ORDER_SUCCESS_ROUTE = "order_success"
    const val DETAIL_MENU_ROUTE = "detail_menu/{menuId}"
    
    fun createDetailMenuRoute(menuId: Int) = "detail_menu/$menuId"

    const val UPDATE_REVIEW_ROUTE = "update_review/{reviewId}"
    fun createUpdateReviewRoute(reviewId: String) = "update_review/$reviewId"
}
