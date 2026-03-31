#!/bin/bash

set -e

CONTAINER_NAME="time-deposit-postgres"
DB_NAME="time_deposit"
DB_USER="postgres"
DB_PASSWORD="postgres"

echo "🐘 Starting PostgreSQL container..."

# Check if container is already running
if docker ps | grep -q "$CONTAINER_NAME"; then
    echo "✓ PostgreSQL container already running"
else
    # Check if container exists but is stopped
    if docker ps -a | grep -q "$CONTAINER_NAME"; then
        echo "Starting existing container..."
        docker start "$CONTAINER_NAME"
    else
        echo "Creating new container..."
        docker run --name "$CONTAINER_NAME" \
          -e POSTGRES_USER="$DB_USER" \
          -e POSTGRES_PASSWORD="$DB_PASSWORD" \
          -e POSTGRES_DB="$DB_NAME" \
          -p 5432:5432 \
          -d postgres:16-alpine
    fi
    
    # Wait for database to be ready
    echo "Waiting for database to be ready..."
    sleep 3
    for i in {1..30}; do
        if docker exec "$CONTAINER_NAME" pg_isready -U "$DB_USER" &>/dev/null; then
            echo "✓ Database is ready"
            break
        fi
        if [ $i -eq 30 ]; then
            echo "✗ Database failed to start"
            exit 1
        fi
        sleep 1
    done
fi

echo ""
echo "🗄️  Running Flyway migrations..."
mvn clean flyway:migrate

echo ""
echo "🚀 Building and starting application..."
mvn spring-boot:run

echo ""
echo "✓ Application is running!"
echo "📍 API: http://localhost:8080"
echo "📚 Swagger UI: http://localhost:8080/swagger-ui.html"

