package com.chii2801.akachanmap.ui.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chii2801.akachanmap.data.model.CryPost
import com.chii2801.akachanmap.data.repository.DailyStats
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.sqrt
import kotlin.random.Random

private val STAR_POSITIONS = listOf(
    Triple(0.39f, 0.10f, 20f),
    Triple(0.74f, 0.14f, 20f),
    Triple(0.13f, 0.08f, 12f),
    Triple(0.54f, 0.07f, 16f),
    Triple(0.85f, 0.08f, 12f),
    Triple(0.22f, 0.18f, 16f),
    Triple(0.85f, 0.25f, 16f),
    Triple(0.39f, 0.35f, 20f),
    Triple(0.77f, 0.38f, 20f),
    Triple(0.13f, 0.30f, 12f),
    Triple(0.56f, 0.29f, 16f),
    Triple(0.87f, 0.30f, 12f),
    Triple(0.33f, 0.55f, 20f),
    Triple(0.80f, 0.58f, 20f),
    Triple(0.13f, 0.50f, 16f),
    Triple(0.60f, 0.51f, 16f),
    Triple(0.44f, 0.52f, 12f),
    Triple(0.91f, 0.53f, 12f),
    Triple(0.25f, 0.80f, 16f),
    Triple(0.70f, 0.78f, 12f),
    Triple(0.50f, 0.90f, 20f),
)

private val INITIAL_BUBBLE_POSITIONS = listOf(
    Pair(0.15f, 0.20f),
    Pair(0.60f, 0.15f),
    Pair(0.35f, 0.45f),
    Pair(0.70f, 0.40f),
    Pair(0.10f, 0.60f),
    Pair(0.55f, 0.65f),
    Pair(0.80f, 0.70f),
    Pair(0.30f, 0.80f),
)

@Composable
fun HouseRoomScreen(
    posts: List<CryPost>,
    stats: DailyStats,
    myPostId: String?,
    myUid: String? = null,
    userAvatar: String,
    userNickname: String,
    userPrefecture: String = "",
    onPostClick: () -> Unit,
    onStopCry: () -> Unit,
    onAccountClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    var showStopModal by remember { mutableStateOf(false) }
    var showReport by remember { mutableStateOf(false) }
    var selectedBubble by remember { mutableStateOf<BubbleDef?>(null) }

    // 自分の投稿をIDとuidの両方で除外（race conditionを防ぐ）
    val otherPosts = posts.filter { p ->
        !p.stopped && p.id != myPostId && (myUid == null || p.userId != myUid)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)) {
                Spacer(Modifier.height(32.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(userAvatar.ifBlank { "👤" }, fontSize = 56.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(userNickname.ifBlank { "ニックネーム未設定" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, null) },
                    label = { Text("アカウント設定") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onAccountClick() },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, null) },
                    label = { Text("利用規約") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        uriHandler.openUri("https://chii2801.github.io/AkaChangMap/terms-of-service")
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Lock, null) },
                    label = { Text("プライバシーポリシー") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        uriHandler.openUri("https://chii2801.github.io/AkaChangMap/privacy-policy")
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, null) },
                    label = { Text("バージョン情報") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onAboutClick() },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 上部バナー
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF6B6B))
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Default.Menu, "メニュー", tint = Color.White)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showReport = true }
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        "🍼 今 ${stats.total} 人の赤ちゃんが泣いてるゾ！",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "タップでレポートを見る",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
                Spacer(Modifier.width(48.dp))
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
                    Text(userAvatar, fontSize = 20.sp)
                    Text(
                        "${userNickname}さん、おつかれさま！",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCC4444)
                    )
                }
            }

            // 夜空エリア
            val density = LocalDensity.current.density
            var boxSize by remember { mutableStateOf(IntSize.Zero) }

            // バブル定義リスト（posts変化時に再生成）
            val bubbleDefs = remember(otherPosts, myPostId) {
                val list = mutableListOf<BubbleDef>()
                otherPosts.take(INITIAL_BUBBLE_POSITIONS.size).forEachIndexed { i, post ->
                    val init = INITIAL_BUBBLE_POSITIONS[i]
                    list += BubbleDef(
                        id = post.id,
                        label = post.nickname.ifBlank { "ままさん" },
                        emoji = when {
                            post.cryLevel >= 3 -> "👩‍ 👶"
                            post.cryLevel == 2 -> "👵 👶"
                            else -> "👩 👶"
                        },
                        radiusDp = 43f,
                        isMe = false,
                        initXFrac = init.first,
                        initYFrac = init.second,
                        seed = post.id.hashCode().toLong(),
                        comment = post.comment,
                        babyBirthdate = post.babyBirthdate
                    )
                }
                if (myPostId != null) {
                    val myPost = posts.firstOrNull { it.id == myPostId }
                    list += BubbleDef(
                        id = "me",
                        label = userNickname.ifBlank { "あなた" },
                        emoji = "${userAvatar.ifBlank { "👩" }} 👶",
                        radiusDp = 48f,
                        isMe = true,
                        initXFrac = 0.45f,
                        initYFrac = 0.75f,
                        seed = 42L,
                        comment = myPost?.comment ?: "",
                        babyBirthdate = myPost?.babyBirthdate ?: ""
                    )
                }
                list
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0A1628), Color(0xFF0D1B2A), Color(0xFF162035))
                        )
                    )
                    .onSizeChanged { boxSize = it }
            ) {
                if (boxSize.width > 0) {
                    val w = boxSize.width.toFloat()
                    val h = boxSize.height.toFloat()

                    // 星
                    STAR_POSITIONS.forEach { (fx, fy, sizePx) ->
                        val starSp = (sizePx * 0.75f).coerceIn(9f, 20f)
                        Text(
                            text = "✦",
                            color = Color.White.copy(alpha = (0.5f + sizePx / 60f).coerceIn(0.4f, 0.9f)),
                            fontSize = starSp.sp,
                            modifier = Modifier.offset(
                                x = (fx * w / density).dp,
                                y = (fy * h / density).dp
                            )
                        )
                    }

                    // シャボン玉（物理一括管理）
                    BubblePhysicsScene(
                        defs = bubbleDefs,
                        boxWidthPx = w,
                        boxHeightPx = h,
                        density = density,
                        onBubbleClick = { selectedBubble = it }
                    )
                }
            }

            // ボトムボタン
            if (myPostId != null) {
                Button(
                    onClick = { showStopModal = true },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A))
                ) {
                    Text("😊 泣き止んだ！", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onPostClick,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                ) {
                    Text("😭 うちの子も泣いてる！", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // 泣き止んだモーダル
    if (showStopModal) {
        AlertDialog(
            onDismissRequest = { showStopModal = false; onStopCry() },
            shape = RoundedCornerShape(20.dp),
            title = null,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✨😊✨", fontSize = 48.sp)
                    Text("おつかれさまでした！", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF6B6B))
                    Text("ゆっくり休んでね🍵", fontSize = 14.sp, color = Color.DarkGray)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStopModal = false; onStopCry() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text("ありがとう！", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // バブルタップ → プロフィールポップアップ
    selectedBubble?.let { bubble ->
        BubbleInfoDialog(bubble = bubble, onDismiss = { selectedBubble = null })
    }

    // レポートダイアログ
    if (showReport) {
        val prefCount = if (userPrefecture.isNotEmpty()) {
            posts.count { !it.stopped && it.prefecture == userPrefecture }
        } else 0
        val prefLabel = com.chii2801.akachanmap.data.model.Prefecture.values()
            .firstOrNull { it.name == userPrefecture }?.label ?: userPrefecture

        AlertDialog(
            onDismissRequest = { showReport = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("📊 今の泣き状況", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🍼 今泣いてる赤ちゃん", fontSize = 13.sp, color = Color.DarkGray)
                        Text("${stats.total} 人", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    if (prefLabel.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("📍 $prefLabel の泣いてる子", fontSize = 13.sp, color = Color.DarkGray)
                            Text("$prefCount 人", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReport = false }) {
                    Text("とじる", color = Color(0xFFFF6B6B))
                }
            }
        )
    }
}

// ──────────────────────────────────────────────
// バブル物理エンジン（全バブル一括管理・衝突検出）
// ──────────────────────────────────────────────

private data class BubbleDef(
    val id: String,
    val label: String,
    val emoji: String,
    val radiusDp: Float,
    val isMe: Boolean,
    val initXFrac: Float,
    val initYFrac: Float,
    val seed: Long,
    val comment: String = "",
    val babyBirthdate: String = "",
)

private class BubbleState(
    val def: BubbleDef,
    val radiusPx: Float,
    initX: Float,
    initY: Float,
    initVx: Float,
    initVy: Float,
) {
    var cx by mutableStateOf(initX)   // 中心X
    var cy by mutableStateOf(initY)   // 中心Y
    var vx = initVx
    var vy = initVy
}

@Composable
private fun BubblePhysicsScene(
    defs: List<BubbleDef>,
    boxWidthPx: Float,
    boxHeightPx: Float,
    density: Float,
    onBubbleClick: (BubbleDef) -> Unit = {},
) {
    val labelHeightPx = 28f * density

    val bubbles = remember(defs, boxWidthPx, boxHeightPx) {
        defs.map { def ->
            val rng = Random(def.seed)
            val r = def.radiusDp * density
            val speed = 45f + rng.nextFloat() * 30f
            val angle = rng.nextFloat() * 2f * Math.PI.toFloat()
            BubbleState(
                def = def,
                radiusPx = r,
                initX = (def.initXFrac * (boxWidthPx - r * 2f) + r).coerceIn(r, boxWidthPx - r),
                initY = (def.initYFrac * (boxHeightPx - r * 2f - labelHeightPx) + r).coerceIn(r, boxHeightPx - r - labelHeightPx),
                initVx = kotlin.math.cos(angle) * speed,
                initVy = kotlin.math.sin(angle) * speed,
            )
        }
    }

    // 物理ループ（毎フレーム全バブルを更新）
    LaunchedEffect(bubbles) {
        var lastMs = 0L
        while (true) {
            withFrameMillis { frameMs ->
                val dt = if (lastMs == 0L) 0f else ((frameMs - lastMs) / 1000f).coerceAtMost(0.05f)
                lastMs = frameMs

                // 1. 位置更新
                for (b in bubbles) {
                    b.cx += b.vx * dt
                    b.cy += b.vy * dt
                }

                // 2. 壁の跳ね返り
                for (b in bubbles) {
                    val maxCx = boxWidthPx - b.radiusPx
                    val maxCy = boxHeightPx - b.radiusPx - labelHeightPx
                    if (b.cx < b.radiusPx) { b.cx = b.radiusPx; b.vx = b.vx.absoluteValue }
                    else if (b.cx > maxCx) { b.cx = maxCx; b.vx = -b.vx.absoluteValue }
                    if (b.cy < b.radiusPx) { b.cy = b.radiusPx; b.vy = b.vy.absoluteValue }
                    else if (b.cy > maxCy) { b.cy = maxCy; b.vy = -b.vy.absoluteValue }
                }

                // 3. バブル同士の衝突
                for (i in bubbles.indices) {
                    for (j in i + 1 until bubbles.size) {
                        val a = bubbles[i]
                        val b = bubbles[j]
                        val dx = b.cx - a.cx
                        val dy = b.cy - a.cy
                        val dist = sqrt(dx * dx + dy * dy)
                        val minDist = a.radiusPx + b.radiusPx
                        if (dist < minDist && dist > 0.01f) {
                            // 重なりを解消
                            val nx = dx / dist
                            val ny = dy / dist
                            val overlap = (minDist - dist) * 0.5f
                            a.cx -= nx * overlap
                            a.cy -= ny * overlap
                            b.cx += nx * overlap
                            b.cy += ny * overlap

                            // 弾性衝突（等質量）: 法線方向の速度成分を交換
                            val dvx = a.vx - b.vx
                            val dvy = a.vy - b.vy
                            val dot = dvx * nx + dvy * ny
                            if (dot > 0f) {
                                a.vx -= dot * nx
                                a.vy -= dot * ny
                                b.vx += dot * nx
                                b.vy += dot * ny
                            }
                        }
                    }
                }
            }
        }
    }

    // 描画
    bubbles.forEach { b ->
        BubbleBall(bubble = b, density = density, onClick = { onBubbleClick(b.def) })
    }
}

@Composable
private fun BubbleBall(bubble: BubbleState, density: Float, onClick: () -> Unit = {}) {
    val def = bubble.def
    val diameterDp = (bubble.radiusPx * 2f / density).dp

    // 呼吸スケール
    val infiniteTransition = rememberInfiniteTransition(label = "breathe_${def.id}")
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600 + (def.seed % 800).toInt().absoluteValue, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b"
    )
    val scale = 1f + breathe * 0.035f

    val offsetXDp = ((bubble.cx - bubble.radiusPx) / density).dp
    val offsetYDp = ((bubble.cy - bubble.radiusPx) / density).dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(x = offsetXDp, y = offsetYDp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(diameterDp * scale)
        ) {
            SoapBubbleCanvas(modifier = Modifier.fillMaxSize(), isMe = def.isMe)
            Text(
                text = def.emoji,
                fontSize = (diameterDp.value * 0.28f * scale).sp,
                textAlign = TextAlign.Center,
                lineHeight = (diameterDp.value * 0.32f * scale).sp
            )
        }
        Text(
            text = def.label,
            color = if (def.isMe) Color(0xFFFFB3B3) else Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.widthIn(max = 100.dp).padding(top = 2.dp)
        )
    }
}

// ──────────────────────────────────────────────
// バブル情報ダイアログ
// ──────────────────────────────────────────────

@Composable
private fun BubbleInfoDialog(bubble: BubbleDef, onDismiss: () -> Unit) {
    val babyAge = remember(bubble.babyBirthdate) { calcBabyAge(bubble.babyBirthdate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFF0D1B2A),
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // アバター
                Text(bubble.emoji, fontSize = 48.sp, textAlign = TextAlign.Center)
                // 名前
                Text(
                    text = bubble.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (bubble.isMe) Color(0xFFFFB3B3) else Color.White,
                    textAlign = TextAlign.Center
                )
                // 赤ちゃん月齢
                if (babyAge.isNotEmpty()) {
                    Text(
                        text = "👶 $babyAge",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                // 一言コメント（吹き出し風）
                if (bubble.comment.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White.copy(alpha = 0.12f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "💬 ${bubble.comment}",
                            fontSize = 14.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("とじる", color = Color(0xFFFF9999))
            }
        }
    )
}

private fun calcBabyAge(birthdate: String): String {
    if (birthdate.isBlank()) return ""
    return try {
        val parts = birthdate.split("-")
        if (parts.size < 2) return ""
        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val cal = java.util.Calendar.getInstance()
        val nowYear = cal.get(java.util.Calendar.YEAR)
        val nowMonth = cal.get(java.util.Calendar.MONTH) + 1
        val totalMonths = (nowYear - birthYear) * 12 + (nowMonth - birthMonth)
        when {
            totalMonths < 0 -> ""
            totalMonths < 12 -> "${totalMonths}ヶ月"
            else -> "${totalMonths / 12}歳${totalMonths % 12}ヶ月"
        }
    } catch (e: Exception) { "" }
}

// ──────────────────────────────────────────────
// シャボン玉 Canvas描画
// ──────────────────────────────────────────────

@Composable
private fun SoapBubbleCanvas(modifier: Modifier, isMe: Boolean) {
    Canvas(modifier = modifier) {
        val r = size.minDimension / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val center = Offset(cx, cy)

        // 虹色リム（スペクトル順）
        val iridColors = if (isMe) listOf(
            Color(0x50FF9999), Color(0x50FFCCAA), Color(0x50FFEEAA),
            Color(0x50FFAACC), Color(0x50FF88BB), Color(0x50FF9999),
        ) else listOf(
            Color(0x4899DDFF), Color(0x48AAFFDD), Color(0x48DDFFAA),
            Color(0x48FFEEAA), Color(0x48FFAACC), Color(0x48CC99FF),
        )
        val arcSweep = 360f / iridColors.size
        iridColors.forEachIndexed { i, color ->
            drawArc(
                color = color,
                startAngle = i * arcSweep - 30f,
                sweepAngle = arcSweep + 8f,
                useCenter = false,
                topLeft = Offset(cx - r + 2f, cy - r + 2f),
                size = Size((r - 2f) * 2f, (r - 2f) * 2f),
                style = Stroke(width = r * 0.13f)
            )
        }

        // 球体感の内部グラデーション（中央薄く、端でわずかに着色）
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.0f to Color(0x00FFFFFF),
                    0.55f to Color(0x06B8DFFF),
                    0.80f to Color(0x18A8D0FF),
                    1.0f to Color(0x30A0C8FF),
                ),
                center = center,
                radius = r
            ),
            radius = r,
            center = center
        )

        // 外縁の白い輪郭
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0x90FFFFFF),
                    Color(0x40FFFFFF),
                    Color(0x80FFFFFF),
                    Color(0x30FFFFFF),
                    Color(0x90FFFFFF),
                ),
                center = center
            ),
            radius = r - r * 0.04f,
            center = center,
            style = Stroke(width = r * 0.05f)
        )

        // メインハイライト（大きな白い楕円・左上）
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xCCFFFFFF), Color(0x00FFFFFF)),
                center = Offset(cx - r * 0.28f, cy - r * 0.55f),
                radius = r * 0.38f
            ),
            topLeft = Offset(cx - r * 0.56f, cy - r * 0.78f),
            size = Size(r * 0.55f, r * 0.33f)
        )

        // サブハイライト（小さな白点・右上）
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x99FFFFFF), Color(0x00FFFFFF)),
                center = Offset(cx + r * 0.38f, cy - r * 0.45f),
                radius = r * 0.12f
            ),
            radius = r * 0.12f,
            center = Offset(cx + r * 0.38f, cy - r * 0.45f)
        )

        // 底面の微かな反射
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x25FFFFFF), Color(0x00FFFFFF)),
                center = Offset(cx, cy + r * 0.65f),
                radius = r * 0.3f
            ),
            topLeft = Offset(cx - r * 0.3f, cy + r * 0.50f),
            size = Size(r * 0.60f, r * 0.22f)
        )
    }
}
