package com.example.cantin.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cantin.model.MenuCategory
import com.example.cantin.ui.theme.PrimaryOrangeColor

@Composable
fun KategoriChips(
    selectedCategory: MenuCategory,
    onCategorySelected: (MenuCategory) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MenuCategory.values().forEach { category ->
            CategoryChip(
                text = if (category == MenuCategory.MAKANAN) "Makanan" else "Minuman", 
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}


@Composable
fun RowScope.CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) PrimaryOrangeColor else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}


