# Use official Maven image with Java 17
FROM maven:3.9.9-eclipse-temurin-17

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the Spring Boot project
RUN mvn clean package -DskipTests

# Render provides dynamic port
ENV PORT=8080

# Expose port
EXPOSE 8080

# Run the application
CMD ["sh", "-c", "java -jar target/*.jar --server.port=$PORT"]