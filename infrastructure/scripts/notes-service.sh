#!/bin/bash
set -e
echo "Applying Kubernetes Notes Service..."
kubectl apply -f ../k8s/application/service/notes/deploy.yaml
kubectl apply -f ../k8s/application/service/notes/service.yaml