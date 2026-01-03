#!/bin/bash
set -e

# -- SECRETS --
echo "Applying Kubernetes Secret..."
kubectl apply -f ../k8s/utils/secret.example.yaml

# -- INGRESS --
echo "Applying Kubernetes Ingress..."
kubectl apply -f ../k8s/utils/ingress.yaml
sleep 15

# -- KAFKA --
echo "Applying Kubernetes Kafka..."
kubectl apply -f ../k8s/utils/kafka.yaml
sleep 30

# -- DATABASES --
echo "Applying Kubernetes Databases..."
kubectl apply -f ../k8s/databases/cockroachdb/statefulset.yaml
kubectl apply -f ../k8s/databases/cockroachdb/cluster.yaml
sleep 30

kubectl apply -f ../k8s/databases/keys-db/config.yaml
kubectl apply -f ../k8s/databases/keys-db/persistent.yaml
kubectl apply -f ../k8s/databases/keys-db/deploy.yaml
kubectl apply -f ../k8s/databases/keys-db/service.yaml
sleep 30

# -- CONFIG SERVER --
echo "Applying Kubernetes Config..."
kubectl apply -f ../k8s/application/config/deploy.yaml
kubectl apply -f ../k8s/application/config/service.yaml

# -- CONFIG SERVER WAIT --
kubectl wait --for=condition=available deploy/config-server --timeout=300s

# -- AUTHENTICATION SERVICE --
echo "Applying Kubernetes Authentication..."
kubectl apply -f ../k8s/application/auth/deploy.yaml
kubectl apply -f ../k8s/application/auth/service.yaml

# -- NOTES SERVICE --
echo "Applying Kubernetes Notes..."
kubectl apply -f ../k8s/application/notes/deploy.yaml
kubectl apply -f ../k8s/application/notes/service.yaml

# -- PASSWORD SERVICE --
echo "Applying Kubernetes Password..."
kubectl apply -f ../k8s/application/password/deploy.yaml
kubectl apply -f ../k8s/application/password/service.yaml

# -- OTP SERVICE --
echo "Applying Kubernetes OTP..."
kubectl apply -f ../k8s/application/otp/deploy.yaml
kubectl apply -f ../k8s/application/otp/service.yaml
