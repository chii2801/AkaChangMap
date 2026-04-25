package com.example.akachanmap

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.akachanmap.data.model.Prefecture
import com.example.akachanmap.ui.account.AccountScreen
import com.example.akachanmap.ui.map.JapanMapScreen
import com.example.akachanmap.ui.map.PrefecturePostsSheet
import com.example.akachanmap.ui.post.PostBottomSheet
import com.example.akachanmap.ui.setup.PrefectureSetupScreen
import com.example.akachanmap.viewmodel.AccountViewModel
import com.example.akachanmap.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AkachanApp(context = this)
            }
        }
    }
}

@Composable
fun AkachanApp(context: Context) {
    val prefs = context.getSharedPreferences("akachan", Context.MODE_PRIVATE)
    var myPrefName by remember { mutableStateOf(prefs.getString("my_prefecture", null)) }
    val myPrefecture = myPrefName?.let { name ->
        Prefecture.values().firstOrNull { it.name == name }
    }

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
    var selectedPrefecture by remember { mutableStateOf<Prefecture?>(null) }

    // プロフィール読み込み中はスプラッシュ表示
    if (!profileLoaded) {
        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFFF6B6B))
        }
        return
    }

    // 初回起動 or ニックネーム未設定ならアカウント設定へ
    if (showAccount || userProfile?.nickname.isNullOrBlank()) {
        AccountScreen(onBack = { showAccount = false }, viewModel = accountViewModel)
        return
    }

    if (myPrefecture == null) {
        PrefectureSetupScreen(onDone = { pref ->
            prefs.edit().putString("my_prefecture", pref.name).apply()
            myPrefName = pref.name
        })
    } else {
        JapanMapScreen(
            posts = posts,
            stats = stats,
            myPrefecture = myPrefecture,
            onPrefectureClick = { selectedPrefecture = it },
            onPostClick = { showPostSheet = true },
            onAccountClick = { showAccount = true },
            userAvatar = userProfile?.avatarEmoji ?: "👩",
            userNickname = userProfile?.nickname ?: "",
            myPostId = myPostId,
            onStopCry = { myPostId?.let { viewModel.markStopped(it) } }
        )

        if (showPostSheet) {
            PostBottomSheet(
                onDismiss = { showPostSheet = false },
                onPost = { level, comment ->
                    viewModel.postCry(
                        myPrefecture, level, comment,
                        nickname = userProfile?.nickname ?: "",
                        babyBirthdate = userProfile?.babyBirthdate ?: ""
                    )
                    showPostSheet = false
                },
                isPosting = isPosting
            )
        }

        selectedPrefecture?.let { pref ->
            val prefPosts = posts.filter { it.prefecture == pref.name }
            PrefecturePostsSheet(
                prefecture = pref,
                posts = prefPosts,
                myPostId = myPostId,
                onReaction = { postId, key -> viewModel.addReaction(postId, key) },
                onStopped = { viewModel.markStopped(it) },
                onDismiss = { selectedPrefecture = null }
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
}
