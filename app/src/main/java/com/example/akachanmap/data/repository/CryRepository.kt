package com.example.akachanmap.data.repository

import com.example.akachanmap.data.model.CryPost
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
                            babyBirthdate = doc.getString("babyBirthdate") ?: ""
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

    suspend fun addReaction(postId: String, reactionKey: String) {
        val doc = collection.document(postId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(doc)
            @Suppress("UNCHECKED_CAST")
            val current = (snapshot.get("reactions") as? Map<String, Long>) ?: emptyMap()
            val updated = current.toMutableMap()
            updated[reactionKey] = (updated[reactionKey] ?: 0) + 1
            transaction.update(doc, "reactions", updated)
        }.await()
    }

    suspend fun markStopped(postId: String) {
        collection.document(postId).update("stopped", true).await()
    }

    fun getStats(): Flow<DailyStats> = callbackFlow {
        val listener = collection
            .whereGreaterThan("expiresAt", Timestamp.now())
            .addSnapshotListener { snapshot, _ ->
                val docs = snapshot?.documents ?: emptyList()
                val total = docs.size
                val byPref = docs.groupBy { it.getString("prefecture") ?: "" }
                val topPref = byPref.maxByOrNull { it.value.size }
                val nightCount = docs.count {
                    val hour = it.getTimestamp("timestamp")
                        ?.toDate()?.hours ?: 0
                    hour in 0..5
                }
                trySend(DailyStats(total, topPref?.key ?: "", topPref?.value?.size ?: 0, nightCount))
            }
        awaitClose { listener.remove() }
    }
}

data class DailyStats(
    val total: Int,
    val topPrefName: String,
    val topPrefCount: Int,
    val nightCount: Int
)
