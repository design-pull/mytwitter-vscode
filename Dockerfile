# Build stage
FROM gradle:8.6-jdk17 AS build
WORKDIR /workspace

# キャッシュしやすくするために先にラッパー/設定をコピーして依存を解決
COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts settings.gradle.kts
COPY settings.gradle settings.gradle
COPY build.gradle.kts build.gradle.kts
COPY build.gradle build.gradle
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon --version

# ソースをコピーしてビルド
COPY . .
RUN ./gradlew --no-daemon clean bootJar -x test -Pprod

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# 実行用 JAR をコピー（Gradle の出力先に合わせる）
COPY --from=build /workspace/build/libs/*.jar app.jar

# Render では環境変数 $PORT を使う。デフォルトは 8080。
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar /app/app.jar"]
