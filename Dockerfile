# Build stage (use Java 21)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x ./mvnw
RUN ./mvnw -q dependency:go-offline

COPY src src
RUN ./mvnw -DskipTests package -Pprod -q

# Runtime stage (Java 21 JRE)
FROM eclipse-temurin:21-jre
ARG JAR_FILE=/workspace/target/mytwitter-vscode-0.0.1-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} /app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar /app.jar"]
