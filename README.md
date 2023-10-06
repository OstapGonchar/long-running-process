## Problem statement

Imagine that you have a system, where **User** issues **Request**s, that should be processed by the **Engine**.
Now, **User** might issue multiple requests, and **Engine** is slow in processing it.
E.g. requests come each 30 seconds, but **Engine** takes 60 seconds to process.

User can see all requests status on a **Status** overview.

Communication pattern between User and Engine should be Apache Kafka.

Requests and Status topics are currently 2 different topics.

Requestor, when initiating request, pushes the same request both in Request and Status topic in **INITIATED** state.

Status Observer cares only about status topic that is visible to user.

Engine(s) should pick up pending requests and process. 
They should not pick up already processed / picked up by someone else requests.

## Your task
Improve the system to ensure we can scale Engines horizontally to ensure we can handle the load of users.
You can change topics and Engine/Producer/StatusObserver logic.
It is only important that the user can still have visibility - requests are issues, he can see the overview and completed requests.

## Build instructions
1. Create Confluent Cluster `docker-compose up -d`
2. Check that Cluster is created by opening http://localhost:9021/
3. Create there two topics, named **requests** and **status**
4. Build gradle project `./gradlew jibDockerBuild`
5. Then create **Status** service:
    ```shell
    docker run --rm -it --net=host app status
    ```
6. Then create **User** that will start producing requests:
    ```shell
    docker run --rm -it --net=host app producer
    ```
7. Then create engine:
    ```shell
    docker run --rm -it --net=host app engine 1
    ```
