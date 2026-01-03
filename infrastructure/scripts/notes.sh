#!/bin/bash
set -e
echo "Applying Kubernetes Notes..."
kubectl apply -f ../k8s/application/notes/deploy.yaml
kubectl apply -f ../k8s/application/notes/service.yaml