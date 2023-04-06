# gutenberg-viking
I present here a microservice-oriented application that uses some basic Kubernetes features including Ingress and Helm, with emphasis on testing. It consists of a collection of separate servers all running in Kubernetes pods. MongoDB is used as a NoSQL database and also runs in a pod. Moreover all backend servers use reactive connection to MongoDB and Spring WebFlux rather than Spring RESTful. Each of the four backend servers book-service, review-service, order-service and user-service handles all requests involving a single collection of the database gutenberg-db. A separate server named composite-service handles all requests that involve more than a single collection.
 
Here are the prerequisites for running the complete application:

A recent Minikube version running in Virtualbox. (I used 1.28.0)
A recent Apache Maven version installed (I used 3.8.7)

In addition I used Spring Tool Suite for developing this demo but it is not required for running the application.

Here is the list of all containers:

Server            | Image                       | Port         | Function             | Database connection | Collection
----------------- | --------------------------- | ------------ | -------------------- | ------------------- | ----------
mongodb           | mongo                       | 27017        | Schemaless database  |                     | 
book-service      | gutenberg/book-service      | 8081         | Book requests        | gutenberg-db	| books
review-service    | gutenberg/review-service    | 8082         | Review requests      | gutenberg-db        | reviews
order-service     | gutenberg/order-service     | 8083         | Order requests       | gutenberg-db        | orders
user-service      | gutenberg/user-service      | 8084         | User requests        | gutenberg-db        | users
composite-service | gutenberg/composite-service | 8080         | Composite requests   | none	        |

An Ingress is used to allow a direct connection from a client to the application through a virtual host named minikube.gutenberg.

Here are the steps to run the application:

# 1. Starting Minikube environment using Helm

Run this script:

```
./startCluster
```
Then install istio:

```
./installIstio.bash
```

Check istio deployment:

```
kubectl -n istio-system get deploy
```
Run these scripts to create (self-signed) certificates:
```
./installCertManager.bash
./helmInstallIstioDeployment.bash
```

Check istio cert:

```
kubectl -n istio-system get secret gutenberg-certificate
kubectl -n istio-system get certificate gutenberg-certificate
```
Run in a separate window:

```
minikube tunnel
```

Note that a super user password is required.

Add hosts:

```
./setIngressIP.bash
```

Set context and namespace gutenberg:

```
./helmSetIstioContext.bash
```

The Minikube environment required for this application is ready.


# 2. Building images

## 2.1 Building Spring Boot JAR files

Run this script:

```
./buildAll
```

## 2.1 Building Docker images in Minikube environment

Run this script:

```
./buildMinikubeImages
```

All images are ready

# 3. Starting the application

Run this script:

```
./helmInstallDeployment
```

To check the deployment run this command:

```
kubectl get pods
```

Expect a response like this:

```
NAME                         READY   STATUS    RESTARTS   AGE
book-64bd78b88f-98fhc        2/2     Running   0          60m
composite-75575488b7-w7w7g   2/2     Running   0          60m
mongodb-76c6575b97-lgx9l     1/1     Running   0          60m
order-7b5448d6b9-cd8k6       2/2     Running   0          60m
review-6995f788cf-zkghp      2/2     Running   0          60m
user-95cbdb757-r5dz2         2/2     Running   0          60m

```

# 4. Testing the servers

## 4.1 Testing the backend servers

To test the 4 backend servers, rune these scripts:

```
./test-books-ingress.bash
./test-reviews-ingress.bash
./test-orders-ingress.bash
./test-users-ingress.bash
```
Each test should return OK

## 4.1 Testing the composite server

Run this script:

```
./testcomposite-ingress.bash
```

This test should return OK

To stop the application run the script:

```
./helmUninstallDeployment
```


# 5. Running the client

## 5.1 Running Redis in a container

The client uses a Redis backed session and does not run itself in Kubernetes environment. The first step is to start a Redis container, outside Kubernetes environment

./runRedis.bash

## 5.2 Running the frontend

Then go to frontend folder and run the script

```
./run
```


Then hit the browser on localhost:8080 to connect to the frontend. A username and password are required. Here are the prepopulated users:

Username | Password
-------- | --------- 
Carol    | s1a2t3o4r 
Albert   | a5r6e7p8o
Werner   | t4e3n2e1t
Alice    | o8p7e6r5a
Richard  | r1o2t3a4s
Sator    | sator1234 
Arepo    | arepo1234
Tenet    | tenet1234
Opera    | opera1234
Rotas    | rotas1234


`
# 6. Accessing MongoDB container
To access the MongoDB container run the command:
```
$ kubectl get po
mongo-6d8d94b7d5-2tp5m      1/1     Running   0          11m

```
Then run the command:

```
kubectl exec -it mongo-6d8d94b7d5-2tp5m -- /bin/bash
```
Then in the pod shell run the command:

```
mongo -u mongodb-user-dev -p
```
Enter the password `mongodb-pwd-dev`
and then for example to display orders collection:

```
use gutenberg-db
db.orders.find().pretty()
```

# 7. Screen snapshots

Here are some screen snapshots that can be seen by running this application:

Welcome page:
![alt text](images/welcome.png "Welcome page")

Book page:
![alt text](images/book.png "Book page")

Cart page:
![alt text](images/cart.png "Cart page")

Checkout page:
![alt text](images/checkout.png "Checkout page")

Payment page:
![alt text](images/checkoutSuccess.png "Payment page")

To stop the application run this command:


Cachan, April 19 2021
 
Dominique Ubersfeld
