#!/bin/bash
set -e
echo "Applying Kubernetes Ingress Controller..."
kubectl apply -f ../k8s/utils/ingress.yaml
sleep 15