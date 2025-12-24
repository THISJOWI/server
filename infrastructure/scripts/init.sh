#!/bin/bash
set -e

# -- SECRETS --
echo "Applying Kubernetes Secret..."
kubectl apply -f ../k8s/utils/secret.example.yaml

# -- INGRESS CONTROLLER --
echo "Applying Kubernetes Ingress Controller..."
kubectl apply -f ../k8s/utils/ingress-controller.yaml
sleep 15

# -- KAFKA --
echo "Applying Kubernetes Kafka..."
kubectl apply -f ../k8s/utils/kafka.yaml
sleep 30

# -- DATABASES --
echo "Applying Kubernetes Databases..."
kubectl apply -f ../k8s/databases/cockroachdb/statefulset.yaml
kubectl apply -f ../k8s/databases/cockroachdb/node.yaml
kubectl apply -f ../k8s/databases/cockroachdb/cluster.yaml
kubectl apply -f ../k8s/databases/mongodb/job.yaml
sleep 30

kubectl apply -f ../k8s/databases/keys-db/config.yaml
kubectl apply -f ../k8s/databases/keys-db/persistent.yaml
kubectl apply -f ../k8s/databases/keys-db/deploy.yaml
kubectl apply -f ../k8s/databases/keys-db/service.yaml
sleep 30

# -- CONFIG SERVER --
echo "Applying Kubernetes Config Server..."
kubectl apply -f ../k8s/application/server/config/deploy.yaml
kubectl apply -f ../k8s/application/server/config/service.yaml

# -- CONFIG SERVER WAIT --
kubectl wait --for=condition=available deploy/config-server --timeout=300s

# -- DISCOVERY SERVER --
echo "Applying Kubernetes Discovery Server..."
kubectl apply -f ../k8s/application/server/discovery/deploy.yaml
kubectl apply -f ../k8s/application/server/discovery/node.yaml
kubectl apply -f ../k8s/application/server/discovery/cluster.yaml

# -- DISCOVERY SERVER WAIT --
kubectl wait --for=condition=available deploy/eureka-server --timeout=300s

# -- API GATEWAY --
echo "Applying Kubernetes API Gateway..."
kubectl apply -f ../k8s/application/server/api/deploy.yaml
kubectl apply -f ../k8s/application/server/api/node.yaml
kubectl apply -f ../k8s/application/server/api/cluster.yaml

# -- AUTHENTICATION SERVICE --
echo "Applying Kubernetes Authentication Service..."
kubectl apply -f ../k8s/application/service/auth/deploy.yaml
kubectl apply -f ../k8s/application/service/auth/service.yaml

# -- NOTES SERVICE --
echo "Applying Kubernetes Notes Service..."
kubectl apply -f ../k8s/application/service/notes/deploy.yaml
kubectl apply -f ../k8s/application/service/notes/service.yaml

# -- PASSWORD SERVICE --
echo "Applying Kubernetes Password Service..."
kubectl apply -f ../k8s/application/service/password/deploy.yaml
kubectl apply -f ../k8s/application/service/password/service.yaml

# -- OTP SERVICE --
echo "Applying Kubernetes OTP Service..."
kubectl apply -f ../k8s/application/service/otp/deploy.yaml
kubectl apply -f ../k8s/application/service/otp/service.yaml

