#!/bin/bash

HOST="minikube.gutenberg"
PORT=443 

curl -X DELETE -s -k https://$HOST:$PORT/review/deleteAllReviews

body="{\"userId\" : 3"
body+=',"bookId" : 1,
    "text" : "The most comprehensive source for everything HareFAQ.",
    "date" : "2022-06-10T15:15:00",
    "rating" : 5,
    "helpfulVotes" : 10,
    "voterIds" : [ ]
}'


echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 4"
body+=',"bookId" : 2,
    	"text" : "Malware for Beginners is a great primer on malware and what you can do to protect yourself and your organisation from it.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 4,
    	"helpfulVotes" : 9,
    	"voterIds" : [  ]
}'


echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 5"
body+=',"bookId" : 3,
    	"text" : "Nivü Nikkonü has assembled a comprehensive reference manual for Emerald. It is a treasure map that everyone will want to use.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 5,
    	"helpfulVotes" : 8,
    	"voterIds" : [ 3 ]
}'


echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 6"
body+=',"bookId" : 4,
    	"text" : "Donkey is fast becoming THE framework for microservices--This book shows you why and how.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 4,
    	"helpfulVotes" : 7,
    	"voterIds" : [ ]
}'


echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 7"
body+=',"bookId" : 5,
    	"text" : "A thorough manual for learning, practicing, and implementing TangoDB.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 5,
    	"helpfulVotes" : 6,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 3"
body+=',"bookId" : 6,
    "text" : "I recommend this book to all history of science majors.",
    "date" : "2022-06-10T15:15:00",
    "rating" : 3,
    "helpfulVotes" : 10,
    "voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


text="Thanks to this book I don't need sleeping pills anymore. It is so boring that I fall asleep after reading two pages."

body="{\"userId\" : 4"
body+=',"bookId" : 9, "text" :'
body+=\"$text\"
body+=',"date" : "2022-06-10T15:15:00",
	"rating" : 2,
    	"helpfulVotes" : 9,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 5"
body+=',"bookId" : 10,
    	"text" : "I was surprised to read that Nostradamus was a doctor. Moreover this book gives many interesting details about everyday life in France in XVIth century.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 4,
    	"helpfulVotes" : 8,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"

body="{\"userId\" : 6"
body+=',"bookId" : 7,
    	"text" : "This is the long awaited biography of the most controversial scientist of XXth century.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 5,
    	"helpfulVotes" : 7,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"

text="This book left me deeply frustrated. After thoroughly reading it twice, I still don't know which cat breed the Schroedinger Cat was!"

body="{\"userId\" : 5"
body+=',"bookId" : 7, "text" :'
body+=\"$text\"
body+=',"date" : "2022-06-10T15:15:00",
    	"rating" : 1,
    	"helpfulVotes" : 6,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 4"
body+=',"bookId" : 7,
    	"text" : "I was really surprised that Heisenberg chose such a desolate place as Helgoland for his spring break.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 2,
    	"helpfulVotes" : 10,
    	"voterIds" : [ ]
}'


echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 7"
body+=',"bookId" : 8,   
    	"text" : "I had a dinner in a Bateau-Mouche during my honeymoon in Paris a long time ago. Reading this book made me feel young again!",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 5,
    	"helpfulVotes" : 9,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"

body="{\"userId\" : 3"
body+=',"bookId" : 11,
    	"text" : "Bedlam at his best. This is really a page-turner.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 5,
    	"helpfulVotes" : 8,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"

body="{\"userId\" : 4"
body+=',"bookId" : 13,
    	"text" : "An incredible exploration of the effect of virtual reality on human behavior.",
   	"date" : "2022-06-10T15:15:00",
    	"rating" : 5,
    	"helpfulVotes" : 7,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"

body="{\"userId\" : 5"
body+=',"bookId" : 13,
    	"text" : "This book shows that philosophy is not an abstract concept. It is the very foundation of social interactions.",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 4,
    	"helpfulVotes" : 6,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 2"
body+=',"bookId" : 14,
    	"text" : "Really entertaining book. Now I hope that one of Hollywood Eight will adapt it into a movie!",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 5,
    	"helpfulVotes" : 10,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 7"
body+=',"bookId" : 15,
    	"text" : "Reading this book made me think of some novels by John Steinbeck. Here the gorillas are more human than some human characters!",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 3,
    	"helpfulVotes" : 9,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 6"
body+=',"bookId" : 15,
    	"text" : "After reading this book I feel like a privileged citizen!",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 4,
    	"helpfulVotes" : 8,
    	"voterIds" : [ ]
}'

echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 2"
body+=',"bookId" : 15,
    	"text" : "After reading this book I prefer gorillas to my neighbors!",
    	"date" : "2022-06-10T15:15:00",
    	"rating" : 5,
    	"helpfulVotes" : 7,
    	"voterIds" : [ ]
}'


echo " "
echo " "
echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/review/review -H "Content-Type: application/json" --data "$body"




