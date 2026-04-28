package com.chii2801.akachanmap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chii2801.akachanmap.data.model.BabyGender
import com.chii2801.akachanmap.data.model.UserProfile
import com.chii2801.akachanmap.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _profileLoaded = MutableStateFlow(false)
    val profileLoaded: StateFlow<Boolean> = _profileLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            _profile.value = repository.getProfile() ?: UserProfile()
            _profileLoaded.value = true
        }
    }

    fun saveProfile(nickname: String, gender: BabyGender, birthdate: String, avatar: String, prefecture: String) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val updated = UserProfile(
                    nickname = nickname,
                    babyGender = gender,
                    babyBirthdate = birthdate,
                    avatarEmoji = avatar,
                    prefecture = prefecture
                )
                repository.saveProfile(updated)
                _profile.value = updated
                _saveSuccess.value = true
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearSaveSuccess() { _saveSuccess.value = false }
}
