package com.example.cantin.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import com.example.cantin.navigation.Destinations
import com.example.cantin.ui.theme.PrimaryOrangeColor
import com.example.cantin.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KodePembayaranScreen(
    cartViewModel: CartViewModel,
    navController: NavController
) {
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val context = LocalContext.current
    
    val notificationPermissionState = remember { mutableStateOf(false) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            notificationPermissionState.value = isGranted
        }
    )

    var hasInteracted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(60 * 1000L) 
        if (!hasInteracted) {
            com.example.cantin.utils.NotificationUtils.showNotification(
                context,
                "Halo",
                "Pemesanan anda menunggu untuk dibayar. Segera selesaikan pembayaran transaksi anda sebelum masa berlaku pembayaran habis!"
            )
        }
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
             launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    var timeLeft by remember { mutableStateOf(20 * 60L) } 

    var qrVersion by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
            if (timeLeft % 60 == 0L) {
                qrVersion++ 
            }
        }
    }

    val formattedTime = remember(timeLeft) {
        val minutes = timeLeft / 60
        val seconds = timeLeft % 60
        String.format("%02d:%02d:%02d", 0, minutes, seconds) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kode pembayaran", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryOrangeColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                "Menunggu pembayaran",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Rp $totalPrice",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Kadaluwarsa dalam ", style = MaterialTheme.typography.bodyMedium)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(4.dp)
                ) {
                      Text(
                        text = formattedTime, 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }


            Spacer(modifier = Modifier.height(32.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("QRIS", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val bitmap = remember(qrVersion) {
                        generateDummyQrBitmap(200, 200, qrVersion)
                    }
                    
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            hasInteracted = true
                            android.widget.Toast.makeText(context, "Kode QR tersimpan di galery", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)), 
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan kode QR", color = Color.Black)
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "Ketuk \"Simpan Kode QR\" atau ambil tangkapan layar untuk menyimpan kode QR ke ponsel anda",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = { 
                    hasInteracted = true
                    cartViewModel.confirmOrder() 
                    com.example.cantin.utils.NotificationUtils.showNotification(
                        context, 
                        "Pembayaran Sukses", 
                        "yey pembayaran sukses. pesanan kamu lagi dimasak.. tunggu ya... ⏳"
                    )
                    navController.navigate(Destinations.ORDER_SUCCESS_ROUTE) {
                         popUpTo(Destinations.HOME_ROUTE) { inclusive = false } 
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Konfirmasi pembayaran", color = Color.Black)
            }
        }
    }
}

fun generateDummyQrBitmap(width: Int, height: Int, seed: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val random = java.util.Random(seed.toLong())
    for (x in 0 until width step 10) {
        for (y in 0 until height step 10) {
            val isBlack = random.nextBoolean()
            val color = if (isBlack) AndroidColor.BLACK else AndroidColor.WHITE
            for (dx in 0 until 10) {
                for (dy in 0 until 10) {
                    if (x + dx < width && y + dy < height) {
                        bitmap.setPixel(x + dx, y + dy, color)
                    }
                }
            }
        }
    }
    return bitmap
}
