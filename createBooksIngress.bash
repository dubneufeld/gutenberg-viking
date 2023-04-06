#!/bin/bash

# first delete all books
HOST="minikube.gutenberg"
PORT=443

curl -X DELETE -s -k https://$HOST:$PORT/book/deleteAllBooks -H 'Content-Type: application/json'

# create a single book

body="{\"bookId\" : 1"
body+=',"slug" : "mess-harefaq-1542",
 	"title" : "Messaging with HareFAQ",
  	"publisher" : "Gutenberg",
     	"authors" : [
        	"Paul Bunyan"	
      	],
    	"description" : "A new enterprise messaging implementation.",
    	"price" : 2339,
    	"categoryId" : 4,
    	"tags" : [
        	"java",
        	"spring",
        	"messaging"
    	]}'
    
  
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


name="O'Rourke"  

body='{"bookId":2,"slug":"malware-begin-666","title":"Malware for Beginners","publisher":'
body+=\"$name\"
body+=',"authors":["Marc Dutroux","George Besse"],"description":"How to crash your entreprise servers.","price":3339,"categoryId":4,"tags":["malware","system","blackhat"]'
body+='}'


echo " "
echo " " 
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


body='{"bookId":3,"slug":"emerald-ultimate-421","title":"The Ultimate Emerald Reference","publisher":'
body+=\"$name\"
body+=',"authors":["Nivü Nikkonü"],"description":"Much easier to master and more efficient than Ruby","price":3539,"categoryId":4,"tags":["software","web","database"]'
body+='}'

echo " "
echo " " 
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


body="{\"bookId\":4"
body+=',"slug" : "donkey-action-1234",
        "title" : "Donkey in Action",
        "publisher" : "Chebyshev",
        "authors" : [
        	"Grace Deveneux"
        ],
    	"description" : "An introduction to the newest microservice framework.",
    	"price" : 3839,
    	"categoryId" : 4,
    	"tags" : [
        	"microservice",
        	"web",
        	"restful"
    	]}'
      

echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"



body="{\"bookId\":5"
body+=',"slug" : "tangodb-depth-5678",
    	"title" : "TangoDB in Depth",
    	"publisher" : "Notch",
    	"authors" : [
        	"Norman Bates"
    	],
    	"description" : "A comprehensive introduction to the latest schemaless database.",
    	"price" : 4339,
    	"categoryId" : 4,
    	"tags" : [
        	"database",
        	"schemaless",
        	"nosql"
    	]}'  


echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


body="{\"bookId\":6"
body+=',"slug" : "jvonneumann-1945",
    	"title" : "The Incredible Life of John von Neumann",
    	"publisher" : "Grouble",
    	"authors" : [
        	"Albert Schweitzer"
    	],
    	"description" : "A founding father of computer science.",
    	"price" : 4439,
    	"categoryId" : 3,
    	"tags" : [
        	"computer",
        	"system",
        	"mathematics"
    	]}'
    	
echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"

body="{\"bookId\":7"
body+=',"slug" : "heisenberg-1923",
    	"title" : "Heisenberg, a Life in Uncertainty",
    	"publisher" : "Grouble",
    	"authors" : [
        	"Isabel Spengler"
    	],
    	"description" : "A founding father of quantum physics. His entire life he had to cope with uncertainty and most probably was not awarded the Nobel prize.",
    	"price" : 4539,
    	"categoryId" : 3,
    	"tags" : [
        	"biography",
        	"science",
        	"history"
    ]}'
    
echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


body="{\"bookId\":8"    
body+=',"slug" : "jsmouche-1900",
    	"title" : "Jean-Sebastien Mouche, from Paris with Love",
    	"publisher" : "Grouble",
    	"authors" : [
        	"André Malraux"
    	],
    	"description" : "He created the popular Bateaux-Mouche where visitors from around the world can enjoy a romantic dinner on the river Seine.",
    	"price" : 2939,
    	"categoryId" : 3,
    	"tags" : [
        	"biography",
        	"science",
        	"history"
    	]}'
    	   
echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


body="{\"bookId\":9"    
body+=',"slug" : "marbront-1902",
    	"title" : "Eleanor Brontë and the Blank Page Challenge",
    	"publisher" : "Spivakov",
    	"authors" : [
        	"Hu Xiao-Mei"
    	],
    	"description" : "The only Brontë sister who never wrote anything.",
    	"price" : 2739,
    	"categoryId" : 3,
    	"tags" : [
        	"biography",
        	"literature",
        	"women"
    	]}'

echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"

description="Everybody has heard of him, now it's time to read about his true story."

body="{\"bookId\":10"   
body+=',"slug" : "nostradamus-42",
    	"title" : "Nostradamus",
    	"publisher" : "Springfield",
    	"authors" : [
        	"Helmut von Staubsauger"
    	],
    	"description": ' 
body+=\"$description\"   	
body+=',"price" : 4439,
    	"categoryId" : 3,
   	"tags" : [
        	"biography",
        	"literature",
        	"medicine"
    	]}'

  
echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


body="{\"bookId\":11"  
body+=',"slug" : "bourne-shell-1542",
    	"title" : "The Bourne Shell Legacy",
    	"publisher" : "MacNamara",
    	"authors" : [
        	"Robert Bedlam"
    	],
    	"description" : "A nail-biting thriller featuring JSON Bourne.",
    	"price" : 4539,
    	"categoryId" : 5,
    	"tags" : [
        	"thriller",
        	"crime",
        	"spying"
    	]}'


echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


body="{\"bookId\":12"  
body+=',"slug" : "raiders-pattern-3190",
    	"title" : "Raiders of the Lost Pattern",
    	"publisher" : "Atkinson-Wembley",
    	"authors" : [
        	"Evert Edepamuur"
    	],
    	"description" : "Two geeks on the track of an elusive pattern that escaped the attention of the Gang of Four.",
    	"price" : 3639,
    	"categoryId" : 5,
    	"tags" : [
        	"thriller",
        	"crime",
        	"software"
    	]}'

echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"


body="{\"bookId\":13"  
body+=',"slug" : "dining-philosophers-1542",
    	"title" : "The Dining Philosophers",
    	"publisher" : "Dyson",
    	"authors" : [
        	"Paul Enclume"
    	],
    	"description" : "Five philosophers decide to have a dinner together. They have to cope with a lack of forks and knives.",
    	"price" : 3839,
    	"categoryId" : 5,
    	"tags" : [
        	"home",
        	"life",
        	"food"
    ]}'
    
echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"

  
body="{\"bookId\":14"    
body+=',"slug" : "walking-planck-3141",
    	"title" : "Walking the Planck Constant",
    	"publisher" : "Hanning",
    	"authors" : [
        	"Laetitia Haddad"
    	],
    	"description" : "A Caribbean pirate captain falls into a quantum entanglement. Only the Schroedinger Cat can rescue him. Is he dead or alive?",
    	"price" : 5339,
    	"categoryId" : 5,
    	"tags" : [
        	"piracy",
        	"science-fiction",
        	"gold"
    	]}'
    	
echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"

   
body="{\"bookId\":15"        	
body+=',"slug" : "apes-wrath-4153",
    	"title" : "Apes of Wrath",
    	"publisher" : "Butterworth",
    	"authors" : [
        	"Boris Cyrulnik"
    	],
    	"description" : "A gorilla keeper in San Diego Zoo struggles to keep his job during the Great Depression.",
    	"price" : 6839,
    	"categoryId" : 5,
    	"tags" : [
        	"apes",
        	"life",
        	"depression"
    	]}'
    	
echo " "
echo " " 
echo $body | jq -r .    

curl -X POST -s -k https://$HOST:$PORT/book/book -H "Content-Type: application/json" --data "$body"

    	
    	
    	
