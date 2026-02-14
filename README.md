# Loyalty Platform

A production-grade microservices backend system implementing a realistic loyalty and voucher platform.

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [System Design](#system-design)
- [Key Features](#key-features)
- [Business Flows](#business-flows)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Event Flows](#event-flows)
- [Testing](#testing)
- [Monitoring](#monitoring)
- [Security](#security)
- [Deployment](#deployment)
- [Best Practices](#best-practices)
- [Project Structure](#project-structure)

## Project Overview

The Loyalty Platform is a comprehensive microservices solution designed to handle loyalty programs and voucher management. The system is built with resilience, scalability, and maintainability in mind, following modern software engineering practices and cloud-native principles.

### Core Capabilities
- User management and profile storage
- Voucher lifecycle management (creation, redemption, expiration)
- Loyalty points system (earning, redemption, tracking)
- Event-driven communication between services
- Distributed transaction management
- Comprehensive monitoring and observability

## Architecture Overview

The system follows a microservices architecture with event-driven communication using Apache Kafka. It consists of three main services:

### Services

1. **User Service** (`user-service`)
   - Manages user registration and profiles
   - Maintains user information in PostgreSQL
   - Publishes `UserRegistered` events
   - Handles user authentication and authorization

2. **Voucher Service** (`voucher-service`)
   - Manages voucher lifecycle (create, redeem, expire)
   - Handles voucher codes and expiration
   - Implements atomic redemption with optimistic locking
   - Publishes `VoucherIssued` and `VoucherRedeemed` events
   - Scheduled task for voucher expiration

3. **Loyalty Service** (`loyalty-service`)
   - Manages loyalty points (earn, redeem)
   - Tracks user points balance (total, available, spent)
   - Implements idempotency for transaction processing
   - Publishes `PointsEarned` and `PointsRedeemed` events

### Shared Components

- **Common Module**: Contains shared entities, events, configurations, and utilities
- **Outbox Pattern**: Ensures reliable event publishing with transactional consistency
- **Saga Pattern**: Orchestrates distributed transactions across services
- **Event-Driven Communication**: Services communicate asynchronously via Kafka
- **Security Layer**: JWT-based authentication and authorization
- **Monitoring Utilities**: Metrics, health checks, and logging utilities

## Technology Stack

### Backend Technologies
- **Language**: Java 17
- **Framework**: Spring Boot 3.2
- **Persistence**: Spring Data JPA with Hibernate
- **Database**: PostgreSQL (separate instances per service)
- **Message Broker**: Apache Kafka
- **Serialization**: JSON with Jackson
- **Validation**: Bean Validation (JSR 303)

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Orchestration**: Kubernetes
- **CI/CD**: Maven build system
- **Database Migration**: Flyway

### Monitoring & Observability
- **Metrics**: Micrometer with Prometheus
- **Visualization**: Grafana
- **Health Checks**: Spring Boot Actuator
- **Logging**: SLF4J with structured JSON logging

### Security
- **Authentication**: JWT (JSON Web Tokens)
- **Authorization**: Spring Security
- **Encryption**: BCrypt for password hashing

### Testing
- **Unit Testing**: JUnit 5
- **Integration Testing**: Spring Boot Test
- **Container Testing**: TestContainers
- **Mocking**: Mockito

## System Design

### Microservices Design Principles
- **Single Responsibility**: Each service handles a specific business domain
- **Loose Coupling**: Services communicate via well-defined APIs and events
- **Data Isolation**: Each service owns its data and provides access through APIs
- **Independent Deployability**: Services can be deployed independently

### Event-Driven Architecture
- **Asynchronous Communication**: Services communicate via events published to Kafka
- **Event Sourcing**: Business events are stored and processed for audit and replay
- **Eventual Consistency**: Services eventually reach a consistent state through events

### Data Management
- **Database Per Service**: Each service has its own database instance
- **Optimistic Locking**: Prevents concurrent modification conflicts
- **ACID Transactions**: Within each service boundary
- **Eventual Consistency**: Across service boundaries through events

### Resilience Patterns
- **Circuit Breaker**: Prevents cascading failures using Resilience4j
- **Retry Logic**: Automatic retry for transient failures with configurable policies
- **Bulkhead**: Resource isolation between different parts of the system
- **Timeouts**: Prevents hanging requests

### Kafka Delivery Semantics
- **At-Least-Once Delivery**: Configured with `acks=all`, retries, and idempotent producers
- **Manual Offset Management**: Consumers manually commit offsets after successful processing
- **Idempotent Processing**: Events are processed idempotently to handle duplicates
- **Transactional Events**: Outbox pattern ensures events are sent transactionally with business data

### Additional Features
- **Global Exception Handling**: Centralized error handling with proper HTTP status codes
- **Circuit Breaker Configuration**: Resilience4j integration for fault tolerance
- **Retry Mechanisms**: Configurable retry policies for transient failures
- **Custom Health Indicators**: Service-specific health checks
- **Structured Logging**: JSON-formatted logs with correlation IDs for distributed tracing

## Key Features

### Event-Driven Architecture
- Asynchronous communication between services
- Loose coupling and high scalability
- Event sourcing patterns
- Message ordering guarantees per user

### Reliability Patterns
- **Outbox Pattern**: Guarantees event delivery consistency
- **Saga Pattern**: Coordinates distributed transactions
- **Idempotency**: Prevents duplicate processing
- **Compensating Transactions**: Rollback mechanisms for failed operations
- **Kafka Delivery Semantics**: Configured for at-least-once delivery with proper acknowledgment settings

### Monitoring & Observability
- Health checks and metrics via Spring Boot Actuator
- Structured JSON logging with correlation IDs
- Prometheus metrics collection
- Preconfigured Grafana dashboards
- Distributed tracing capabilities

### Security
- JWT-based authentication
- Secure API endpoints
- Encrypted communication
- Input validation and sanitization

### Data Management
- Optimistic locking for concurrency control
- Database migrations with Flyway
- Indexes for performance optimization
- Audit trails for business events

### Scalability
- Horizontal scaling capabilities
- Load balancing support
- Caching strategies (ready for implementation)
- Database sharding readiness

## Business Flows

### User Registration Flow
1. User sends registration request to User Service
2. User Service validates input and creates user record
3. User Service publishes `UserRegistered` event to Kafka
4. Loyalty Service consumes event and creates loyalty account with welcome bonus
5. Response returned to client with user details

### Voucher Issuance Flow
1. External system or business rule triggers voucher creation
2. Voucher Service generates unique voucher code
3. Voucher Service creates voucher record in database
4. Voucher Service publishes `VoucherIssued` event to Kafka
5. Response returned with voucher details

### Voucher Redemption Flow
1. Client requests to redeem voucher with code and user ID
2. Voucher Service validates user, voucher existence, status, and expiration
3. Atomic update performed to change voucher status (optimistic locking)
4. Voucher Service publishes `VoucherRedeemed` event to Kafka
5. Response returned with redemption details

### Points Management Flow
1. Client requests to earn or redeem points
2. Loyalty Service validates request and checks available balance (for redemptions)
3. Loyalty Service updates account balances
4. Transaction record created for audit trail
5. Loyalty Service publishes `PointsEarned` or `PointsRedeemed` event to Kafka
6. Response returned with updated account balance

### Saga Pattern - Purchase with Voucher
1. Client initiates purchase with voucher
2. Saga orchestrator coordinates the process
3. Voucher redemption attempted first
4. If successful, purchase proceeds with discount applied
5. If any step fails, compensation actions are triggered

## Running the Application

### Prerequisites
- Docker and Docker Compose
- Java 17
- Maven 3.6+ or Gradle 8.5+
- At least 4GB RAM available for Docker

### Local Development

1. Clone the repository:
```bash
git clone <repository-url>
cd loyalty-platform
```

2. Build the services using Maven:
```bash
mvn clean install
```

Or build the services using Gradle:
```bash
./gradlew clean build
```

3. Start the infrastructure and services:
```bash
docker-compose up --build
```

4. Access the services:
   - User Service: http://localhost:8081
   - Voucher Service: http://localhost:8082
   - Loyalty Service: http://localhost:8083
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)

### Building Individual Services

To build individual services:

Maven:
```bash
# Build only user service
cd user-service
mvn clean install

# Build only voucher service
cd ../voucher-service
mvn clean install

# Build only loyalty service
cd ../loyalty-service
mvn clean install
```

Gradle:
```bash
# Build only user service
./gradlew :user-service:clean :user-service:build

# Build only voucher service
./gradlew :voucher-service:clean :voucher-service:build

# Build only loyalty service
./gradlew :loyalty-service:clean :loyalty-service:build
```

### Running Tests

Maven:
```bash
# Run all tests
mvn test

# Run tests for specific module
cd user-service
mvn test
```

Gradle:
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :user-service:test
```

### Kubernetes Deployment

1. Build and push Docker images to your registry
2. Update image tags in k8s/deployments.yaml
3. Apply Kubernetes manifests:
```bash
kubectl apply -f k8s/
```

## API Documentation

### User Service API

#### Create User
- **Endpoint**: `POST /api/users`
- **Request Body**:
```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phoneNumber": "string"
}
```
- **Response**: User details with generated user ID
- **Authentication**: None required

#### Get User by ID
- **Endpoint**: `GET /api/users/{userId}`
- **Response**: User details
- **Authentication**: None required

### Voucher Service API

#### Create Voucher
- **Endpoint**: `POST /api/vouchers`
- **Request Body**:
```json
{
  "userId": "string",
  "discountAmount": "decimal",
  "expirationDate": "datetime",
  "description": "string"
}
```
- **Response**: Voucher details with generated voucher ID and code
- **Authentication**: JWT required

#### Redeem Voucher
- **Endpoint**: `POST /api/vouchers/redeem`
- **Request Body**:
```json
{
  "voucherCode": "string",
  "userId": "string",
  "orderId": "string"
}
```
- **Response**: Updated voucher details
- **Authentication**: JWT required

#### Get Voucher by ID
- **Endpoint**: `GET /api/vouchers/{voucherId}`
- **Response**: Voucher details
- **Authentication**: JWT required

#### Get Vouchers by User ID
- **Endpoint**: `GET /api/vouchers/user/{userId}`
- **Response**: Array of user's vouchers
- **Authentication**: JWT required

### Loyalty Service API

#### Earn Points
- **Endpoint**: `POST /api/loyalty/earn`
- **Request Body**:
```json
{
  "userId": "string",
  "points": "decimal",
  "referenceId": "string",
  "reason": "string"
}
```
- **Response**: Updated loyalty account details
- **Authentication**: JWT required

#### Redeem Points
- **Endpoint**: `POST /api/loyalty/redeem`
- **Request Body**:
```json
{
  "userId": "string",
  "points": "decimal",
  "referenceId": "string",
  "reason": "string"
}
```
- **Response**: Updated loyalty account details
- **Authentication**: JWT required

#### Get Loyalty Account
- **Endpoint**: `GET /api/loyalty/account/{userId}`
- **Response**: Loyalty account details with balances
- **Authentication**: JWT required

### Saga Orchestration API

#### Execute Purchase with Voucher
- **Endpoint**: `POST /api/saga/purchase-with-voucher`
- **Request Body**:
```json
{
  "userId": "string",
  "orderId": "string",
  "orderAmount": "decimal",
  "voucherCode": "string"
}
```
- **Response**: Purchase result with discount applied
- **Authentication**: JWT required

### Health and Monitoring Endpoints

#### Health Check
- **Endpoint**: `GET /actuator/health`
- **Response**: Health status of the service

#### Metrics
- **Endpoint**: `GET /actuator/metrics`
- **Response**: Application metrics

#### Prometheus Metrics
- **Endpoint**: `GET /actuator/prometheus`
- **Response**: Metrics in Prometheus format

### Event-Driven Architecture

The services communicate asynchronously via Apache Kafka with at-least-once delivery semantics:

- **User Service** publishes `UserRegistered` events when new users register
- **Loyalty Service** listens to `UserRegistered` events and creates loyalty accounts with welcome bonuses
- **Voucher Service** publishes `VoucherIssued` and `VoucherRedeemed` events
- **Loyalty Service** listens to `PointsRedeemed` events and may issue reward vouchers
- All events are processed idempotently to handle potential duplicates
- Kafka is configured with `acks=all`, retries, and idempotent producers for durability

## Database Schema

### User Service Database (userdb)

#### users table
```sql
id BIGSERIAL PRIMARY KEY,
user_id VARCHAR(255) UNIQUE NOT NULL,
first_name VARCHAR(255) NOT NULL,
last_name VARCHAR(255) NOT NULL,
email VARCHAR(255) UNIQUE NOT NULL,
phone_number VARCHAR(50),
is_active BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
version INTEGER DEFAULT 0
```

### Voucher Service Database (voucherdb)

#### voucher_status enum
```sql
ENUM ('ACTIVE', 'USED', 'EXPIRED', 'CANCELLED')
```

#### vouchers table
```sql
id BIGSERIAL PRIMARY KEY,
voucher_id VARCHAR(255) UNIQUE NOT NULL,
user_id VARCHAR(255) NOT NULL,
voucher_code VARCHAR(255) UNIQUE NOT NULL,
status voucher_status NOT NULL DEFAULT 'ACTIVE',
discount_amount DECIMAL(19, 4) NOT NULL,
expiration_date TIMESTAMP WITH TIME ZONE NOT NULL,
description TEXT,
order_id VARCHAR(255),
created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
version INTEGER
```

### Loyalty Service Database (loyaltydb)

#### loyalty_accounts table
```sql
id BIGSERIAL PRIMARY KEY,
user_id VARCHAR(255) UNIQUE NOT NULL,
total_points DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
available_points DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
spent_points DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
version INTEGER
```

#### transaction_type enum
```sql
ENUM ('EARN', 'REDEEM')
```

#### points_transactions table
```sql
id BIGSERIAL PRIMARY KEY,
user_id VARCHAR(255) NOT NULL,
amount DECIMAL(19, 4) NOT NULL,
type transaction_type NOT NULL,
reference_id VARCHAR(255) NOT NULL,
description TEXT,
event_id VARCHAR(255) UNIQUE, -- For idempotency
created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
version INTEGER
```

### Common Database Components

#### outbox table
```sql
id BIGSERIAL PRIMARY KEY,
aggregate_type VARCHAR(255) NOT NULL,
aggregate_id VARCHAR(255) NOT NULL,
event_type VARCHAR(255) NOT NULL,
payload TEXT NOT NULL,
processed BOOLEAN NOT NULL DEFAULT FALSE,
processed_at TIMESTAMP WITH TIME ZONE,
created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
version INTEGER DEFAULT 0
```

## Event Flows

### Event Types
- `UserRegistered`: Published when a new user registers
- `VoucherIssued`: Published when a voucher is created
- `VoucherRedeemed`: Published when a voucher is redeemed
- `PointsEarned`: Published when points are earned
- `PointsRedeemed`: Published when points are redeemed

### Event Processing
- Events are published to Kafka topics named after the event type
- Each service subscribes to relevant topics
- Event processing is idempotent to handle duplicates
- Failed events are logged for manual intervention
- Kafka configured for at-least-once delivery semantics with proper acknowledgment settings

### Event Schema
All events extend the `BaseEvent` class with common fields:
- `eventId`: Unique identifier for the event
- `timestamp`: When the event was created
- `eventType`: Type of the event

## Testing

### Unit Tests
- Service layer logic
- Business rule validation
- Utility functions
- Event processing logic

### Integration Tests
- Database interactions using TestContainers
- API endpoint testing
- Cross-service communication simulation
- Transaction boundary testing

### Test Coverage
- Minimum 80% code coverage required
- Critical business logic has 100% coverage
- Edge cases and error conditions tested

Run unit and integration tests with Maven:
```bash
mvn test
```

Or run unit and integration tests with Gradle:
```bash
./gradlew test
```

Run tests with coverage report:
```bash
mvn test jacoco:report
```

Or with Gradle:
```bash
./gradlew test jacocoTestReport
```

## Monitoring

### Metrics Collection
- JVM metrics (heap usage, garbage collection, threads)
- HTTP metrics (response times, error rates, throughput)
- Database metrics (connection pool, query performance)
- Kafka metrics (consumer lag, throughput)

### Health Checks
- Database connectivity
- Kafka broker connectivity
- External service dependencies
- Disk space and memory usage

### Logging
- Structured JSON logs for easy parsing
- Correlation IDs for request tracing
- Different log levels for different environments
- Sensitive information filtered from logs

### Dashboards
- Preconfigured Grafana dashboard
- Key performance indicators
- Error rate monitoring
- System resource utilization

## Security

### Authentication
- JWT-based authentication
- Token expiration and refresh
- Secure token storage
- Password strength requirements

### Authorization
- Role-based access control
- API endpoint protection
- Data access restrictions
- Audit trail for sensitive operations

### Data Protection
- Input validation and sanitization
- SQL injection prevention
- Cross-site scripting (XSS) protection
- Secure communication (HTTPS/TLS)

### Secrets Management
- Environment variables for sensitive data
- Externalized configuration
- No hardcoded credentials
- Secure key rotation mechanisms

## Deployment

### Local Development
- Docker Compose for local environment
- Single command setup
- Isolated service instances
- Development-friendly configuration

### Staging Environment
- Kubernetes cluster
- Separate namespaces
- Automated CI/CD pipeline
- Blue-green deployment strategy

### Production Environment
- Multiple availability zones
- Auto-scaling based on load
- Disaster recovery procedures
- Backup and restore mechanisms

### Configuration Management
- Environment-specific configurations
- Externalized secrets
- Feature flags for gradual rollouts
- Configuration validation

## Best Practices Implemented

### Code Quality
- Clean architecture principles
- Proper separation of concerns
- SOLID principles adherence
- Consistent coding standards

### Error Handling
- Comprehensive error handling
- Graceful degradation
- Fallback mechanisms
- Detailed error logging

### Performance
- Database query optimization
- Connection pooling
- Caching strategies (ready for implementation)
- Efficient algorithms and data structures

### Maintainability
- Comprehensive documentation
- Clear code comments
- Modular design
- Easy configuration changes

### Scalability
- Horizontal scaling capabilities
- Load distribution
- Database optimization
- Asynchronous processing

### Security
- Input validation and sanitization
- Secure communication protocols
- Regular security audits
- Vulnerability scanning

## Project Structure

```
loyalty-platform/
├── common/                     # Shared components
│   ├── src/main/java/
│   │   └── com/mj/loyalty/common/
│   │       ├── config/         # Configuration classes
│   │       ├── entity/         # Shared entities
│   │       ├── events/         # Event definitions
│   │       ├── repository/     # Shared repositories
│   │       ├── saga/           # Saga orchestrators
│   │       ├── security/       # Security utilities
│   │       ├── service/        # Shared services
│   │       └── logging/        # Logging utilities
│   └── src/main/resources/
│       └── db/migration/       # Database migrations
├── user-service/               # User management service
│   ├── src/main/java/
│   │   └── com/mj/loyalty/userservice/
│   │       ├── controller/     # REST controllers
│   │       ├── dto/            # Data transfer objects
│   │       ├── entity/         # JPA entities
│   │       ├── repository/     # Data access layer
│   │       ├── service/        # Business logic
│   │       └── ...             # Other components
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/       # Database migrations
├── voucher-service/            # Voucher management service
│   ├── src/main/java/
│   │   └── com/mj/loyalty/voucherservice/
│   │       ├── controller/     # REST controllers
│   │       ├── dto/            # Data transfer objects
│   │       ├── entity/         # JPA entities
│   │       ├── enums/          # Enum definitions
│   │       ├── repository/     # Data access layer
│   │       ├── service/        # Business logic
│   │       ├── listener/       # Event listeners
│   │       └── scheduler/      # Scheduled tasks
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/       # Database migrations
├── loyalty-service/            # Loyalty points service
│   ├── src/main/java/
│   │   └── com/mj/loyalty/loyaltyservice/
│   │       ├── controller/     # REST controllers
│   │       ├── dto/            # Data transfer objects
│   │       ├── entity/         # JPA entities
│   │       ├── enums/          # Enum definitions
│   │       ├── repository/     # Data access layer
│   │       ├── service/        # Business logic
│   │       └── listener/       # Event listeners
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/       # Database migrations
├── k8s/                        # Kubernetes configurations
│   └── deployments.yaml
├── docker-compose.yml          # Docker Compose configuration
├── Dockerfile.*                # Docker build files
├── gradle/                     # Gradle wrapper
│   └── wrapper/
├── gradlew                     # Gradle wrapper script (Unix)
├── gradlew.bat                 # Gradle wrapper script (Windows)
├── build.gradle                # Gradle parent build file
├── settings.gradle             # Gradle settings
├── prometheus.yml              # Prometheus configuration
├── grafana-dashboards.yaml     # Grafana provisioning
├── dashboard.json              # Grafana dashboard definition
├── README.md                   # Project documentation
├── pom.xml                     # Maven parent POM
└── build.gradle                # Gradle build file
```

## Build Systems

This project supports both Maven and Gradle build systems:

### Maven
- Standard Maven build structure (`pom.xml`)
- Multi-module configuration
- Standard Maven lifecycle

### Gradle
- Modern Gradle build system (`build.gradle`)
- Multi-project configuration (`settings.gradle`)
- Spring Boot plugin integration
- Wrapper scripts for easy execution (`gradlew`, `gradlew.bat`)