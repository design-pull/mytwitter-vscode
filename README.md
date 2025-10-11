# MyTwitter (Spring Boot × PostgreSQL × Render)

## 🌐 デプロイ先
[https://mytwitter-vscode.onrender.com/](https://mytwitter-vscode.onrender.com/)

---

## 📖 プロジェクト概要
このアプリケーションは **Spring Boot** を用いて構築した、シンプルな「つぶやき投稿」プラットフォームです。  
ユーザーは短いコメントを投稿し、一覧で確認できます。  
Render 上にデプロイし、**PostgreSQL** をバックエンドデータベースとして利用しています。

---

## 🛠️ 技術スタック
- **言語 / フレームワーク**: Java 21, Spring Boot 3.x  
- **ビルドツール**: Maven  
- **データベース**: PostgreSQL 17 (Render Managed DB)  
- **インフラ / デプロイ**: Docker, Render Web Service  
- **監視 / 運用**: Spring Boot Actuator (`/actuator/health`)  

---

## 🚀 デプロイ構成
- **Dockerfile**
  - マルチステージビルド（`eclipse-temurin:21-jdk` → `21-jre`）
  - `mvnw package` により JAR を生成し、軽量な JRE イメージで実行
- **環境変数（Render の Web Service に設定）**
  ```bash
  SPRING_PROFILES_ACTIVE=prod
  SPRING_DATASOURCE_URL=jdbc:postgresql://<DB_HOST>:5432/<DB_NAME>?sslmode=require
  SPRING_DATASOURCE_USERNAME=<DB_USER>
  SPRING_DATASOURCE_PASSWORD=<DB_PASSWORD>

- **Health Check Path**: `/actuator/health`  

---

## ⚠️ 注意事項
- 本サービスは Render の無料プラン上で稼働しているため、**停止するとデータはすべて削除されます**。  
- 永続的な利用を想定する場合は、外部ストレージや有料プランの利用が必要です。  

---

## 📌 今後の改善予定
- ユーザー認証機能（ログイン／ログアウト）  
- プロフィールページの拡充  
- Docker Compose によるローカル開発環境の整備  
- CI/CD パイプラインの導入  

==========
# MyTwitter (Spring Boot × PostgreSQL × Render)

## 🌐 Live Demo
[https://mytwitter-vscode.onrender.com/](https://mytwitter-vscode.onrender.com/)

---

## 📖 Overview
**MyTwitter** is a simple microblogging platform built with **Spring Boot**.  
Users can post short messages and view them in a timeline.  
The application is deployed on **Render** and uses **PostgreSQL** as the backend database.

---

## 🛠️ Tech Stack
- **Language / Framework**: Java 21, Spring Boot 3.x  
- **Build Tool**: Maven  
- **Database**: PostgreSQL 17 (Render Managed DB)  
- **Infrastructure / Deployment**: Docker, Render Web Service  
- **Monitoring**: Spring Boot Actuator (`/actuator/health`)  

---

## 🚀 Deployment Setup
- **Dockerfile**
  - Multi-stage build (`eclipse-temurin:21-jdk` → `21-jre`)  
  - Build JAR with `mvnw package` and run on a lightweight JRE image  
- **Environment Variables (set in Render Web Service)**
  ```bash
  SPRING_PROFILES_ACTIVE=prod
  SPRING_DATASOURCE_URL=jdbc:postgresql://<DB_HOST>:5432/<DB_NAME>?sslmode=require
  SPRING_DATASOURCE_USERNAME=<DB_USER>
  SPRING_DATASOURCE_PASSWORD=<DB_PASSWORD>
