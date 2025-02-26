#!/bin/bash

# Navigate to the root directory of the repository
cd "$(dirname "$0")/.."

# Build the JAR file for the client application
./gradlew buildClientJar

# Run the client application
./gradlew runClient

echo "Client application has been built and run."