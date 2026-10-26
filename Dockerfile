# Recipe for a container image that runs the API. Two stages:
#   1. "build": a JDK image compiles the code and packages it into one jar.
#   2. the final image: a smaller JRE image (Java runtime only) that just runs that jar.
# The JDK, Maven and source code stay in stage 1, so the final image is smaller and
# contains only what's needed to run.

# ---- Stage 1: build the jar ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy the Maven wrapper and pom.xml first and download the dependencies. Docker caches
# each step, so as long as pom.xml doesn't change, rebuilding after a code change skips
# this slow download.
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

# Now the source code, then package it. Tests are skipped here: the only test starts the
# whole app and needs a database, which isn't available while building an image.
COPY src src
RUN ./mvnw -q package -DskipTests

# ---- Stage 2: the image that actually runs ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Run as an ordinary user, not root: if the app were ever compromised, the attacker
# couldn't change the container's system files.
RUN useradd --system --create-home appuser
USER appuser

# Take only the built jar from stage 1.
COPY --from=build /app/target/*.jar app.jar

# Let the JVM size its memory to the container's limit (small on free hosting plans)
# instead of assuming it owns the whole machine.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

# The port the app listens on (hosts like Render pass their own via $PORT; see
# server.port in application.properties).
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
