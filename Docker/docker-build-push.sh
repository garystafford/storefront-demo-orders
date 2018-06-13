#!/usr/bin/env bash

./gradlew clean build
docker build -f Docker/Dockerfile --no-cache -t garystafford/storefront-orders:latest .
docker push garystafford/storefront-orders:latest

# docker run --name storefront-orders -d garystafford/storefront-orders:latest