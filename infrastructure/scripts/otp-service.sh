#!/bin/bash
set -e
echo "Applying Kubernetes OTP Service..."
kubectl apply -f ../k8s/application/service/otp/deploy.yaml
kubectl apply -f ../k8s/application/service/otp/service.yaml