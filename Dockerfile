# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -DskipTests package


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar

# Optimized JVM flags for Cloud Run + Spring Boot
ENV JAVA_TOOL_OPTIONS="\
-XX:+UseG1GC \
-XX:MaxRAMPercentage=75.0 \
-XX:InitialRAMPercentage=50.0 \
-Xss256k \
-Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
