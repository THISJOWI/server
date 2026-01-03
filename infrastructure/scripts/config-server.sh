#!/bin/bash
set -e
echo "Applying Kubernetes Config..."
kubectl apply -f ../k8s/application/config/deploy.yaml
kubectl apply -f ../k8s/application/config/service.yaml