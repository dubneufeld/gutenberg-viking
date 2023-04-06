#!/bin/bash


echo $(kubectl config current-context) 

kubectl config set-context $(kubectl config current-context) --namespace=gutenberg --create-namespace



