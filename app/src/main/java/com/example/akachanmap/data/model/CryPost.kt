package com.example.akachanmap.data.model

import com.google.firebase.Timestamp

data class CryPost(
    val id: String = "",
    val prefecture: String = "",
    val cryLevel: Int = 1,
    val comment: String = "",
    val reactions: Map<String, Int> = emptyMap(),
    val stopped: Boolean = false,
    val timestamp: Timestamp = Timestamp.now(),
    val expiresAt: Timestamp = Timestamp.now(),
    val nickname: String = "",
    val babyBirthdate: String = ""
)

enum class ReactionType(val emoji: String, val label: String) {
    OTSUKARESAMA("💪", "おつかれさま"),
    WAKARU("🤝", "わかるよ〜"),
    NIGHT("🌙", "夜中ファイト"),
    REST("☕", "休んでね"),
    GREAT("👑", "すごいよ！")
}

enum class CryLevel(val level: Int, val emoji: String, val label: String) {
    LITTLE(1, "😢", "ちょっと泣き"),
    CRYING(2, "😭", "号泣"),
    SUPER(3, "🔥", "超絶大号泣");

    companion object {
        fun fromInt(level: Int) = values().firstOrNull { it.level == level } ?: LITTLE
    }
}
