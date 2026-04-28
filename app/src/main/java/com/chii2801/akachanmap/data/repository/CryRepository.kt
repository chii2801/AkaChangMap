package com.chii2801.akachanmap.data.repository

import com.chii2801.akachanmap.data.model.CryPost
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class CryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = db.collection("cry_posts")

    suspend fun signInAnonymously() {
        if (auth.currentUser == null) {
            auth.signInAnonymously().await()
        }
    }

    fun getActivePosts(): Flow<List<CryPost>> = callbackFlow {
        val now = Timestamp.now()
        val listener = collection
            .whereGreaterThan("expiresAt", now)
            .addSnapshotListener { snapshot, _ ->
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        @Suppress("UNCHECKED_CAST")
                        CryPost(
                            id = doc.id,
                            prefecture = doc.getString("prefecture") ?: "",
                            cryLevel = doc.getLong("cryLevel")?.toInt() ?: 1,
                            comment = doc.getString("comment") ?: "",
                            reactions = (doc.get("reactions") as? Map<String, Long>)
                                ?.mapValues { it.value.toInt() } ?: emptyMap(),
                            stopped = doc.getBoolean("stopped") ?: false,
                            timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now(),
                            expiresAt = doc.getTimestamp("expiresAt") ?: Timestamp.now(),
                            nickname = doc.getString("nickname") ?: "",
                            babyBirthdate = doc.getString("babyBirthdate") ?: "",
                            userId = doc.getString("userId") ?: ""
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    suspend fun postCry(prefecture: String, level: Int, comment: String, nickname: String, babyBirthdate: String): String {
        val now = Timestamp.now()
        val expiresAt = Timestamp(Date(now.toDate().time + 2 * 60 * 60 * 1000))
        val doc = collection.add(
            mapOf(
                "prefecture" to prefecture,
                "cryLevel" to level,
                "comment" to comment,
                "reactions" to emptyMap<String, Int>(),
                "stopped" to false,
                "timestamp" to now,
                "expiresAt" to expiresAt,
                "userId" to (auth.currentUser?.uid ?: ""),
                "nickname" to nickname,
                "babyBirthdate" to babyBirthdate
            )
        ).await()
        return doc.id
    }

    suspend fun markStopped(postId: String) {
        collection.document(postId).update("stopped", true).await()
    }
}

data class DailyStats(
    val total: Int,
    val topPrefName: String,
    val topPrefCount: Int,
)
