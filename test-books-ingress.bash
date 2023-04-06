#!/bin/bash

# function declarations

PORT=443
HOST="minikube.gutenberg"
MGM_PORT=4004
MGM_HOST="health.minikube.gutenberg"

function assertCurl() {

    local expectedHttpCode=$1
    local curlCmd="$2 -w \"%{http_code}\""
    local result=$(eval $curlCmd)
    local httpCode="${result:(-3)}"
    RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

    if [ "$httpCode" = "$expectedHttpCode" ]
    then
        if [ "$httpCode" = "200" ]
        then
            echo "Test OK (HTTP Code: $httpCode)"
        else
            echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
        fi
        return 0
    else
        echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
        echo  "- Failing command: $curlCmd"
        echo  "- Response Body: $RESPONSE"
        exit 1
    fi
}


function assertEqual() {

    local expected=$1
    local actual=$2

    if [ "$actual" = "$expected" ]
    then
        echo "Test OK (actual value: $actual)"
        return 0
    else
        echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
        exit 1
    fi
}


function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 3
            echo -n ", retry #$n "
        fi
    done
    echo "DONE, continues..."
}

function testUrl() {
    url=$@
    if $url -ks -f -o /dev/null
    then
          return 0
    else
          return 1
    fi;
}


waitForService curl -s -k https://$MGM_HOST:$PORT/book/actuator/health

# clear books collection

#assertCurl 200 "curl -X DELETE -s -k https://$HOST:$PORT/book/deleteAllCategories"
#assertCurl 200 "curl -X DELETE -s -k https://$HOST:$PORT/book/deleteAllBooks"
 
# populate categories collection from Ingress endpoint
./createCategoriesIngress.bash
# populate books collection
./createBooksIngress.bash


echo ""
echo ""
echo "SATOR"

# GET a single category by categoryId
assertCurl 200 "curl -s -k https://$HOST:$PORT/book/category/categoryId/4"
assertEqual "computer-science" $(echo $RESPONSE | jq -r .slug)

# GET a single category by categorySlug
#curl -s http://$HOST:8081/category/slug/fiction

assertCurl 200 "curl -s -k https://$HOST:$PORT/book/category/slug/fiction"
assertEqual 5 $(echo $RESPONSE | jq -r .categoryId)

# category not found
assertCurl 404 "curl -s -k https://$HOST:$PORT/book/category/categoryId/42"
assertCurl 404 "curl -s -k https://$HOST:$PORT/book/category/slug/cuisine"

# exception handling
assertCurl 422 "curl -s -k https://$HOST:$PORT/book/category/categoryId/-1"
assertCurl 400 "curl -s -k https://$HOST:$PORT/book/category/categoryId/invalid"


echo ""
echo ""
echo "AREPO"

# GET a single book
assertCurl 200 "curl -s -k https://$HOST:$PORT/book/book/9"

#curl -s http://$HOST:8081/book/9
assertEqual "marbront-1902" $(echo $RESPONSE | jq -r .slug)

assertCurl 200 "curl -s -k https://$HOST:$PORT/book/book/slug/marbront-1902"

#curl -s http://$HOST:8081/book/9
assertEqual 9 $(echo $RESPONSE | jq -r .bookId)

# GET all books in a category
assertCurl 200 "curl -s -k https://$HOST:$PORT/book/allBooksByCategory/fiction/sortBy/title"
assertEqual 5 $(echo $RESPONSE | jq ". | length")

# GET all books in a category not found
assertCurl 200 "curl -s -k https://$HOST:$PORT/book/allBooksByCategory/cuisine/sortBy/title"
assertEqual 0 $(echo $RESPONSE | jq ". | length")

# book not found
assertCurl 404 "curl -s -k https://$HOST:$PORT/book/book/42"
assertCurl 404 "curl -s -k https://$HOST:$PORT/book/book/slug/cuisine"

# search requests

echo ""
echo ""
echo "MORBUS"

# search by title
body="{\"searchString\": \"Life\"}"

toto="curl -X POST -s -k https://$HOST:$PORT/book/searchByTitle -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'"

echo " "
echo " "
echo "by title"

#echo "$toto"
assertCurl 200 "$toto"
assertEqual 2 $(echo $RESPONSE | jq -r ". | length")
#assertEqual "Heisenberg, a Life in Uncertainty" "$(echo $RESPONSE | jq -r .[0].title)"
assertEqual "The Incredible Life of John von Neumann" "$(echo $RESPONSE | jq -r .[0].title)"


# search by description
body="{\"searchString\": \"pirate quantum life\"}"

toto="curl -X POST -s -k https://$HOST:$PORT/book/searchByDescription -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'"

echo " "
echo " "
echo "by description"

echo "$toto"
assertCurl 200 "$toto"
assertEqual 2 $(echo $RESPONSE | jq -r ". | length")
#assertEqual "The Incredible Life of John von Neumann" "$(echo $RESPONSE | jq -r .[0].title)"
assertEqual "Heisenberg, a Life in Uncertainty" "$(echo $RESPONSE | jq -r .[0].title)"


# search by tags
body="{\"searchString\": \"quantum computer piracy\"}"

toto="curl -X POST -s -k https://$HOST:$PORT/book/searchByTags -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'"

echo " "
echo " "
echo "by tags"

#echo "$toto"
assertCurl 200 "$toto"
assertEqual 2 $(echo $RESPONSE | jq -r ". | length")
assertEqual "The Incredible Life of John von Neumann" "$(echo $RESPONSE | jq -r .[0].title)"



echo ""
echo ""
echo "TENET"

# exception handling
assertCurl 422 "curl -s -k https://$HOST:$PORT/book/book/-1"
assertCurl 400 "curl -s -k https://$HOST:$PORT/book/book/invalid"

echo ""
echo ""
echo "OPERA"

body='{"bookIds":[7,12,13]}'
toto="curl -s -k -X POST https://$HOST:$PORT/book/booksByBookIdList -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

echo "$toto"
assertCurl 200 "$toto"

assertEqual 3 "$(echo $RESPONSE | jq -r ". | length")"
assertEqual "heisenberg-1923" "$(echo $RESPONSE | jq -r .[0].slug)"


echo ""
echo "End, all tests OK:" `date`


