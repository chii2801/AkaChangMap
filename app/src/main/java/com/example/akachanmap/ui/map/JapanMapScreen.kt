package com.example.akachanmap.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akachanmap.data.model.CryLevel
import com.example.akachanmap.data.model.CryPost
import com.example.akachanmap.data.model.Prefecture
import com.example.akachanmap.data.repository.DailyStats

private val japanLayout: List<List<Prefecture?>> = listOf(
    listOf(null, null, null, null, null, Prefecture.HOKKAIDO, Prefecture.HOKKAIDO),
    listOf(null, null, null, Prefecture.AOMORI, Prefecture.IWATE, null, null),
    listOf(null, null, null, Prefecture.AKITA, Prefecture.MIYAGI, null, null),
    listOf(null, null, null, Prefecture.YAMAGATA, Prefecture.FUKUSHIMA, null, null),
    listOf(null, Prefecture.NIIGATA, Prefecture.TOCHIGI, Prefecture.IBARAKI, null, null, null),
    listOf(null, null, Prefecture.GUNMA, Prefecture.SAITAMA, Prefecture.CHIBA, null, null),
    listOf(null, Prefecture.TOYAMA, Prefecture.NAGANO, Prefecture.TOKYO, null, null, null),
    listOf(null, Prefecture.ISHIKAWA, Prefecture.YAMANASHI, Prefecture.KANAGAWA, null, null, null),
    listOf(Prefecture.FUKUI, Prefecture.GIFU, Prefecture.SHIZUOKA, Prefecture.AICHI, null, null, null),
    listOf(Prefecture.KYOTO, Prefecture.SHIGA, Prefecture.MIE, null, null, null, null),
    listOf(Prefecture.HYOGO, Prefecture.OSAKA, Prefecture.NARA, Prefecture.WAKAYAMA, null, null, null),
    listOf(Prefecture.TOTTORI, Prefecture.OKAYAMA, Prefecture.HIROSHIMA, Prefecture.YAMAGUCHI, null, null, null),
    listOf(Prefecture.SHIMANE, Prefecture.KAGAWA, Prefecture.TOKUSHIMA, null, null, null, null),
    listOf(null, Prefecture.EHIME, Prefecture.KOCHI, null, null, null, null),
    listOf(Prefecture.FUKUOKA, Prefecture.OITA, Prefecture.MIYAZAKI, null, null, null, null),
    listOf(Prefecture.SAGA, Prefecture.KUMAMOTO, Prefecture.KAGOSHIMA, null, null, null, null),
    listOf(Prefecture.NAGASAKI, null, null, null, null, null, Prefecture.OKINAWA)
)

@Composable
fun JapanMapScreen(
    posts: List<CryPost>,
    stats: DailyStats,
    myPrefecture: Prefecture,
    onPrefectureClick: (Prefecture) -> Unit,
    onPostClick: () -> Unit,
    onAccountClick: () -> Unit = {},
    userAvatar: String = "👩",
    userNickname: String = "",
    myPostId: String? = null,
    onStopCry: () -> Unit = {}
) {
    val postCountByPref = posts.groupBy { it.prefecture }
    var showReport by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)) {
                Spacer(modifier = Modifier.height(32.dp))
                // アカウント情報
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(userAvatar.ifBlank { "👤" }, fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = userNickname.ifBlank { "ニックネーム未設定" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text("アカウント設定") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onAccountClick()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8F8))) {

        // 上部バナー（タップでレポート表示）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6B6B))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // ハンバーガーアイコン（左上）
            IconButton(
                onClick = { scope.launch { drawerState.open() } },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "メニュー", tint = Color.White)
            }
            // バナーテキスト（タップでレポート）
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showReport = true }
                    .padding(horizontal = 40.dp)
            ) {
                Text(
                    "🍼 今 ${stats.total} 人の赤ちゃんが泣いてるゾ！",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    "タップでレポートを見る →",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }

        // ユーザー情報バー
        if (userNickname.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEEEE))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(userAvatar, fontSize = 24.sp)
                Text(
                    text = "${userNickname}さん、おつかれさま！",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCC4444)
                )
            }
        }

        // 地図
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                japanLayout.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        row.forEach { pref ->
                            if (pref == null) {
                                Spacer(modifier = Modifier.size(48.dp))
                            } else {
                                val prefPosts = postCountByPref[pref.name] ?: emptyList()
                                PrefectureBlock(
                                    prefecture = pref,
                                    postCount = prefPosts.size,
                                    topCryLevel = prefPosts.maxOfOrNull { it.cryLevel } ?: 1,
                                    isMyPref = pref == myPrefecture,
                                    onClick = { onPrefectureClick(pref) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 投稿 or 泣き止んだボタン
        if (myPostId != null) {
            Button(
                onClick = onStopCry,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A))
            ) {
                Text("😊 泣き止んだ！", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onPostClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
            ) {
                Text("😭 うちの子も泣いてる！", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // レポートダイアログ
    if (showReport) {
        ReportDialog(stats = stats, onDismiss = { showReport = false })
    }
    } // ModalNavigationDrawer
}

@Composable
fun ReportDialog(stats: DailyStats, onDismiss: () -> Unit) {
    val topPrefLabel = Prefecture.values()
        .firstOrNull { it.name == stats.topPrefName }?.label ?: stats.topPrefName

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("📊 今日の日本の泣き状況", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ReportRow("🍼 今泣いてる赤ちゃん", "${stats.total} 人")
                if (topPrefLabel.isNotEmpty()) {
                    ReportRow("🏆 号泣エリア1位", "$topPrefLabel (${stats.topPrefCount}人)")
                }
                ReportRow("🌙 深夜に泣いてた子", "${stats.nightCount} 人")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "みんな頑張ってるのだ。あなただけじゃないゾ！",
                    fontSize = 13.sp,
                    color = Color(0xFFFF6B6B),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("とじる", color = Color(0xFFFF6B6B))
            }
        }
    )
}

@Composable
fun ReportRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PrefectureBlock(
    prefecture: Prefecture,
    postCount: Int,
    topCryLevel: Int,
    isMyPref: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (postCount > 0) prefecture.region.color.copy(alpha = 0.85f)
                  else prefecture.region.color.copy(alpha = 0.25f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .background(bgColor, RoundedCornerShape(8.dp))
            .then(
                if (isMyPref) Modifier.border(2.dp, Color(0xFFFF6B6B), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onClick() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = prefecture.label,
                fontSize = 8.sp,
                color = if (postCount > 0) Color.White else Color.Gray,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                lineHeight = 10.sp
            )
            if (postCount > 0) {
                Text(text = CryLevel.fromInt(topCryLevel).emoji, fontSize = 12.sp)
            }
        }
    }
}
