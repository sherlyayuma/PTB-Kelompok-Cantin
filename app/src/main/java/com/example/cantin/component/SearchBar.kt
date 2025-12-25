package com.example.cantin.component


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cantin.ui.theme.PrimaryOrangeColor

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Makanan, Minuman") },
        leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        singleLine = true,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(percent = 50),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color(0xFFF0F0F0), 
            unfocusedContainerColor = Color(0xFFF0F0F0)
        )
    )
}

