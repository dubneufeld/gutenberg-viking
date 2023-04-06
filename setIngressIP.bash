#!/bin/bash

INGRESS_IP=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

echo $INGRESS_IP

MINIKUBE_HOSTS="minikube.gutenberg health.minikube.gutenberg"

echo "$INGRESS_IP $MINIKUBE_HOSTS" | sudo tee -a /etc/hosts  
