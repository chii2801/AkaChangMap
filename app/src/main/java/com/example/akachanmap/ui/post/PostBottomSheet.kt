package com.example.akachanmap.ui.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akachanmap.ui.components.CryLevelSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBottomSheet(
    onDismiss: () -> Unit,
    onPost: (level: Int, comment: String) -> Unit,
    isPosting: Boolean
) {
    var selectedLevel by remember { mutableStateOf(1) }
    var comment by remember { mutableStateOf("") }

    val commentSuggestions = listOf("もう限界〜", "でもかわいい", "なんで泣いてるかわからん", "一緒に頑張ろ", "眠い...")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🍼 今どのくらい泣いてる？",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            CryLevelSelector(selected = selectedLevel, onSelect = { selectedLevel = it })

            Text(text = "今日の一言（任意）", fontSize = 14.sp, color = Color.Gray)

            // 候補ボタン
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                commentSuggestions.take(3).forEach { suggestion ->
                    SuggestionChip(
                        onClick = { comment = suggestion },
                        label = { Text(suggestion, fontSize = 11.sp) }
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                commentSuggestions.drop(3).forEach { suggestion ->
                    SuggestionChip(
                        onClick = { comment = suggestion },
                        label = { Text(suggestion, fontSize = 11.sp) }
                    )
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { if (it.length <= 50) comment = it },
                placeholder = { Text("自由に入力も OK なのだ！") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                text = "※ 投稿は2時間後に自動的に消えるゾ",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Button(
                onClick = { onPost(selectedLevel, comment) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isPosting,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
            ) {
                if (isPosting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("投稿する！", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
