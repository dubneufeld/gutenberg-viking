#!/bin/bash

# gateway https port
HOST="minikube.gutenberg"
PORT=443
MGM_HOST="health.minikube.gutenberg"
MGM_PORT=4004

# function declarations

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


waitForService curl -k -s https://$MGM_HOST:$PORT/review/actuator/health


# clear reviews collection

#assertCurl 200 "curl -X DELETE -k -s https://$HOST:$PORT/review/deleteAllReviews" 

#echo "DELETE OK"

# populate reviews collection

./createReviewsIngress.bash

# check the presence of reviews by userId

echo ""
echo ""
echo "SATOR"


assertCurl 200 "curl -s -k https://$HOST:$PORT/review/reviewsByUserId?userId=3"
#echo $(echo $RESPONSE | jq ". | length")
assertEqual 3 $(echo $RESPONSE | jq ". | length")


# check the presence of reviews by bookId

echo ""
echo ""
echo "AREPO"

assertCurl 200 "curl -s -k https://$HOST:$PORT/review/reviewsByBookId/7/sort/rate?"
assertEqual 3 $(echo $RESPONSE | jq ". | length")

echo ""
echo ""
echo "TENET"

echo "GET by userId and bookId"
assertCurl 200 "curl -s -k https://$HOST:$PORT/review/reviewByReviewId?userId=6\&bookId=7 -H 'Accept: application/json'"
assertCurl 404 "curl -s -k https://$HOST:$PORT/review/reviewByReviewId?userId=34\&bookId=7 -H 'Accept: application/json'"


# get a book rate

assertCurl 200 "curl -s -k https://$HOST:$PORT/review/bookRating/7" 
assertEqual 2.6666 ${RESPONSE:0:6}

echo ""
echo ""
echo "OPERA"

# add a vote to a review

assertCurl 200 "curl -s -k -XPOST https://$HOST:$PORT/review/addVote -H 'Content-Type: application/json' --data '{\"userId\": \"6\", \"bookId\": 7, \"voterId\": 42, \"helpful\": \"true\"}'"

# expect true the first time, false the second time
assertEqual "true" $RESPONSE

echo ""
echo ""
echo "LAPIN"

assertCurl 200 "curl -s -k -XPOST https://$HOST:$PORT/review/addVote -H 'Content-Type: application/json' --data '{\"userId\": \"6\", \"bookId\": 7, \"voterId\": 42, \"helpful\": \"true\"}'"

assertEqual "false" $RESPONSE


# exceptions handling

# GET request with a negative argument

echo ""
echo ""
echo "ROTAS"

assertCurl 422 "curl -s -k https://$HOST:$PORT/review/reviewsByUserId?userId=-1"
assertCurl 422 "curl -s -k https://$HOST:$PORT/review/reviewsByBookId/-1/sort/rate"

echo ""
echo ""
echo "LOREM"

# GET request with argument not a number

assertCurl 400 "curl -s -k https://$HOST:$PORT/review/reviewsByUserId?userId=invalid"
assertCurl 400 "curl -s -k https://$HOST:$PORT/review/reviewsByBookId/invalid/sort/rate"

echo ""
echo "End, all tests OK:" `date`   



