package com.chii2801.akachanmap.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("バージョン情報", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6B6B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF8F8))
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // アイコン＋アプリ名
            Text("🍼", fontSize = 72.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "AkaChangMap",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B)
                )
                Text(
                    "アカちゃんマップ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // バージョン情報カード
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    VersionRow("バージョン", "1.0.0")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    VersionRow("開発者", "chii2801")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    VersionRow("お問い合わせ", "sakurairyutarou@gmail.com")
                }
            }

            // アップデート履歴カード
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "アップデート履歴",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFFF6B6B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ChangelogItem("1.0.0", "初回リリース 🎉")
                }
            }

            // リンクボタン
            OutlinedButton(
                onClick = { uriHandler.openUri("https://chii2801.github.io/AkaChangMap/privacy-policy") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("プライバシーポリシー") }

            OutlinedButton(
                onClick = { uriHandler.openUri("https://chii2801.github.io/AkaChangMap/terms-of-service") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("利用規約") }
        }
    }
}

@Composable
private fun VersionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ChangelogItem(version: String, description: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            version,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6B6B),
            modifier = Modifier.width(48.dp)
        )
        Text(description, fontSize = 13.sp, color = Color.DarkGray)
    }
}
