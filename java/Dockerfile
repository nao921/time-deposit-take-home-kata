FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Download dependencies during build to speed up container startup
RUN mvn dependency:go-offline -q

EXPOSE 8080

# Just run the app - Flyway migrations must be run manually or via compose service
ENTRYPOINT ["mvn", "spring-boot:run"]
