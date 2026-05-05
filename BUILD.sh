#!/bin/bash
# Build script for SRUU Emergency Response System

set -e  # Exit on error

echo "=========================================="
echo "Building SRUU Emergency System"
echo "=========================================="
echo ""

# Check Java
echo "[1/3] Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Java not found. Please install Java 11 or higher."
    exit 1
fi
java_version=$(java -version 2>&1 | grep version)
echo "✓ Java found: $java_version"
echo ""

# Check Maven
echo "[2/3] Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven not found. Please install Maven 3.6 or higher."
    echo "Visit: https://maven.apache.org/download.cgi"
    exit 1
fi
maven_version=$(mvn --version | head -1)
echo "✓ Maven found: $maven_version"
echo ""

# Build
echo "[3/3] Compiling project..."
mvn clean package -DskipTests -q

if [ -f target/sruu-emergency-system-1.0.0.jar ]; then
    echo "✓ Build successful!"
    echo ""
    echo "JAR location: target/sruu-emergency-system-1.0.0.jar"
    echo "JAR size: $(du -h target/sruu-emergency-system-1.0.0.jar | cut -f1)"
    echo ""
    echo "To run the system:"
    echo "  java -jar target/sruu-emergency-system-1.0.0.jar"
else
    echo "ERROR: Build failed. JAR not found."
    exit 1
fi
