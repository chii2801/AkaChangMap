package com.example.akachanmap.data.repository

import com.example.akachanmap.data.model.BabyGender
import com.example.akachanmap.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = db.collection("users")

    private val uid get() = auth.currentUser?.uid ?: ""

    suspend fun getProfile(): UserProfile? {
        if (uid.isEmpty()) return null
        val doc = collection.document(uid).get().await()
        if (!doc.exists()) return null
        return UserProfile(
            uid = uid,
            nickname = doc.getString("nickname") ?: "",
            babyGender = BabyGender.values().firstOrNull {
                it.name == doc.getString("babyGender")
            } ?: BabyGender.SECRET,
            babyBirthdate = doc.getString("babyBirthdate") ?: "",
            avatarEmoji = doc.getString("avatarEmoji") ?: "👩",
            prefecture = doc.getString("prefecture") ?: ""
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        if (uid.isEmpty()) return
        collection.document(uid).set(
            mapOf(
                "nickname" to profile.nickname,
                "babyGender" to profile.babyGender.name,
                "babyBirthdate" to profile.babyBirthdate,
                "avatarEmoji" to profile.avatarEmoji,
                "prefecture" to profile.prefecture
            )
        ).await()
    }
}
