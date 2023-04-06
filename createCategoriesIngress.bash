#!/bin/bash

HOST="minikube.gutenberg"
PORT=443

# delete all categories

curl -X DELETE -s -k https://$HOST:$PORT/book/deleteAllCategories -H 'Content-Type: application/json'

# create a single category

body="{\"categoryId\" : 1"
body+=',"slug": "books",
        "name": "Books",
        "description" : "All books",
        "children" : [2, 3, 5
	]}'        


echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/book/category -H "Content-Type: application/json" --data "$body"


body="{\"categoryId\" : 2"
body+=',"slug" : "textbooks",
        "name" : "Textbooks",
        "description" : "Textbooks for professionals",
        "parentId" : 1,
        "children" : [
        	4
    	],
        "ancestors" : [
        	{
            		"name" : "Books",
            		"categoryId" : 1,
            		"slug" : "books"
        	}
    	]}'


echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/book/category -H "Content-Type: application/json" --data "$body"


body="{\"categoryId\" : 3"
body+=',"slug" : "biographies",
        "name" : "Biographies",
        "description" : "All about the life of famous people",
        "parentId" : 1,
        "children" : [ ],
        "ancestors" : [
        	{
            		"name" : "Books",
            		"categoryId" : 1,
            		"slug" : "books"
        	}
    	]}'


echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/book/category -H "Content-Type: application/json" --data "$body"



body="{\"categoryId\" : 4"
body+=',"slug" : "computer-science",
        "name" : "Computer science",
        "description" : "Latest trends in computer science",
        "parentId" : 2,
        "children" : [ ],
        "ancestors" : [
        	{
            		"name" : "Textbooks",
            		"categoryId" : 2,
            		"slug" : "textbooks"
        	},
        	{
            		"name" : "Books",
            		"categoryId" : 1,
            		"slug" : "books"
        	}
    	]}'
   
echo $body | jq -r .


curl -X POST -s -k https://$HOST:$PORT/book/category -H "Content-Type: application/json" --data "$body"



body="{\"categoryId\" : 5"
body+=',"slug" : "fiction",
    	"name" : "Fiction",
    	"description" : "Most popular novels.",
    	"parentId" : 1,
    	"children" : [ ],
    	"ancestors" : [
        	{
            		"name" : "Books",
            		"categoryId" : 1,
            		"slug" : "books"
        	}
    	]}' 
    	
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/book/category -H "Content-Type: application/json" --data "$body"
    	
