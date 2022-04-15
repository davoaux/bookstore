# Commerce Services - Technical Interview

## Context:

We would like to evaluate both skills: backend development and ops knowledge.
We have prepared two independent tests. 


## Dev part

### Problem:

We are an online book store. We receive book orders from customers and process them.

### Features

- **Create a new Order**:
  - The application receives orders in a JSON format through an HTTP API endpoint (POST).
  - Orders contain a list of books and the quantity.
  - Before registering the order, the system should check if there's enough stock to fulfill the order (`import.sql` will set the initial stock).
  - If one of the books in the order does not have enough stock we will reject the entire order.
  - After stock validation, the order is marked as a success, and it would return a Unique Order Identifier to the caller of the HTTP API endpoint.
  - If the order was processed we need to update available stock, taking into consideration:
    - Updating stock should not be a blocker for replying to the customer.
    - If the process of updating stock fails, should not cause an error in order processing.

- **Retrieve Orders**:
  - The application has an endpoint to extract a list of existing orders. So that we can run "curl localhost:8080/orders/" and get a list of them

### Required:

- Resolution needs to be fully in English
- You need to use Java 11
- You are provided with a scaffold, fork or create a public repository with your solution. Once the code is complete, reply to your hiring person of contact.
- We expect you to implement tests for the requested functionalities. You decide the scope.

### How to run

Building
```shell
$ ./mvnw compile
```

Test
```shell
$ ./mvnw test
```

Start the application

```shell
$ ./mvnw spring-boot:run
```

Getting current stock for a given book 

```shell
$ curl localhost:8080/books_stock/ae1666d6-6100-4ef0-9037-b45dd0d5bb0e
{"id":"ae1666d6-6100-4ef0-9037-b45dd0d5bb0e","name":"adipisicing culpa Lorem laboris adipisicing","quantity":0}
```

## Ops art

### Problem:

We would like you to define and build the infrastructure we need for our SaaS product.

We have two different services, both serving http content.
Both services will use the docker image "nginxdemos/nginx-hello" (see https://github.com/nginxinc/NGINX-Demos/tree/master/nginx-hello-nonroot) .
The first service, named "api-svc", is exposed via the host "api.oursaas.org".
The second service, "static-svc", serves the static content on the domain "static.oursaas.org"
Both should be accesible externally via those hosts mentioned.
For HA requirements we need at least 3 replicas of each service, however we need to scale the api-svc when having some spike in traffic up to 10 replicas. Consider CPU > 50% as metric threshold to scale up.
We want also to protect our services from disruptions in case some node needs to be recreated or moved.

Additionally, api-svc uses some AWS resources: an RDS running in a VPC with CIDR 172.1.1.0/24 and a DynamoDB table in us-east-1.
The service static-svc uses AWS S3 as a backend to store and retrieve assets.
We need to setup proper network policies for services to connect corresponding AWS resources. Note that 0/0 is too open-wide and discouraged, we should use the least privilege principle when possible.

Finally, following the devops mindset we would like to automate the setup. We need a declarative pipeline (Jenkinsfile) to validate (when change requests) and rollout (merge to master) the resources.
We are delivering our SaaS in three different environments: integration (ns-int), sandbox (ns-sandbox) and production (ns-prod).
Corresponding hosts will be rolling the pattern "svc-[env].oursaas.org" except for prod that we omit the env, so that we will have:  api-int.oursaas.org, static-int.oursaas.org, api-sandbox.oursaas.org, …., api.oursaas.org, etc.

Last, do not forget to document tradeoffs, assumptions you made and other relevant documentation that will help us to understand your setup.

### Scope:

You need to know that computing resources will be in Kubernetes while persistence will be in AWS backed services (RDS, DynamoDB, S3). Specifically in this test *you'll need to define only kubernetes resources*, assuming that AWS resources are out of scope.

