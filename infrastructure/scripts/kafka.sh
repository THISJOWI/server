#!/bin/bash
set -e
echo "Applying Kubernetes Kafka..."
kubectl apply -f ../k8s/utils/kafka.yaml
sleep 30