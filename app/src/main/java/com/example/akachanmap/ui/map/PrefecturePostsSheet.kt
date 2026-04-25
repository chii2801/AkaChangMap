package com.example.akachanmap.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akachanmap.data.model.CryLevel
import com.example.akachanmap.data.model.CryPost
import com.example.akachanmap.data.model.Prefecture
import com.example.akachanmap.data.model.ReactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrefecturePostsSheet(
    prefecture: Prefecture,
    posts: List<CryPost>,
    myPostId: String?,
    onReaction: (postId: String, reactionKey: String) -> Unit,
    onStopped: (postId: String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "📍 ${prefecture.label}の赤ちゃん",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("今は泣いてる子がいないのだ😊", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 500.dp)
                ) {
                    items(posts) { post ->
                        PostCard(
                            post = post,
                            isMyPost = post.id == myPostId,
                            onReaction = { key -> onReaction(post.id, key) },
                            onStopped = { onStopped(post.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: CryPost,
    isMyPost: Boolean,
    onReaction: (String) -> Unit,
    onStopped: () -> Unit
) {
    val level = CryLevel.fromInt(post.cryLevel)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (post.stopped) Color(0xFFE8F5E9) else Color(0xFFFFF0F0)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (post.stopped) "😌" else level.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (post.stopped) "泣き止みました！！🎉" else level.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (post.stopped) Color(0xFF388E3C) else Color.DarkGray
                    )
                    if (post.nickname.isNotBlank()) {
                        val ageLabel = calcBabyAge(post.babyBirthdate)
                        Text(
                            text = "${post.nickname}${if (ageLabel.isNotBlank()) "（$ageLabel）" else ""}",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (post.comment.isNotBlank()) {
                        Text(
                            text = "「${post.comment}」",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (!post.stopped) {
                Spacer(modifier = Modifier.height(10.dp))

                // リアクションボタン
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ReactionType.values().forEach { reaction ->
                        val count = post.reactions[reaction.name] ?: 0
                        ReactionChip(
                            emoji = reaction.emoji,
                            label = reaction.label,
                            count = count,
                            onClick = { onReaction(reaction.name) }
                        )
                    }
                }

                // 自分の投稿なら泣き止んだボタン
                if (isMyPost) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onStopped,
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A))
                    ) {
                        Text("😊 泣き止んだ！", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun calcBabyAge(birthdate: String): String {
    if (birthdate.isBlank()) return ""
    return try {
        val fmt = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        val birth = fmt.parse(birthdate) ?: return ""
        val now = Date()
        val diffMs = now.time - birth.time
        val months = (diffMs / (1000L * 60 * 60 * 24 * 30.44)).toInt()
        when {
            months < 1 -> "0ヶ月"
            months < 24 -> "${months}ヶ月"
            else -> "${months / 12}歳"
        }
    } catch (e: Exception) { "" }
}

@Composable
fun ReactionChip(emoji: String, label: String, count: Int, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(Color(0xFFFFE0E0), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 16.sp)
            if (count > 0) {
                Text(text = "$count", fontSize = 9.sp, color = Color(0xFFFF6B6B))
            }
        }
    }
}
