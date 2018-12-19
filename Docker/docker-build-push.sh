#!/usr/bin/env bash

./gradlew clean build
docker build -f Docker/Dockerfile --no-cache -t garystafford/storefront-orders:gke-2.0.0 .
docker push garystafford/storefront-orders:gke-2.0.0

# docker run --name storefront-orders -d garystafford/storefront-orders:gke-2.0.0