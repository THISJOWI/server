# syntax=docker/dockerfile:1

# Comments are provided throughout this file to help you get started.
# If you need more help, visit the Dockerfile reference guide at
# https://docs.docker.com/go/dockerfile-reference/

# Want to help us make this template better? Share your feedback here: https://forms.gle/ybq9Krt8jtBL3iCk7

################################################################################

# Create a stage for resolving and downloading dependencies.
FROM eclipse-temurin:21-jdk-jammy AS deps

WORKDIR /build

# Copy the gradlew wrapper with executable permissions.
COPY --chmod=0755 gradlew gradlew
COPY gradle/ gradle/

# Copy the build configuration files
COPY build.gradle.kts settings.gradle.kts ./

# Download dependencies as a separate step to take advantage of Docker's caching.
# Leverage a cache mount to /root/.gradle so that subsequent builds don't have to
# re-download packages.
RUN --mount=type=cache,target=/root/.gradle ./gradlew dependencies --no-daemon

################################################################################

# Create a stage for building the application based on the stage with downloaded dependencies.
FROM deps AS package

WORKDIR /build

COPY ./src src/
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew build -x test --no-daemon && \
    find build/libs -name "*.jar" -not -name "*-plain.jar" -exec mv {} app.jar \;

################################################################################

# Create a new stage for running the application that contains the minimal
# runtime dependencies for the application.
FROM eclipse-temurin:21-jre-jammy AS final

# Create a non-privileged user that the app will run under.
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/home/appuser" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

# Copy the executable from the "package" stage.
COPY --from=package /build/app.jar app.jar

EXPOSE 8082

ENTRYPOINT [ "java", "-jar", "app.jar" ]
