#!/bin/bash
set -e
echo "Applying Kubernetes Authentication Service..."
kubectl apply -f ../k8s/application/service/auth/deploy.yaml
kubectl apply -f ../k8s/application/service/auth/service.yaml