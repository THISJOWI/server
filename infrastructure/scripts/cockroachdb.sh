#!/bin/bash
set -e
echo "Applying Kubernetes Databases..."
kubectl apply -f ../k8s/databases/cockroachdb/statefulset.yaml
kubectl apply -f ../k8s/databases/cockroachdb/node.yaml
kubectl apply -f ../k8s/databases/cockroachdb/cluster.yaml
kubectl apply -f ../k8s/databases/mongodb/job.yaml
sleep 30