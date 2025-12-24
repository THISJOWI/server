#!/bin/bash
set -e
echo "Applying Kubernetes KeyDB..."
kubectl apply -f ../k8s/databases/keys-db/config.yaml
kubectl apply -f ../k8s/databases/keys-db/persistent.yaml
kubectl apply -f ../k8s/databases/keys-db/deploy.yaml
kubectl apply -f ../k8s/databases/keys-db/service.yaml
sleep 30