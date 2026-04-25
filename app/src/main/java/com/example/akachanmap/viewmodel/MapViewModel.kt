package com.example.akachanmap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akachanmap.data.model.CryPost
import com.example.akachanmap.data.model.Prefecture
import com.example.akachanmap.data.repository.CryRepository
import com.example.akachanmap.data.repository.DailyStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val repository = CryRepository()

    private val _posts = MutableStateFlow<List<CryPost>>(emptyList())
    val posts: StateFlow<List<CryPost>> = _posts.asStateFlow()

    private val _stats = MutableStateFlow(DailyStats(0, "", 0, 0))
    val stats: StateFlow<DailyStats> = _stats.asStateFlow()

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting.asStateFlow()

    private val _postError = MutableStateFlow<String?>(null)
    val postError: StateFlow<String?> = _postError.asStateFlow()

    private val _myPostId = MutableStateFlow<String?>(null)
    val myPostId: StateFlow<String?> = _myPostId.asStateFlow()

    init {
        viewModelScope.launch {
            try { repository.signInAnonymously() } catch (e: Exception) { }
            repository.getActivePosts().collect { _posts.value = it }
        }
        viewModelScope.launch {
            repository.getStats().collect { _stats.value = it }
        }
    }

    fun postCry(prefecture: Prefecture, level: Int, comment: String, nickname: String = "", babyBirthdate: String = "") {
        viewModelScope.launch {
            _isPosting.value = true
            try {
                val postId = repository.postCry(prefecture.name, level, comment, nickname, babyBirthdate)
                _myPostId.value = postId
            } catch (e: Exception) {
                _postError.value = "投稿に失敗したのだ: ${e.message}"
            } finally {
                _isPosting.value = false
            }
        }
    }

    fun addReaction(postId: String, reactionKey: String) {
        viewModelScope.launch {
            try { repository.addReaction(postId, reactionKey) } catch (e: Exception) { }
        }
    }

    fun markStopped(postId: String) {
        _myPostId.value = null
        viewModelScope.launch {
            try { repository.markStopped(postId) } catch (e: Exception) { }
        }
    }

    fun clearError() { _postError.value = null }
}
