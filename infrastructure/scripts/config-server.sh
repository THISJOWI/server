#!/bin/bash
set -e
echo "Applying Kubernetes Config Server..."
kubectl apply -f ../k8s/application/server/config/deploy.yaml
kubectl apply -f ../k8s/application/server/config/service.yaml