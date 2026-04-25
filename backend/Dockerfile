# ====================================================
# CampusLink Backend — Dockerfile Simplifié
# Utilise le JAR déjà compilé sur l'hôte pour un build rapide
# ====================================================

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Utilisateur non-root
RUN addgroup -S campuslink && adduser -S campuslink -G campuslink
USER campuslink

# Copie du JAR compilé sur l'hôte
# Note : Il faut avoir lancé 'mvn package' avant le build Docker
COPY target/campuslink-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

# Health check simple
HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep UP || exit 1

ENTRYPOINT ["java", "-jar", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=prod", \
  "app.jar"]
