package com.chii2801.akachanmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chii2801.akachanmap.ui.about.AboutScreen
import com.chii2801.akachanmap.ui.account.AccountScreen
import com.chii2801.akachanmap.ui.map.HouseRoomScreen
import com.chii2801.akachanmap.ui.post.PostBottomSheet
import com.chii2801.akachanmap.viewmodel.AccountViewModel
import com.chii2801.akachanmap.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AkachanApp()
            }
        }
    }
}

@Composable
fun AkachanApp() {
    val viewModel: MapViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()
    val postError by viewModel.postError.collectAsState()
    val myPostId by viewModel.myPostId.collectAsState()

    val accountViewModel: AccountViewModel = viewModel()
    val userProfile by accountViewModel.profile.collectAsState()
    val profileLoaded by accountViewModel.profileLoaded.collectAsState()

    var showPostSheet by remember { mutableStateOf(false) }
    var showAccount by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    if (!profileLoaded) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFFF6B6B))
        }
        return
    }

    if (showAccount || userProfile?.nickname.isNullOrBlank()) {
        AccountScreen(onBack = { showAccount = false }, viewModel = accountViewModel)
        return
    }

    if (showAbout) {
        AboutScreen(onBack = { showAbout = false })
        return
    }

    HouseRoomScreen(
        posts = posts,
        stats = stats,
        myPostId = myPostId,
        myUid = viewModel.myUid,
        userAvatar = userProfile?.avatarEmoji?.ifBlank { "👩" } ?: "👩",
        userNickname = userProfile?.nickname ?: "",
        userPrefecture = userProfile?.prefecture ?: "",
        onPostClick = { showPostSheet = true },
        onStopCry = { myPostId?.let { viewModel.markStopped(it) } },
        onAccountClick = { showAccount = true },
        onAboutClick = { showAbout = true }
    )

    if (showPostSheet) {
        PostBottomSheet(
            onDismiss = { showPostSheet = false },
            onPost = { level, comment ->
                viewModel.postCry(
                    level, comment,
                    nickname = userProfile?.nickname ?: "",
                    babyBirthdate = userProfile?.babyBirthdate ?: "",
                    prefecture = userProfile?.prefecture ?: ""
                )
                showPostSheet = false
            },
            isPosting = isPosting
        )
    }

    postError?.let { error ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { androidx.compose.material3.Text("エラーなのだ😢") },
            text = { androidx.compose.material3.Text(error) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { viewModel.clearError() }) {
                    androidx.compose.material3.Text("OK")
                }
            }
        )
    }
}
