package com.chii2801.akachanmap.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chii2801.akachanmap.data.model.Prefecture
import com.chii2801.akachanmap.data.model.Region

@Composable
fun PrefectureSetupScreen(onDone: (Prefecture) -> Unit) {
    var selected by remember { mutableStateOf<Prefecture?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6B6B))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🍼", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "住んでる都道府県を教えてのだ！",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Region.values().forEach { region ->
                item {
                    Text(
                        text = region.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = region.color,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    val prefs = Prefecture.values().filter { it.region == region }
                    FlowRow(prefs = prefs, selected = selected, onSelect = { selected = it })
                }
            }
        }

        Button(
            onClick = { selected?.let { onDone(it) } },
            enabled = selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
        ) {
            Text("はじめるのだ！", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FlowRow(prefs: List<Prefecture>, selected: Prefecture?, onSelect: (Prefecture) -> Unit) {
    var rowItems = prefs.toMutableList()
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        while (rowItems.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val rowCount = minOf(4, rowItems.size)
                repeat(rowCount) {
                    val pref = rowItems.removeFirst()
                    val isSelected = selected == pref
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFFFF6B6B) else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                if (isSelected) Color(0xFFFFEEEE) else Color.White,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelect(pref) }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = pref.label,
                            fontSize = 13.sp,
                            color = if (isSelected) Color(0xFFFF6B6B) else Color.DarkGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
