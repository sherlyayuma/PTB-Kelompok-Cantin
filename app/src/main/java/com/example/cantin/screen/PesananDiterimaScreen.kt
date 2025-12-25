package com.example.cantin.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cantin.navigation.Destinations
import com.example.cantin.ui.theme.PrimaryOrangeColor

@Composable
fun PesananDiterimaScreen(
    navController: NavController
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .background(color = Color(0xFFFFC107), shape = CircleShape) 
            ) {
                 Icon(
                     imageVector = Icons.Default.Check,
                     contentDescription = null,
                     tint = Color.Green, 
                     modifier = Modifier.size(80.dp)
                 )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Pesanan Diterima",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8D6E63) 
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Pesanan mu sudah diterima dan akan segera dipersiapkan, silahkan menunggu dengan santuy ya..",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(
                onClick = { 
                    navController.navigate(Destinations.ORDERS_ROUTE)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(4.dp),
                 colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text("Detail Pesanan")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    navController.navigate(Destinations.HOME_ROUTE) {
                        popUpTo(Destinations.HOME_ROUTE) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeColor)
            ) {
                Text("Pesan Lainnya", color = Color.White)
            }
        }
    }
}
