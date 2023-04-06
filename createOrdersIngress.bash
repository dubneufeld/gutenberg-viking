#!/bin/bash

HOST="minikube.gutenberg"
PORT=443

function createOrder() {
local body=$1

#echo "$body"
toto="curl -X POST -s -k https://$HOST:$PORT/order/createOrder -H 'Content-Type: application/json' --data '"
toto+="$body"
toto+="'" 

#echo "$toto"
eval "$toto"
}

# first delete all orders
curl -X DELETE -s -k https://$HOST:$PORT/order/deleteAllOrders

# populate orders collection

body="{\"userId\" : 7"
# Arepo
body+=', "state" : "SHIPPED",
        "lineItems" : [
            {
            "bookId" : 4,
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
    "date" : "2017-01-24T15:15:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 8"
# Tenet
body+=', "state" : "SHIPPED",
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
    "date" : "2017-02-24T00:00:00"
}'
  
#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 9"
# Opera
body+=', "state" : "SHIPPED",
    "lineItems" : [
        {
            "bookId" : 2,
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
    "date" : "2017-03-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 10"
# Rotas
body+=', "state" : "SHIPPED",
    "lineItems" : [
        {
            "bookId" : 3,
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
    "date" : "2017-04-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 6"
# Sator
body+=', "state" : "SHIPPED",
    "lineItems" : [
        {
            "bookId" : 5,
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
    "date" : "2017-05-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 7"
# Arepo
body+=', "state" : "SHIPPED",
    "lineItems" : [
        {
            "bookId" : 15,
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
    "date" : "2018-01-24T15:15:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 8"
# Tenet
body+=', "state" : "SHIPPED",
	"lineItems" : [
		{
			"bookId" : 9,
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
	"date" : "2017-08-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 9"
# Opera
body+=', "state" : "SHIPPED",
	"lineItems" : [
		{
			"bookId" : 9,
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
	"date" : "2018-08-24T00:00:00"
	
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 10"
# Rotas
body+=', "state" : "SHIPPED",
	"lineItems" : [
		{
			"bookId" : 12,
			"quantity" : 1
		},
		{
			"bookId" : 15,
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
	"date" : "2017-09-24T00:00:00"
}'


#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 6"
# Sator
body+=', "state" : "SHIPPED",
	"lineItems" : [
		{
			"bookId" : 12,
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
	"date" : "2017-11-24T00:00:00Z"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 10"
# Rotas
body+=', "state" : "CART",
	"lineItems" : [
		{
			"bookId" : 14,
			"quantity" : 3
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
	"date" : "2017-07-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 4"
# Alice
body+=', "state" : "CART",
	"lineItems" : [
		{
			"bookId" : 14,
			"quantity" : 3
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
	"date" : "2017-07-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 5"
# Richard
body+=', "state" : "CART",
	"lineItems" : [
		{
			"bookId" : 14,
			"quantity" : 3
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
	"date" : "2017-07-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 6"
# Sator
body+=', "state" : "CART",
	"lineItems" : [
		{
			"bookId" : 14,
			"quantity" : 3
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
	"date" : "2022-07-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 7"
# Arepo
body+=', "state" : "CART",
	"lineItems" : [
		{
			"bookId" : 14,
			"quantity" : 3
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
	"date" : "2017-07-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 8"
# Tenet
body+=', "state" : "CART",
	"lineItems" : [
		{
			"bookId" : 14,
			"quantity" : 3
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
	"date" : "2017-07-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 9"
# Opera
body+=', "state" : "CART",
	"lineItems" : [
		{
			"bookId" : 14,
			"quantity" : 3
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
	"date" : "2017-07-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 2"
# Albert
#body+=',"userId" : 2,
body+=', "state" : "SHIPPED",
    "lineItems" : [
        {
            "bookId" : 14,
            "quantity" : 1
        },
        {
            "bookId" : 15,
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
    "date" : "2018-05-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"

body="{\"userId\" : 2"
# Albert
body+=', "state" : "SHIPPED",
    "lineItems" : [
        {
            "bookId" : 7,
            "quantity" : 1
        },
        {
            "bookId" : 3,
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
    "date" : "2019-05-24T00:00:00"
}'

#echo $body | jq -r .
createOrder "$body"



