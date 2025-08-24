FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
RUN mkdir -p /app/logs && chown -R 1001:1001 /app/logs
COPY --link pom.xml mvnw ./
COPY --link .mvn .mvn/
# Make sure the Maven wrapper is executable and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline dependency:resolve-plugins

COPY --link src ./src
RUN ./mvnw clean package -DskipTests -T1C

FROM gcr.io/distroless/java21-debian12 AS runtime
USER 1001
WORKDIR /app
COPY --from=build /app/logs /app/logs
COPY --from=build /app/target/*.war /app/app.war

# JVM options for container awareness and memory limits
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.war"]

LABEL maintainer="vne.vvm@gmail.com"