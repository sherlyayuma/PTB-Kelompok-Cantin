package com.example.cantin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cantin.ui.theme.PrimaryOrangeColor
import com.example.cantin.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihMejaScreen(
    cartViewModel: CartViewModel,
    navController: NavController
) {
    val user by cartViewModel.userProfile.collectAsState()
    val selectedTable by cartViewModel.selectedTable.collectAsState()
    val occupiedTables by cartViewModel.occupiedTables.collectAsState()

    val colGroups = listOf("A", "B", "C")
    val rows = (1..10).toList()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Pilih Meja", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryOrangeColor)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryOrangeColor)
                        .padding(16.dp)
                ) {
                   Column {
                       Text(user.name.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                       Text("Meja ${selectedTable ?: "-"}", color = Color.White, fontSize = 12.sp)
                   }
                }
            }
        },
        bottomBar = {
            Button(
                onClick = { navController.popBackStack() }, 
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)) 
            ) {
                Text("SIMPAN", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color.White, borderColor = PrimaryOrangeColor, text = "Tersedia")
                LegendItem(color = Color(0xFFFF9800), borderColor = Color.Transparent, text = "Dipilih") 
                LegendItem(color = Color.LightGray, borderColor = Color.Transparent, text = "Terisi")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 32.dp) 
            ) {
                 colGroups.forEach { Text(it, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center) }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(rows.size) { index ->
                    val rowNum = rows[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("$rowNum", fontWeight = FontWeight.Bold, modifier = Modifier.width(20.dp), textAlign = TextAlign.End)
                        
                        Spacer(modifier = Modifier.width(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            colGroups.forEach { col ->
                                val tableId = "$col$rowNum"
                                SeatItem(
                                    tableId = tableId,
                                    state = when {
                                        occupiedTables.contains(tableId) -> SeatState.OCCUPIED
                                        selectedTable == tableId -> SeatState.SELECTED
                                        else -> SeatState.AVAILABLE
                                    },
                                    onClick = { cartViewModel.selectTable(tableId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class SeatState { AVAILABLE, SELECTED, OCCUPIED }

@Composable
fun SeatItem(
    tableId: String,
    state: SeatState,
    onClick: () -> Unit
) {
    val backgroundColor = when (state) {
        SeatState.AVAILABLE -> Color.White
        SeatState.SELECTED -> Color(0xFF4169E1) 
        SeatState.OCCUPIED -> Color.LightGray
    }
    
    val borderColor = if (state == SeatState.AVAILABLE) Color(0xFF4169E1) else Color.Transparent

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = state != SeatState.OCCUPIED, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (state == SeatState.SELECTED) {
            Text("SP", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) 
        }
    }
}

@Composable
fun LegendItem(color: Color, borderColor: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .border(1.dp, borderColor, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}
