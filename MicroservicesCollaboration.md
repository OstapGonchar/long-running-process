# Microservices Collaboration notes

## Notes

### What is microservice?
Microservice - a type of distribute system, communicating over network. 
They are independently deployable.
Microservices often **depend** on other microservices.
**Boundaries** primarily defined by Domain (DDD).
**Data is hidden** inside the microservice boundary.

### Provisioning
You want to over provision when you do RollingUpdate, it's important to take into account load.

### Communication

* **Request-response** -> executed via issuing **request** and waiting for response (Sync/Async)
* **Event-driven** -> executed by issuing event (**fire&forget mode**), consumers will consume it

When designing communication endpoints -> expose only data consumers need, hiding internals.

HTTP3 QUIC protocol to replace traditional HTTP2 TCP connections with more modern stream based HTTP3 UDP connection.

More about QUIC:
* https://github.com/ptrd/kwik
* https://github.com/VKCOM/KNet
* You can see more about QUIC here - [mobile](https://www.highload.ru/spb/2021/abstracts/8037) or [product](https://www.highload.ru/spb/2021/abstracts/8035) or [backend](https://www.highload.ru/spb/2021/abstracts/8034)
* https://nginx.org/en/docs/quic.html

To trace distributed communication you can consider using https://www.jaegertracing.io/ (based on OpenTracing standard).

### Handling errors

In distributed system you might experience not just Service Exceptions, but also other errors (potentially transitive), such as:
* Timeout - meaning we might need Retries
* Downstream outage / Service unavailability
* Difference between Client/Service errors

### Transactions across multiple Microservices

How to achieve the following in Microservices?
* Atomicity
* Isolation
* Durability
* Consistency

**Two-phase commit example** -> You could introduce Transaction Coordinator, 
that coordinates complex transaction across multiple microservices.

It is better to be consistent and slow, then fast and inconsistent.

### Implementing Saga Pattern (Transaction chain across microservices)

You need the following items:
* **Execution Log** - a record of what has happened
* **Execution Coordinator** - Something to tell you what to do

In order to coordinate transaction you can either use **Orchestration** or **Choreography**.

### Workshop task

Buying a ticket
* A person uses a machine at the car park to buy a ticket
* The ticket goes on the dashboard

Fines
* The attendant records the car registration
* Assume we can find addresses from that
* Fine is recorded and sent in the post

Only Request/Response