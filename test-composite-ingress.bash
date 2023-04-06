#!/bin/bash

# gateway https port
HOST="minikube.gutenberg"
PORT=443
MGM_HOST="health.minikube.gutenberg"

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

    if [[ "$actual" = "$expected" ]]
    then
        echo "Test OK (actual value: $actual)"
        return 0
    else
        echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
        exit 1
    fi
}

function assertMatch() {

  local expected=$1
  local myarray=("$expected") 
  local actual=$2
  
  #echo "My array: ${myarray[@]}"
  #echo "Number of elements in the array: ${#myarray[@]}"
    
  for grunge in ${myarray[@]}
    do
      echo "grunge $grunge actual $actual"
      if [ "$actual" = "$grunge" ]
      then 
        #echo "true"
        echo "Test OK (actual value: $actual)"
        return 0
      else
        echo "false"    
      fi
    done; 
  #echo "What the fuck?"
  echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT" 
  exit 1   
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

./createCategoriesIngress.bash
./createBooksIngress.bash
./createReviewsIngress.bash
./createOrdersIngress.bash
./createUsersIngress.bash

waitForService curl -s -k https://$MGM_HOST:$PORT/composite/actuator/health

echo " "
echo " "
echo "SATOR"

# get books not reviewed by given user
toto="curl -s -k https://$HOST:$PORT/composite/getBooksNotReviewed?userId=2\&outLimit=5 -H 'Accept: application/json'"

assertCurl 200 "$toto"
echo $RESPONSE
assertMatch "[3,7]" "$(echo $RESPONSE)"

echo " "
echo " "
echo "AREPO"

toto="curl -s -k https://$HOST:$PORT/composite/getBooksBoughtWithBookId?bookId=3"

assertCurl 200 "$toto"
assertEqual 4 $(echo $RESPONSE | jq ". | length")

assertEqual "heisenberg-1923" $(echo $RESPONSE | jq -r .[0].slug)


# test editCart request

echo " " 
echo " " 
echo "TENET"

# EditCart object
body='{"userId":10, "date": "2017-07-24T00:00:00", "items":[{"bookId":14, "quantity":3},{"bookId":15, "quantity":1}]}'

toto="curl -X POST -s -k https://$HOST:$PORT/composite/editCart -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 200 "$toto"
#echo $RESPONSE | jq -r .
assertEqual 22856 $(echo $RESPONSE | jq -r .subtotal)

# exceptions handling

echo " " 
echo " " 
echo "OPERA"

# illegal editCart

body='{"userId":10, "date": "2017-09-24T00:00:00","items":[{"bookId":14, "quantity":3},{"bookId":15, "quantity":1}]}'

toto="curl -X POST -s -k https://$HOST:$PORT/composite/editCart -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 400 "$toto"
#echo $RESPONSE | jq -r .message
assertEqual "illegal state" "$(echo $RESPONSE | jq -r .message)"

echo " " 
echo " " 
echo "ROTAS"

# update book price

body='{"bookId": 14, "price": 3800}' 

toto="curl -X POST -s -k https://$HOST:$PORT/book/updateBook -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 200 "$toto"
echo $RESPONSE | jq -r .price
assertEqual 3800 $(echo $RESPONSE | jq -r .price)

echo " " 
echo " " 
echo "AD PATRES"

# recalculate order

body='{"userId":10, "date":"2017-07-24T00:00:00"}'

toto="curl -s -k -XPOST https://$HOST:$PORT/composite/recalculateTotal -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'"

assertCurl 200 "$toto"
echo $RESPONSE | jq -r .price
assertEqual 18239 $(echo $RESPONSE | jq -r .subtotal)

# edit cart again

body='{"userId":10, "date": "2017-07-24T00:00:00", "items":[{"bookId":14, "quantity":3},{"bookId":15, "quantity":1}]}'

toto="curl -X POST -s -k https://$HOST:$PORT/composite/editCart -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 200 "$toto"
#echo $RESPONSE | jq -r .
assertEqual 18239 $(echo $RESPONSE | jq -r .subtotal)


echo " "
echo "AD LIBITUM"

# add book to order

body='{"userId": 8, "date": "2017-07-24T00:00:00", "bookId": 5}'

toto="curl -X POST -s -k https://$HOST:$PORT/composite/addBookToOrder -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo "$toto"
assertCurl 200 "$toto"
echo $RESPONSE | jq -r .
assertEqual 5 $(echo $RESPONSE | jq -r .lineItems[1].bookId)
assertEqual 1 $(echo $RESPONSE | jq -r .lineItems[1].quantity)

echo " "
echo "AD HOMINEM"

# change order state from CART to PRE_AUTHORIZE

body='{"userId": 9, "date": "2017-07-24T00:00:00", "state": "PRE_AUTHORIZE"}'

toto="curl -X POST -s -k https://$HOST:$PORT/composite/setOrderState -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

echo "$toto"
assertCurl 200 "$toto"
assertEqual "PRE_AUTHORIZE" $(echo $RESPONSE | jq -r .state)

echo " "
echo "AD ADA SUR MON BIDET"

# change order state from PRE_AUTHORIZE to PRE_SHIPPING

body='{"userId": 9, "date": "2017-07-24T00:00:00", "state": "PRE_SHIPPING"}'

toto="curl -X POST -s -k https://$HOST:$PORT/composite/setOrderState -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

echo "$toto"
assertCurl 200 "$toto"
assertEqual "PRE_SHIPPING" $(echo $RESPONSE | jq -r .state)

echo " "
echo "MORBUS GRAVIS"

# illegal transition from PRE_SHIPPING to PRE_AUTHORIZE

body='{"userId": 9, "date": "2017-07-24T00:00:00", "state": "PRE_AUTHORIZE"}'

toto="curl -X POST -s -k https://$HOST:$PORT/composite/setOrderState -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo "$toto"
assertCurl 400 "$toto"

echo " "
echo "LOREM IPSUM"

# post a review

body="{\"userId\" : 2"
body+=',"bookId" : 3, "username" : "Albert",
    "text" : "Lorem ipsum sed de profundis morpîonibus.",
    "date" : "2023-02-23T15:15:00",
    "rating" : 5,
    "helpfulVotes" : 0,
    "voterIds" : [ ]
}'

toto="curl -X POST -s -k https://$HOST:$PORT/composite/review -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'"

assertCurl 200 "$toto"
#echo $RESPONSE | jq -r .
assertEqual "true" $(echo $RESPONSE | jq -r .ok)

echo " "
echo "STABAT MATER"

# try to post the same review again, expect false
assertCurl 200 "$toto"
assertEqual "false" $(echo $RESPONSE | jq -r .ok)


echo ""
echo "End, all tests OK:" `date` 









