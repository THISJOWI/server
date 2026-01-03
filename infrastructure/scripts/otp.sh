#!/bin/bash
set -e
echo "Applying Kubernetes OTP..."
kubectl apply -f ../k8s/application/otp/deploy.yaml
kubectl apply -f ../k8s/application/otp/service.yaml