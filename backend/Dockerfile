# ====================================================
# CampusLink Backend — Multi-stage Dockerfile
# Stage 1: Build avec Maven
# Stage 2: Runtime JRE 17 slim
# ====================================================

# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copier uniquement pom.xml d'abord (cache Maven)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copier les sources et compiler
COPY src ./src
COPY sujet9 ./sujet9
RUN mvn clean package -DskipTests -q

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Utilisateur non-root
RUN addgroup -S campuslink && adduser -S campuslink -G campuslink
USER campuslink

COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/sujet9 ./sujet9

EXPOSE 8080

ENTRYPOINT ["java", "-jar", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.jpa.open-in-view=false", \
  "app.jar"]
