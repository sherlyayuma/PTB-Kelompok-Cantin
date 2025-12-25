package com.example.cantin.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cantin.model.MenuItem
import com.example.cantin.ui.theme.PrimaryOrangeColor
import com.example.cantin.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonfirmasiPesananScreen(
    cartViewModel: CartViewModel,
    navController: NavController
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalItemCount by cartViewModel.totalItemCount.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()

    val selectedTable by cartViewModel.selectedTable.collectAsState()
    val isTakeAway by cartViewModel.isTakeAway.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Konfirmasi Pesanan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryOrangeColor
                )
            )
        },
        bottomBar = {
            Surface(
                color = PrimaryOrangeColor,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                Color.White.copy(alpha = 0.5f),
                                RoundedCornerShape(2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Jumlah Item", color = Color.White, fontSize = 14.sp)
                            Text("Total Pesanan", color = Color.White, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "$totalItemCount Item",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Rp $totalPrice",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate(
                                com.example.cantin.navigation.Destinations.PAYMENT_METHOD_ROUTE
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "Pesan Sekarang",
                            color = PrimaryOrangeColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9F9F9)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /** ---------------- INFORMASI PESANAN ---------------- **/
            item {
                Text(
                    "Informasi Pesanan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                val isTableOptionEnabled = !isTakeAway

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isTableOptionEnabled)
                                Modifier.clickable {
                                    navController.navigate(
                                        com.example.cantin.navigation.Destinations.SEAT_SELECTION_ROUTE
                                    )
                                }
                            else Modifier
                        ),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (isTableOptionEnabled) Color(0xFFF0F0F0)
                            else Color.LightGray.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text =
                                if (selectedTable.isNullOrEmpty())
                                    "Pilih Nomor Meja"
                                else
                                    "Meja $selectedTable",
                            color =
                                if (!isTableOptionEnabled) Color.Gray
                                else if (selectedTable.isNullOrEmpty())
                                    PrimaryOrangeColor
                                else Color.Black,
                            fontWeight =
                                if (!selectedTable.isNullOrEmpty())
                                    FontWeight.Bold
                                else FontWeight.Normal
                        )
                    }
                }
            }

            /** ---------------- TAKE AWAY ---------------- **/
            item {
                val isTakeAwayOptionEnabled = selectedTable.isNullOrEmpty()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isTakeAwayOptionEnabled)
                                Modifier.clickable {
                                    cartViewModel.toggleTakeAway()
                                }
                            else Modifier
                        ),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            !isTakeAwayOptionEnabled ->
                                Color.LightGray.copy(alpha = 0.5f)
                            isTakeAway ->
                                PrimaryOrangeColor
                            else ->
                                Color(0xFFF0F0F0)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Dibungkus",
                            color = when {
                                !isTakeAwayOptionEnabled -> Color.Gray
                                isTakeAway -> Color.White
                                else -> Color.Black
                            },
                            fontWeight = FontWeight.Bold
                        )

                        if (isTakeAway) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            /** ---------------- DAFTAR PESANAN ---------------- **/
            item {
                Text(
                    "Daftar Pesanan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            items(cartItems) { item ->
                ConfirmationItemCard(
                    menuItem = item.menuItem,
                    quantity = item.quantity,
                    onIncrease = {
                        cartViewModel.increaseQuantity(item.menuItem.id)
                    },
                    onDecrease = {
                        cartViewModel.decreaseQuantity(item.menuItem.id)
                    }
                )
            }
        }
    }
}

@Composable
fun ConfirmationItemCard(
    menuItem: MenuItem,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            Image(
                painter = painterResource(menuItem.imageResId),
                contentDescription = menuItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        menuItem.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Rp ${menuItem.price}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                PrimaryOrangeColor,
                                RoundedCornerShape(
                                    topStart = 4.dp,
                                    bottomStart = 4.dp
                                )
                            )
                            .clickable(onClick = onDecrease),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .background(PrimaryOrangeColor)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            quantity.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                PrimaryOrangeColor,
                                RoundedCornerShape(
                                    topEnd = 4.dp,
                                    bottomEnd = 4.dp
                                )
                            )
                            .clickable(onClick = onIncrease),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
