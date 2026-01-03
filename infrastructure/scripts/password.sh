#!/bin/bash
set -e
echo "Applying Kubernetes Password..."
kubectl apply -f ../k8s/application/password/deploy.yaml
kubectl apply -f ../k8s/application/password/service.yaml