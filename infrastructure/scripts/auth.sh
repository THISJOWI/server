#!/bin/bash
set -e
echo "Applying Kubernetes Authentication..."
kubectl apply -f ../k8s/application/auth/deploy.yaml
kubectl apply -f ../k8s/application/auth/service.yaml