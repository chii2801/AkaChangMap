package com.example.akachanmap.data.model

import androidx.compose.ui.graphics.Color

enum class Region(val label: String, val color: Color) {
    HOKKAIDO("北海道", Color(0xFF4CAF50)),
    TOHOKU("東北", Color(0xFF2196F3)),
    KANTO("関東", Color(0xFFFF5722)),
    CHUBU("中部", Color(0xFF9C27B0)),
    KINKI("近畿", Color(0xFFFF9800)),
    CHUGOKU("中国", Color(0xFF00BCD4)),
    SHIKOKU("四国", Color(0xFFE91E63)),
    KYUSHU("九州・沖縄", Color(0xFF795548))
}

enum class Prefecture(val label: String, val region: Region) {
    HOKKAIDO("北海道", Region.HOKKAIDO),

    AOMORI("青森", Region.TOHOKU),
    IWATE("岩手", Region.TOHOKU),
    MIYAGI("宮城", Region.TOHOKU),
    AKITA("秋田", Region.TOHOKU),
    YAMAGATA("山形", Region.TOHOKU),
    FUKUSHIMA("福島", Region.TOHOKU),

    IBARAKI("茨城", Region.KANTO),
    TOCHIGI("栃木", Region.KANTO),
    GUNMA("群馬", Region.KANTO),
    SAITAMA("埼玉", Region.KANTO),
    CHIBA("千葉", Region.KANTO),
    TOKYO("東京", Region.KANTO),
    KANAGAWA("神奈川", Region.KANTO),

    NIIGATA("新潟", Region.CHUBU),
    TOYAMA("富山", Region.CHUBU),
    ISHIKAWA("石川", Region.CHUBU),
    FUKUI("福井", Region.CHUBU),
    YAMANASHI("山梨", Region.CHUBU),
    NAGANO("長野", Region.CHUBU),
    GIFU("岐阜", Region.CHUBU),
    SHIZUOKA("静岡", Region.CHUBU),
    AICHI("愛知", Region.CHUBU),

    MIE("三重", Region.KINKI),
    SHIGA("滋賀", Region.KINKI),
    KYOTO("京都", Region.KINKI),
    OSAKA("大阪", Region.KINKI),
    HYOGO("兵庫", Region.KINKI),
    NARA("奈良", Region.KINKI),
    WAKAYAMA("和歌山", Region.KINKI),

    TOTTORI("鳥取", Region.CHUGOKU),
    SHIMANE("島根", Region.CHUGOKU),
    OKAYAMA("岡山", Region.CHUGOKU),
    HIROSHIMA("広島", Region.CHUGOKU),
    YAMAGUCHI("山口", Region.CHUGOKU),

    TOKUSHIMA("徳島", Region.SHIKOKU),
    KAGAWA("香川", Region.SHIKOKU),
    EHIME("愛媛", Region.SHIKOKU),
    KOCHI("高知", Region.SHIKOKU),

    FUKUOKA("福岡", Region.KYUSHU),
    SAGA("佐賀", Region.KYUSHU),
    NAGASAKI("長崎", Region.KYUSHU),
    KUMAMOTO("熊本", Region.KYUSHU),
    OITA("大分", Region.KYUSHU),
    MIYAZAKI("宮崎", Region.KYUSHU),
    KAGOSHIMA("鹿児島", Region.KYUSHU),
    OKINAWA("沖縄", Region.KYUSHU)
}
