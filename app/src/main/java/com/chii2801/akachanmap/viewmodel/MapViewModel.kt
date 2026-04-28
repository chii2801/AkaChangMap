package com.chii2801.akachanmap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chii2801.akachanmap.data.model.CryPost
import com.chii2801.akachanmap.data.repository.CryRepository
import com.chii2801.akachanmap.data.repository.DailyStats
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val repository = CryRepository()
    private val auth = FirebaseAuth.getInstance()

    val myUid: String? get() = auth.currentUser?.uid

    private val _posts = MutableStateFlow<List<CryPost>>(emptyList())
    val posts: StateFlow<List<CryPost>> = _posts.asStateFlow()

    // stats は posts から派生させることで、バブル数と必ず一致する
    val stats: StateFlow<DailyStats> = _posts.map { postList ->
        val active = postList.filter { !it.stopped }
        val byPref = active.groupBy { it.prefecture }
        val topPref = byPref.maxByOrNull { it.value.size }
        DailyStats(
            total = active.size,
            topPrefName = topPref?.key ?: "",
            topPrefCount = topPref?.value?.size ?: 0,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DailyStats(0, "", 0))

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting.asStateFlow()

    private val _postError = MutableStateFlow<String?>(null)
    val postError: StateFlow<String?> = _postError.asStateFlow()

    private val _myPostId = MutableStateFlow<String?>(null)
    val myPostId: StateFlow<String?> = _myPostId.asStateFlow()

    // 起動時の復元を一度だけ行うフラグ
    private var restoredOnFirstSnapshot = false

    private val authReady = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            try { repository.signInAnonymously() } catch (e: Exception) { }
            authReady.value = true
            repository.getActivePosts().collect { posts ->
                _posts.value = posts
                // 最初のスナップショットで前セッションの投稿を復元（新規クエリ不要）
                if (!restoredOnFirstSnapshot) {
                    restoredOnFirstSnapshot = true
                    val uid = auth.currentUser?.uid ?: ""
                    if (uid.isNotEmpty() && _myPostId.value == null) {
                        _myPostId.value = posts.firstOrNull { !it.stopped && it.userId == uid }?.id
                    }
                }
            }
        }
    }

    fun postCry(level: Int, comment: String, nickname: String = "", babyBirthdate: String = "", prefecture: String = "") {
        viewModelScope.launch {
            _isPosting.value = true
            try {
                // auth が完了してから投稿（uid が正しく保存されるように）
                authReady.first { it }
                val postId = repository.postCry(prefecture, level, comment, nickname, babyBirthdate)
                _myPostId.value = postId
            } catch (e: Exception) {
                _postError.value = "投稿に失敗したのだ: ${e.message}"
            } finally {
                _isPosting.value = false
            }
        }
    }

    fun markStopped(postId: String) {
        _myPostId.value = null
        // Firestoreの反映を待たずに即座にローカルのstatsに反映させる
        _posts.value = _posts.value.map { if (it.id == postId) it.copy(stopped = true) else it }
        viewModelScope.launch {
            try {
                repository.markStopped(postId)
            } catch (e: Exception) {
                _postError.value = "泣き止んだ更新エラー: ${e.message}"
            }
        }
    }

    fun clearError() { _postError.value = null }
}
