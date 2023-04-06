#!/bin/bash

HOST="minikube.gutenberg"
PORT=443

# delete all users

curl -X DELETE -s -k https://$HOST:$PORT/user/deleteAllUsers

# create all users

body="{\"userId\" : 1"
body+=',"username" : "Carol",
    "username" : "Carol",
    "hashedPassword" : "{bcrypt}$2a$10$uj9mmgShGneS5gXG77TA6eITexGHdF/fOQk/85EFhlcslGzqyyVGm",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [
        {
            "authority" : "ROLE_USER"
        }
    ],
    "addresses" : [
        {
            "street" : "Main Street 2233",
            "city" : "Dallas",
            "zip" : "75215",
            "state" : "TX",
            "country" : "USA"
        }
    ],
    "paymentMethods" : [
        {
            "cardNumber" : "1234567813572468",
            "name" : "Carol Baker"
        }
    ],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'

#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 


body="{\"userId\" : 2"
body+=',"username" : "Albert",
    "hashedPassword" : "{bcrypt}$2a$10$2amyAvl.aoVidnbd/8AUWOl7LDqkcWHZF/z29WFd6jQ6ZR78sffCi",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "addresses" : [
        {
            "street" : "Plumber Street 22",
            "city" : "London",
            "zip" : "WC2N 5DU",
            "state" : "",
            "country" : "UK"
        }
    ],
    "paymentMethods" : [
        {
            "cardNumber" : "1357246813572468",
            "name" : "Albert Degroot"
        }
    ],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'


#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 



body="{\"userId\" : 3"
body+=',"username" : "Werner",
    "hashedPassword" : "{bcrypt}$2a$10$O29SfMmkboaMcepA0KevPes0/hVS2PHle7gvYT9JOOwRx7Nuh8.b2",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "addresses" : [
        {
            "street" : "Hannoverstr. 22",
            "city" : "Berlin",
            "zip" : "10315",
            "state" : "",
            "country" : "DE"
        },
     {
            "street" : "31 rue du Louvre",
            "city" : "Paris",
            "zip" : "75001",
            "state" : "",
            "country" : "France"
        }
    ],
    "paymentMethods" : [
        {
            "cardNumber" : "4321987643219876",
            "name" : "Werner Stolz"
        },
     {
            "cardNumber" : "8888777744441111",
            "name" : "Jean Castex"
        }
    ],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'


#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 


body="{\"userId\" : 4"
body+=',"username" : "Alice",
    "hashedPassword" : "{bcrypt}$2a$10$Ip8KBSorI9R39m.KQBk3nu/WhjekgPSmfmpnmnf5yCL3aL9y.ITVW",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [
        {"authority"  :  "ROLE_USER"}],
    "addresses" : [
        {
            "street" : "42 rue Amélie Poulain",
            "city" : "Paris",
            "zip" : "75018",
            "state" : "",
            "country" : "FR"
        },
        {
            "street" : "42 rue Amélie Nothomb",
            "city" : "Paris",
            "zip" : "75018",
            "state" : "",
            "country" : "FR"
        }
    ],
    "paymentMethods" : [
        {
            "cardNumber" : "6789432167894321",
            "name" : "Alice Carrol"
        },
        {
            "cardNumber" : "6789432167891234",
            "name" : "Alice Carrol"
        }
    ],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'

#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 


body="{\"userId\" : 5"
body+=',"username" : "Richard",
    "hashedPassword" : "{bcrypt}$2a$10$LBMYa2KlGqKyjUg6UPSx8.SqV99/mWTryFwl1sY4.x0UKKqnab9ru",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "addresses" : [
        {
            "street" : "Avenue de la Gare 55",
            "city" : "Lausanne",
            "zip" : "1022",
            "state" : "",
            "country" : "CH"
        }
    ],
    "paymentMethods" : [
        {
            "cardNumber" : "9876123498761234",
            "name" : "Richard Brunner"
        },
        {
            "cardNumber" : "9876123498761235",
            "name" : "Richard Brenner"
        }
    ],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'


#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 


body="{\"userId\" : 6"
body+=',"username" : "Sator",
    "username" : "Sator",
    "hashedPassword" : "{bcrypt}$2a$10$bMiqHtOacryfa90U9ddokelGe3xlEmVVuZ1UDre3ArINmspjjsIGC",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'


#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body"


body="{\"userId\" : 7"
body+=',"username" : "Arepo",
    "username" : "Arepo",
    "hashedPassword" : "{bcrypt}$2a$10$5YYBC/bq85.z8.cUbDzi5euUotdU1kHgqsuDGAiDNByiXMG3zEoMi",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'

#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 


body="{\"userId\" : 8"
body+=',"username" : "Tenet",
    "hashedPassword" : "{bcrypt}$2a$10$Dr0/hk5Zy8xilHh.fdoc.OTsT/sDMraT1i4IMVpW39pzbu2w6ZVMC",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'

#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 


body="{\"userId\" : 9"
body+=',"username" : "Opera",
    "hashedPassword" : "{bcrypt}$2a$10$VwntQlZvYt4g7e3M7QadG.91SkLd/MW1vrhab2Qj.0VkTdGcjVrnm",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'

#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 


body="{\"userId\" : 10"
body+=',"username" : "Rotas",
    "hashedPassword" : "{bcrypt}$2a$10$zICb16aMAVQyJ6180HUpDuvQAbZX.yGPmjUC7FSD7otSRSoBdbnjG",

    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [{"authority"  :  "ROLE_USER"}],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
}'

#echo $body | jq -r .

curl -X POST -s -k https://$HOST:$PORT/user/user -H "Content-Type: application/json" --data "$body" 



