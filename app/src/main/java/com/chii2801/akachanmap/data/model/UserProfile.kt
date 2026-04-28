package com.chii2801.akachanmap.data.model

data class UserProfile(
    val uid: String = "",
    val nickname: String = "",
    val babyGender: BabyGender = BabyGender.SECRET,
    val babyBirthdate: String = "",
    val avatarEmoji: String = "👩",
    val prefecture: String = ""
)

enum class BabyGender(val label: String) {
    BOY("男の子"),
    GIRL("女の子"),
    SECRET("ひみつ")
}
