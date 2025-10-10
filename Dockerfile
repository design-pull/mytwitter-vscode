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
ARG JAR=/workspace/target/*.jar
COPY --from=build ${JAR} /app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar /app.jar"]
