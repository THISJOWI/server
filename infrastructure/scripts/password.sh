#!/bin/bash
set -e
echo "Applying Kubernetes Password Service..."
kubectl apply -f ../k8s/application/service/password/deploy.yaml
kubectl apply -f ../k8s/application/service/password/service.yaml