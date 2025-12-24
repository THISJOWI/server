#!/bin/bash
set -e
echo "Applying Kubernetes Secret..."
kubectl apply -f ../k8s/utils/secret.example.yaml