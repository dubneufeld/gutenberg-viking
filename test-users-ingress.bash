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


waitForService curl -s -k https://health.minikube.gutenberg:443/user/actuator/health

# clear books collection

#assertCurl 200 "curl -X DELETE -s -k https://$HOST:$PORT/user/deleteAllUsers"

# populate users collection
./createUsersIngress.bash

echo " "
echo "COLLECTION POPULATED"

# get user by username
assertCurl 200 "curl -s -k https://$HOST:$PORT/user/userByName/Carol"
assertEqual 1 $(echo $RESPONSE | jq -r .userId)

echo " "
echo "SATOR"

# get user by userId
assertCurl 200 "curl -s -k https://$HOST:$PORT/user/userByUserId/1"
assertEqual "Carol" $(echo $RESPONSE | jq -r .username)

# get user not found
assertCurl 404 "curl -s -k https://$HOST:$PORT/user/userByName/Arthur"

# create a new user

body="{\"userId\" : 42"
body+=',"username" : "Morbus",
    "hashedPassword" : "{bcrypt}$2a$10$zICb16aMAVQyJ6180HUpDuvQAbZX.yGPmjUC7FSD7otSRSoBdbnjG",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'


toto="curl -X POST -s -k https://$HOST:$PORT/user/user -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 200 "$toto"
#echo $RESPONSE
assertEqual "Morbus" $(echo $RESPONSE)

# try to create a user with an existing username
assertCurl 409 "$toto"

echo ""
echo ""
echo "SATOR"


# delete a user by username
assertCurl 200 "curl -X DELETE -s -k https://$HOST:$PORT/user/deleteUser/Sator"

# delete should be idempotent
assertCurl 200 "curl -X DELETE -s  -k https://$HOST:$PORT/user/deleteUser/Sator"

echo ""
echo ""
echo "AREPO"

# handling address changes

# add an address

body="{\"op\": \"ADD\", \"userId\": 42"
body+=',"address": { "street" : "Bahnhofstrasse 42",
            "city" : "Zurich",
            "zip" : "4000",
            "state" : "",
            "country" : "Switzerland"}
}'

toto="curl -X PUT -s -k https://$HOST:$PORT/user/addAddress -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'"

assertCurl 200 "$toto"
assertEqual "Bahnhofstrasse 42" "$(echo $RESPONSE | jq -r .addresses[0].street)"


# make an address primary

# Werner has two addresses, initiallly primary is set to 0

# body is a Primary POJO

body="{\"userId\": 3, \"index\": 1}"

#echo $body | jq .            

toto="curl -X PUT -s -k https://$HOST:$PORT/user/primaryAddress -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo $toto
assertCurl 200 "$toto"
#echo $RESPONSE | jq .mainShippingAddress 
assertEqual 1 "$(echo $RESPONSE | jq -r .mainShippingAddress)"

# delete an address

body="{\"op\": \"DELETE\", \"userId\": 42"
body+=',"address": {
            "street" : "31 rue du Louvre",
            "city" : "Paris",
            "zip" : "75001",
            "state" : "",
            "country" : "France"
        }
}'

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/deleteAddress -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

echo $toto

assertCurl 200 "$toto"
echo "$(echo $RESPONSE | jq ".addresses | length")"
assertEqual 1 $(echo $RESPONSE | jq ".addresses | length")

echo ""
echo ""
echo "TENET"


# handling payment method changes

# add a payment method

body="{\"op\": \"ADD\", \"userId\": 42"
body+=',"paymentMethod": { "cardNumber" : "12346666789803333",
            "name" : "Paul Enclume"}
}'

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/addPaymentMethod -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

assertCurl 200 "$toto"
#echo "$(echo $RESPONSE | jq .paymentMethods[0].name)"
#assertEqual "\"Paul Enclume\"" "$(echo $RESPONSE | jq -r .paymentMethods[0].name)"
assertEqual "Paul Enclume" "$(echo $RESPONSE | jq -r .paymentMethods[0].name)"


# make a payment method primary

body="{\"userId\": 3, \"index\": 1}"

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/primaryPaymentMethod -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo $toto

assertCurl 200 "$toto"
#echo "$(echo $RESPONSE | jq .mainPayMeth)"
assertEqual 1 "$(echo $RESPONSE | jq .mainPayMeth)"

# delete a payment method

body="{\"op\": \"DELETE\", \"userId\": 42"
body+=',"paymentMethod": {
            "cardNumber" : "8888777744441111",
            "name" : "Jean Castex"
        }
}'

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/deletePaymentMethod -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo $toto

assertCurl 200 "$toto"
#echo "$(echo $RESPONSE | jq ".paymentMethods | length")"
assertEqual 1 $(echo $RESPONSE | jq ".paymentMethods | length")


echo ""
echo ""
echo "OPERA"


  

# handling a non existing user

body="{\"userId\": 33, \"index\": 1}"

#echo $body | jq .            

toto="curl -X PUT -s -k https://$HOST:$PORT/user/primaryAddress -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

echo $toto
assertCurl 404 "$toto"

body="{\"userId\": 33, \"index\": 1}"

#echo $body | jq .            

toto="curl -X PUT -s -k https://$HOST:$PORT/user/primaryPaymentMethod -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

echo $toto
assertCurl 404 "$toto"


# add an address to a non existing user

body="{\"op\": \"ADD\", \"userId\": 33"
body+=',"address": { "street" : "Bahnhofstrasse 42",
            "city" : "Zurich",
            "zip" : "4000",
            "state" : "",
            "country" : "Switzerland"}
}'

toto="curl -X PUT -s -k https://$HOST:$PORT/user/addAddress -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'"

assertCurl 404 "$toto"

echo ""
echo ""
echo "ROTAS"

# delete an address from a non existing user

body="{\"op\": \"DELETE\", \"userId\": 33"
body+=',"address": {
            "street" : "31 rue du Louvre",
            "city" : "Paris",
            "zip" : "75001",
            "state" : "",
            "country" : "France"
        }
}'

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/deleteAddress -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo $toto
assertCurl 404 "$toto"

# add a payment method to a non existing user

body="{\"op\": \"ADD\", \"userId\": 33"
body+=',"paymentMethod": { "cardNumber" : "12346666789803333",
            "name" : "Paul Enclume"}
}'

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/addPaymentMethod -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo $toto

assertCurl 404 "$toto"

# delete a payment method from a non existing user

body="{\"op\": \"DELETE\", \"userId\": 33"
body+=',"paymentMethod": {
            "cardNumber" : "8888777744441111",
            "name" : "Jean Castex"
        }
}'

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/deletePaymentMethod -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 


echo $toto

assertCurl 404 "$toto"

# delete a payment method with invalid userId

body="{\"op\": \"DELETE\", \"userId\": -1"
body+=',"paymentMethod": {
            "cardNumber" : "8888777744441111",
            "name" : "Jean Castex"
        }
}'

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/deletePaymentMethod -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 


echo $toto

assertCurl 422 "$toto"

body="{\"op\": \"DELETE\", \"userId\": invalid"
body+=',"paymentMethod": {
            "cardNumber" : "8888777744441111",
            "name" : "Jean Castex"
        }
}'

#echo $body | jq .            


toto="curl -X PUT -s -k https://$HOST:$PORT/user/deletePaymentMethod -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 


echo $toto

assertCurl 400 "$toto"


echo ""
echo "End, all tests OK:" `date` 
