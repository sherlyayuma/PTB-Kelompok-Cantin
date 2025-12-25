@Composable
fun RatingItemCard(item: CartItem) {
    var rating by remember { mutableIntStateOf(3) }
    var reviewText by remember { mutableStateOf("") }

    Card(...) {
        Column {
             // Product Info Row (Image, Name, Price)
             // Star Rating Row (Cycle 5 stars, clickable)
             // TextField (Ulasan)
             // Photo/Video Buttons (Dummy)
             // Kirim Button
        }
    }
}
