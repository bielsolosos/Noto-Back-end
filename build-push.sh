#!/bin/bash

set -e

IMAGE_TAG="bielsolosos/noto-api:latest"

echo "============================================="
echo "Building Docker Image: $IMAGE_TAG"
echo "============================================="

docker build -t "$IMAGE_TAG" .

echo "============================================="
echo "Build complete! Pushing image to registry..."
echo "============================================="

docker push "$IMAGE_TAG"

echo "============================================="
echo "Successfully built and pushed $IMAGE_TAG!"
echo "============================================="
