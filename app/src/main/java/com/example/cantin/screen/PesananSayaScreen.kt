package com.example.cantin.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Videocam
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
import com.example.cantin.ui.theme.PrimaryOrangeColor
import com.example.cantin.viewmodel.CartViewModel
import com.example.cantin.model.CartItem
import com.example.cantin.model.MenuItem
import com.example.cantin.model.Order
import com.example.cantin.model.Review
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import java.text.NumberFormat
import com.example.cantin.utils.formatRupiah
import java.util.Locale
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesananSayaScreen(
    cartViewModel: CartViewModel,
    navController: NavController,
    initialTab: Int = 1 
) {
    val activeOrders by cartViewModel.activeOrders.collectAsState()
    val completedOrders by cartViewModel.completedOrders.collectAsState()
    val myReviews by cartViewModel.myReviews.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(if(initialTab == 1) 2 else initialTab) } 
    val tabs = listOf("Belum Dinilai", "Penilaian Saya", "Dalam Proses") 

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val titleText = when(selectedTab) {
                        0 -> "Nilai Produk"
                        1 -> "Ulasan Saya"
                        else -> "Pesanan Saya"
                    }
                    Text(titleText, color = Color.White, fontWeight = FontWeight.Bold) 
                },
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
                .background(Color.White)
        ) {
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                tabs.forEachIndexed { index, title ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 4.dp) 
                            .clickableNoRipple { selectedTab = index }
                            .weight(1f)
                    ) {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) PrimaryOrangeColor else Color.Gray,
                            fontSize = 12.sp, 
                            maxLines = 1
                        )
                        if (selectedTab == index) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .width(40.dp)
                                    .height(3.dp)
                                    .background(PrimaryOrangeColor, RoundedCornerShape(1.5.dp))
                            )
                        }
                    }
                }
            }

            when (selectedTab) {
                2 -> {
                    if (activeOrders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                            Text("Belum ada pesanan yang sedang diproses.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f).padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(activeOrders) { order ->
                                OrderCard(
                                    order = order, 
                                    onComplete = { cartViewModel.markOrderAsComplete(order.id) },
                                    onCancel = { cartViewModel.cancelOrder(order.id) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                     if (myReviews.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada ulasan yang dikirim saat ini.", color = Color.Gray)
                        }
                    } else {
                        var showEditDialog by remember { mutableStateOf(false) }
                        var selectedReview by remember { mutableStateOf<Review?>(null) }
                        
                        if (showEditDialog && selectedReview != null) {
                            EditReviewDialog(
                                review = selectedReview!!,
                                onDismiss = { showEditDialog = false },
                                onSave = { updatedReview ->
                                    cartViewModel.updateReview(updatedReview)
                                    showEditDialog = false
                                }
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(myReviews) { review ->
                                SubmittedReviewCard(
                                    review = review,
                                    onEditClick = {
                                        navController.navigate(com.example.cantin.navigation.Destinations.createUpdateReviewRoute(review.id))
                                    }
                                )
                            }
                        }
                    }
                }
                0 -> {
                     if (completedOrders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada pesanan untuk dinilai.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(completedOrders) { item ->
                                RatingItemCard(item, cartViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubmittedReviewCard(
    review: com.example.cantin.model.Review,
    onEditClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row {
                         for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (i <= review.rating) Color(0xFFFFC107) else Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Review: ${review.reviewText}", fontSize = 14.sp)
            
            if (review.imageUrl != null) { 
                 Spacer(modifier = Modifier.height(8.dp))
                 AsyncImage(
                    model = review.imageUrl, 
                    contentDescription = "Review Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
                )
            } else if (review.imageUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = review.imageUri,
                    contentDescription = "Review Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.SpaceBetween,
                 verticalAlignment = Alignment.CenterVertically
            ) {


                 OutlinedButton(
                     onClick = onEditClick,
                     shape = RoundedCornerShape(8.dp),
                     contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                     modifier = Modifier.height(32.dp)
                 ) {
                     Text("Perbarui", color = PrimaryOrangeColor, fontSize = 12.sp)
                 }
            }
        }
    }
}

@Composable
fun EditReviewDialog(
    review: com.example.cantin.model.Review,
    onDismiss: () -> Unit,
    onSave: (com.example.cantin.model.Review) -> Unit
) {
    var rating by remember { mutableIntStateOf(review.rating) }
    var text by remember { mutableStateOf(review.reviewText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Ulasan", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                     for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                                .clickableNoRipple { rating = i }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Ulasan") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onSave(review.copy(rating = rating, reviewText = text)) 
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeColor)
            ) {
                Text("Simpan", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun OrderCard(
    order: com.example.cantin.model.Order,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) { }
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    var timeRemaining by remember { mutableLongStateOf(0L) }
    var isTimerFinished by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = order.timestamp) {
        while (true) {
            val elapsed = System.currentTimeMillis() - order.timestamp
            val remaining = order.durationInMillis - elapsed
            
            if (remaining <= 0) {
                timeRemaining = 0
                if (!isTimerFinished) {
                    isTimerFinished = true
                    com.example.cantin.utils.NotificationUtils.showNotification(
                        context = context,
                        title = "Pesanan Selesai",
                        message = "Pesanan Anda telah selesai dan siap diambil.. terimakasih telah menunggu! 🤗",
                        notificationId = order.id.hashCode() 
                    )
                }
            } else {
                timeRemaining = remaining
                isTimerFinished = false
            }
            kotlinx.coroutines.delay(1000L)
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 val suffix = if (order.isTakeAway) "B" else "M"
                 Text(
                    text = "Order #${order.id.take(4).uppercase()}$suffix", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp
                )
                
                if (!isTimerFinished) {
                     val minutes = (timeRemaining / 1000) / 60
                     val seconds = (timeRemaining / 1000) % 60
                     Text(
                         text = String.format("Estimasi: %02d:%02d", minutes, seconds),
                         color = PrimaryOrangeColor,
                         fontWeight = FontWeight.Bold
                     )
                } else {
                    Text("Siap Diambil", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                order.items.forEach { item ->
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Text("${item.quantity}x", fontWeight = FontWeight.Bold, color = PrimaryOrangeColor)
                         Spacer(modifier = Modifier.width(8.dp))
                         Text(item.menuItem.name, modifier = Modifier.weight(1f))
                         Text(formatRupiah(item.menuItem.price * item.quantity), fontSize = 12.sp, color = Color.Gray)
                     }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val totalBatchPrice = order.items.sumOf { it.quantity * it.menuItem.price }
             Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                 Text("Total Pesanan", fontWeight = FontWeight.Bold)
                 Text(formatRupiah(totalBatchPrice), fontWeight = FontWeight.Bold, color = PrimaryOrangeColor)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batalkan", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if(isTimerFinished) PrimaryOrangeColor else Color.Gray),
                    shape = RoundedCornerShape(8.dp),
                    enabled = isTimerFinished
                ) {
                    Text("Selesai", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RatingItemCard(item: CartItem, cartViewModel: CartViewModel) {
    var rating by remember { mutableIntStateOf(3) }
    var reviewText by remember { mutableStateOf("") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
        }
    }
    
    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
         if (!success) {
         }
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = item.menuItem.imageResId),
                    contentDescription = item.menuItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(item.menuItem.name, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(formatRupiah(item.menuItem.price), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Total ${item.quantity} produk: ${formatRupiah(item.menuItem.price * item.quantity)}", fontSize = 10.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Nilai Produk", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star $i",
                        tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier
                            .size(32.dp)
                            .clickableNoRipple { rating = i }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Tulis Ulasan 20 karakter", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Bagikan penilaianmu...", fontSize = 12.sp, color = Color.Gray) },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.widget.Toast.makeText(context, "Izin kamera diberikan, silakan coba lagi.", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(context, "Izin kamera diperlukan.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                )
                if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    try {
                        val file = createImageFile(context)
                        val uri = getUriForFile(context, file)
                        imageUri = uri
                        takePictureLauncher.launch(uri)
                    } catch (e: Exception) {
                         android.widget.Toast.makeText(context, "Gagal membuka kamera: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                } else {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }, 
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray), 
            modifier = Modifier.weight(1f), 
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black)
            Text("tambah foto", fontSize = 10.sp, color = Color.Black)
        }
        
        Button(
            onClick = {
                val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                )
                if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    try {
                        val file = createVideoFile(context)
                        val uri = getUriForFile(context, file)
                        videoUri = uri
                        captureVideoLauncher.launch(uri)
                    } catch (e: Exception) {
                         android.widget.Toast.makeText(context, "Gagal membuka video: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                 } else {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }, 
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray), 
            modifier = Modifier.weight(1f), 
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.Black)
            Text("tambah video", fontSize = 10.sp, color = Color.Black)
        }
    }
            
            if (imageUri != null || videoUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (imageUri != null) {
                         AsyncImage(
                            model = imageUri,
                            contentDescription = "Captured Photo",
                            contentScale = ContentScale.Crop,
                             modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Gray)
                        )
                    }
                     if (videoUri != null) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = "Video Recorded", tint = Color.White)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
             Button(
                onClick = { 
                    if (rating == 0) {
                        android.widget.Toast.makeText(context, "Silakan beri bintang dulu", android.widget.Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val review = com.example.cantin.model.Review(
                        id = java.util.UUID.randomUUID().toString(),
                        menuId = item.menuItem.id,
                        rating = rating,
                        reviewText = reviewText,
                        imageUri = imageUri,
                        videoUri = videoUri,
                        userName = "Rose" 
                    )
                    cartViewModel.submitReview(review)
                    android.widget.Toast.makeText(context, "Ulasan terkirim!", android.widget.Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Kirim", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun LogItemCard(item: CartItem) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.menuItem.imageResId),
                contentDescription = item.menuItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.menuItem.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                     Text(
                        text = formatRupiah(item.menuItem.price), 
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                     Text(
                        text = "Total ${item.quantity} produk: ${formatRupiah(item.menuItem.price * item.quantity)}", 
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}




fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.externalCacheDir
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

fun createVideoFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.externalCacheDir
    return File.createTempFile(
        "MP4_${timeStamp}_",
        ".mp4",
        storageDir
    )
}

fun getUriForFile(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(
        context,
        "com.example.cantin.fileprovider",
        file
    )
}

@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
)
