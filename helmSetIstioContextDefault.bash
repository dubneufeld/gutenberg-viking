#!/bin/bash

echo $(kubectl config current-context) 

#kubectl delete namespace gutenberg 
kubectl apply -f kubernetes/gutenberg-namespace.yml
kubectl config set-context $(kubectl config current-context) #--namespace=gutenberg



