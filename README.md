# API Gateway

A production-oriented **API Gateway microservice** built with **Spring Boot**.
This service acts as the single entry point for client requests and is responsible for handling common cross-cutting concerns before forwarding requests to internal backend services.

## Architecture
                         ┌──────────────────┐
                         │      Client      │
                         └────────┬─────────┘
                                  │
                                  │ HTTPS
                                  ▼
                     ┌────────────────────────┐
                     │      API Gateway       │
                     │                        │
                     │  • Authentication      │
                     │  • Authorization       │
                     │  • Rate Limiting       │
                     │  • CORS               │
                     │  • Request Routing     │
                     │  • Request Validation  │
                     │  • Logging             │
                     └───────────┬────────────┘
                                 │
                         Private Network
                                 │
                  ┌──────────────┴──────────────┐
                  │                             │
                  ▼                             ▼
        ┌──────────────────┐          ┌──────────────────┐
        │  Core Service    │          │  Other Services  │
        │      EC2         │          │                  │
        └────────┬─────────┘          └──────────────────┘
                 │
                 ▼
              ┌───────┐
              │  RDS  │
              └───────┘

The gateway is the **public-facing entry point**, while internal services can remain inside a private network.

---

## Responsibilities

The API Gateway handles concerns that should not need to be implemented independently in every backend service.
### Authentication

Validates incoming requests and verifies the user's identity.

Example:
Client
  │
  │ Authorization: Bearer <JWT>
  ▼
API Gateway
  │
  ├── Validate JWT
  │
  └── Forward authenticated request

Invalid or missing credentials can be rejected at the gateway.

### Authorization
Authentication answers:

> Who are you?
Authorization answers:

> Are you allowed to access this resource?

The gateway can inspect authenticated user information and enforce access rules before routing the request.

Example:
USER       → /api/users/**       → Allowed
ADMIN      → /api/admin/**      → Allowed
USER       → /api/admin/**      → Forbidden

### Rate Limiting
Protects backend services from excessive traffic.

Example:
100 requests / minute / client

When the limit is exceeded:
http
HTTP/1.1 429 Too Many Requests

Rate limiting can be implemented using **Redis** for distributed deployments.

---

### CORS
The gateway handles Cross-Origin Resource Sharing policies.

Example:

Allowed Origins
    ↓
https://frontend.example.com

Allowed Methods
    ↓
GET
POST
PUT
DELETE

Allowed Headers
    ↓
Authorization
Content-Type

This keeps CORS configuration centralized instead of duplicating it across every backend service.

---

### Request Routing

The gateway determines which backend service should receive a request.

Example:
/api/auth/**     → Authentication Service
/api/users/**    → User Service
/api/orders/**   → Order Service
/api/products/** → Product Service
The client does not need to know the internal service locations.

### Request Source Validation
Internal services can verify that requests are coming through the API Gateway.

The gateway can add:
X-Request-From: API-GATEWAY

The internal service can validate this header before processing the request.
Client
   │
   ▼
API Gateway
   │
   │ X-Request-From: API-GATEWAY
   ▼
Core Service

X-Request-From` is an additional application-level check, not a replacement for network security or authentication.

---

## Network Security

Internal services should not be directly exposed to the public internet.

Example:

```text
Internet
   │
   ▼
API Gateway
   │
   │ Private Network
   ▼
Core Service EC2
```

The Core Service Security Group should allow inbound traffic only from the API Gateway/security-group source.

Core Service Security Group

Inbound:
--------------------------------
Port 8080
Source: API Gateway Security Group
--------------------------------

Public Internet → ❌
Direct Client   → ❌
API Gateway     → ✅

This provides a network-level boundary around internal services.

---

## Request Flow

A typical request follows this flow:

1. Client sends request
          │
          ▼
2. API Gateway receives request
          │
          ▼
3. CORS validation
          │
          ▼
4. Authentication
          │
          ▼
5. Authorization
          │
          ▼
6. Rate limiting
          │
          ▼
7. Request validation
          │
          ▼
8. Add internal request metadata
          │
          ▼
9. Route request
          │
          ▼
10. Internal backend service

---
## Example Request

Client:

GET /api/users/profile HTTP/1.1
Host: api.example.com
Authorization: Bearer <JWT>
Origin: https://frontend.example.com

Gateway validates the request and forwards it internally:

http
GET /api/users/profile HTTP/1.1
Host: core-service
X-Request-From: API-GATEWAY
Authorization: Bearer <JWT>


The internal service processes the request without being directly exposed to the client.

---

## Technology Stack

* Java
* Spring Boot
* Spring Security
* Spring Cloud Gateway
* Redis
* Docker
* AWS EC2
* AWS VPC
* AWS Security Groups
* AWS RDS
* GitHub Actions

---

## Project Goals

This project is designed to demonstrate practical API Gateway and backend architecture concepts.

### Core Features

* [ ] Authentication
* [ ] JWT validation
* [ ] Authorization / RBAC
* [ ] Request routing
* [ ] Rate limiting
* [ ] Redis-based rate limiting
* [ ] CORS configuration
* [ ] Request validation
* [ ] Request/response logging
* [ ] Global exception handling
* [ ] Internal request validation
* [ ] `X-Request-From` header validation
* [ ] Service-to-service communication
* [ ] Health checks
* [ ] Monitoring

### AWS / Deployment

* [ ] VPC configuration
* [ ] Public API Gateway layer
* [ ] Private backend EC2
* [ ] Security Groups
* [ ] RDS
* [ ] CloudWatch monitoring
* [ ] Docker deployment
* [ ] GitHub Actions CI/CD

---

## Security Model

The project follows a layered security approach:
                 ┌──────────────────────┐
                 │     Client           │
                 └──────────┬───────────┘
                            │
                     HTTPS / TLS
                            │
                            ▼
                 ┌──────────────────────┐
                 │    API Gateway       │
                 │                      │
                 │ Authentication       │
                 │ Authorization        │
                 │ Rate Limiting        │
                 │ CORS                │
                 └──────────┬───────────┘
                            │
                     Private Network
                            │
                            ▼
                 ┌──────────────────────┐
                 │   Core Service       │
                 │                      │
                 │ Network restriction  │
                 │ Request validation   │
                 └──────────┬───────────┘
                            │
                            ▼
                         Database

Each layer provides a different security boundary.

---

## Local Development

Clone the repository:
git clone <repository-url>
cd api-gateway

Run application : 
./mvnw spring-boot:run

For Windows:
mvnw.cmd spring-boot:run

The gateway will start on:
http://localhost:8080

---
## Environment Configuration

Sensitive configuration should not be committed to Git.

Example:

properties
JWT_SECRET=${JWT_SECRET}
REDIS_HOST=${REDIS_HOST}
REDIS_PORT=${REDIS_PORT}

Environment variables should be provided through the local environment, deployment platform, or CI/CD secrets.

---

## Future Improvements

Potential future enhancements:

* Distributed tracing
* Correlation IDs
* Centralized metrics
* Circuit breaker
* Retry policies
* Request timeout policies
* Service discovery
* API documentation
* Observability with CloudWatch / ELK
* Distributed rate limiting
* Canary / blue-green deployment support

---

## Learning Objectives

This project focuses on understanding how an API Gateway fits into a real backend architecture.

The main concepts covered are:

API Gateway
      ↓
Authentication
      ↓
Authorization
      ↓
Rate Limiting
      ↓
CORS
      ↓
Routing
      ↓
Private Services
      ↓
AWS Networking
      ↓
Monitoring
      ↓
CI/CD
```

The goal is not only to build an API Gateway, but to understand **why each responsibility belongs at the gateway layer and how it interacts with internal microservices**.
