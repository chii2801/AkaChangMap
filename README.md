# AkaChangMap（アカちゃんマップ）

赤ちゃんの泣き状況を日本地図上でリアルタイム共有するAndroidアプリ。

## 機能

- 都道府県ごとの泣き状況をマップで可視化
- 泣きレベル（ちょっと泣き / 号泣 / 超絶大号泣）を投稿
- 投稿は2時間後に自動削除
- 他のユーザーの投稿にリアクションを送れる
- 泣き止んだら「泣き止んだ！」ボタンで報告
- ニックネーム・赤ちゃん情報のアカウント設定

## 技術スタック

- **言語**: Kotlin
- **UI**: Jetpack Compose + Material3
- **バックエンド**: Firebase Firestore / Firebase Auth（匿名認証）
- **アーキテクチャ**: MVVM（ViewModel + StateFlow）

## プロジェクト構成

```
app/src/main/java/com/example/akachanmap/
├── MainActivity.kt                  # エントリポイント・ルーティング
├── data/
│   ├── model/
│   │   ├── CryPost.kt              # 投稿データモデル
│   │   ├── Prefecture.kt           # 都道府県・地方enum
│   │   └── UserProfile.kt          # ユーザープロフィールモデル
│   └── repository/
│       ├── CryRepository.kt        # 投稿のFirestore操作
│       └── UserRepository.kt       # ユーザー情報のFirestore操作
├── ui/
│   ├── account/
│   │   └── AccountScreen.kt        # アカウント設定画面
│   ├── components/
│   │   └── CryLevelSelector.kt     # 泣きレベル選択UI
│   ├── map/
│   │   ├── JapanMapScreen.kt       # メイン地図画面
│   │   └── PrefecturePostsSheet.kt # 都道府県別投稿一覧
│   ├── post/
│   │   └── PostBottomSheet.kt      # 投稿BottomSheet
│   └── setup/
│       └── PrefectureSetupScreen.kt # 初回都道府県設定
└── viewmodel/
    ├── AccountViewModel.kt          # アカウント画面ViewModel
    └── MapViewModel.kt              # 地図画面ViewModel
```

## Firestore コレクション構成

```
cry_posts/          # 泣き投稿（2時間TTL）
  - prefecture      # 都道府県名
  - cryLevel        # 泣きレベル (1-3)
  - comment         # 一言コメント
  - nickname        # 投稿者ニックネーム
  - babyBirthdate   # 赤ちゃん生年月日
  - reactions       # リアクション数
  - stopped         # 泣き止みフラグ
  - timestamp       # 投稿日時
  - expiresAt       # 有効期限（投稿+2時間）

users/              # ユーザープロフィール
  - nickname        # ニックネーム
  - babyGender      # 赤ちゃんの性別
  - babyBirthdate   # 赤ちゃん生年月日
  - avatarEmoji     # アバター絵文字
  - prefecture      # 都道府県
```

## セットアップ

1. Firebase プロジェクトを作成し `google-services.json` を `app/` に配置
2. Firestore セキュリティルールを設定（`docs/firestore.rules` 参照）
3. Android Studio で開いてビルド

## リリース

```bash
# リリースビルド生成
./gradlew bundleRelease

# 出力先
app/build/outputs/bundle/release/app-release.aab
```

## ドキュメント

- [プライバシーポリシー](https://chii2801.github.io/AkaChangMap/privacy-policy)
- [利用規約](https://chii2801.github.io/AkaChangMap/terms-of-service)

## バージョン履歴

### 1.0.0
- 初回リリース
- 日本地図での泣き状況表示
- 投稿・リアクション機能
- アカウント設定
