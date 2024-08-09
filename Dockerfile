FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the source code
COPY . .

# Build the application
RUN ./gradlew build -x test

# Run the application
CMD ["./gradlew", "bootRun"]