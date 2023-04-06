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
            echo "Test OK (HTTP Code: $httpCode)"
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


waitForService curl -s -k https://$MGM_HOST:$PORT/order/actuator/health




# clear orders collection

#assertCurl 200 "curl -X DELETE -s -k https://$HOST:$PORT/order/deleteAllOrders"

# populate orders collection
./createOrdersIngress.bash


echo " "
echo " "
echo "BEGIN"
# get an order by orderKey

body='{"userId":"10", "date":"2017-09-24T00:00:00"}'

toto="curl -s -k https://$HOST:$PORT/order/orderByKey -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 200 "$toto"
assertEqual 12 $(echo $RESPONSE | jq -r .lineItems[0].bookId)

echo " "
echo "SATOR"
# get active order for userId 10

toto="curl -s -k https://$HOST:$PORT/order/activeOrder/10 -H 'Accept: application/json'"

assertCurl 200 "$toto"
assertEqual "CART" $(echo $RESPONSE | jq -r .state)
assertEqual 14 $(echo $RESPONSE  | jq -r .lineItems[0].bookId)


echo " "
echo "AREPO"

# find all orders shipped to a user
toto="curl -s -k https://$HOST:$PORT/order/shippedOrdersByUserId/6 -H 'Accept: application/json'"
assertCurl 200 "$toto"
echo $RESPONSE  | jq -r .
assertEqual 2 "$(echo $RESPONSE | jq -r ". | length")"
assertEqual "2017-05-24T00:00:00" "$(echo $RESPONSE | jq -r .[0].date)"

# find all books bought with a book identified by bookId
toto="curl -s -k https://$HOST:$PORT/order/booksBoughtWithBookId?bookId=3\&outLimit=10 -H 'Accept: application/json'"
assertCurl 200 "$toto"
assertEqual "[7,12,14,15]" $(echo $RESPONSE)

# request to a non existing order
#body='{"orderId": 42, "bookId": 5}'
body='{"userId": 2, "date": "2019-08-24T00:00:00", "bookId": 5}'

toto="curl -X POST -s -k https://$HOST:$PORT/order/addBookToOrder -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 404 "$toto"

body='{"userId": 2, "date": "2019-08-24T00:00:00", "state": "PRE_SHIPPING"}'

toto="curl -X POST -s -k https://$HOST:$PORT/order/setOrderState -H 'Accept: application/json' -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo "$toto"
assertCurl 404 "$toto"

# create an order in state CART when already existing active order

body="{\"userId\" : 10"
# Carol
body+=',"state" : "CART",
    "lineItems" : [
        {
            "bookId" : 1,
            "quantity" : 1
        }
    ],
    "shippingAddress" : {
        "street" : "",
        "city" : "",
        "zip" : "",
        "state" : "",
        "country" : ""
    },
    "paymentMethod" : {
        
    },
    "subtotal" : 0,
    "date" : "2023-02-07T00:00:00"
}'
 
toto="curl -X POST -s -k https://$HOST:$PORT/order/createOrder -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 422 "$toto"
assertEqual "Active order already present" "$(echo $RESPONSE | jq -r .message)"


echo ""
echo "End, all tests OK:" `date` 
