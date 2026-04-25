package com.example.akachanmap.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akachanmap.data.model.CryLevel

@Composable
fun CryLevelSelector(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        CryLevel.values().forEach { level ->
            val isSelected = selected == level.level
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFFFF6B6B) else Color.LightGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(level.level) }
                    .padding(12.dp)
            ) {
                Text(text = level.emoji, fontSize = 32.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = level.label,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) Color(0xFFFF6B6B) else Color.Gray
                )
            }
        }
    }
}
