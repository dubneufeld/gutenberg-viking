#!/bin/bash

helm install istio-gutenberg-addons \
  kubernetes/helm/environments/istio-system -n istio-system --wait
