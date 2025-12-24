#!/bin/bash
set -e
echo "Applying Kubernetes Ingress Controller..."
kubectl apply -f ../k8s/utils/ingress-controller.yaml
sleep 15