#!/bin/bash
set -e
echo "Applying Kubernetes Discovery Server..."
kubectl apply -f ../k8s/application/server/discovery/deploy.yaml
kubectl apply -f ../k8s/application/server/discovery/node.yaml
kubectl apply -f ../k8s/application/server/discovery/cluster.yaml