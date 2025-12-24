#!/bin/bash
set -e
echo "Applying Kubernetes API Gateway..."
kubectl apply -f ../k8s/application/server/api/deploy.yaml
kubectl apply -f ../k8s/application/server/api/node.yaml
kubectl apply -f ../k8s/application/server/api/cluster.yaml
