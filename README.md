# Spring Boot + MongoDB CRUD API

## Stack

| Component           | Version     |
| ------------------- | ----------- |
| Java                | 21 (LTS)    |
| Spring Boot         | 3.4.5 (LTS) |
| MongoDB             | 7.0 (LTS)   |
| Spring Data MongoDB | 4.x         |

---

## Project Structure

```
src/main/java/com/example/crud/
├── SpringbootMongoCrudApplication.java   # Entry point
├── MongoConfig.java                      # Auditing config
├── model/
│   └── Product.java                      # MongoDB document
├── dto/
│   ├── ProductRequest.java               # Input DTO (validated)
│   ├── ProductResponse.java              # Output DTO
│   └── ApiResponse.java                  # Generic wrapper
├── repository/
│   └── ProductRepository.java            # MongoRepository + custom queries
├── service/
│   └── ProductService.java               # Business logic
├── controller/
│   └── ProductController.java            # REST endpoints
└── exception/
    ├── ResourceNotFoundException.java
    ├── DuplicateResourceException.java
    └── GlobalExceptionHandler.java
```

---

## Run with Docker Compose (recommended)

```bash
# 1. Copy env file
cp .env.example .env

# 2. Build and start everything
docker-compose up --build

# 3. Stop
docker-compose down

# 4. Stop and remove volumes (clears MongoDB data)
docker-compose down -v
```

---

## Run Locally (without Docker)

```bash
# Requires Java 21 + MongoDB 7.0 running on localhost:27017
mvn spring-boot:run
```

---

## API Reference

Base URL: `http://localhost:8080/api/v1/products`

### Create a Product

```
POST /api/v1/products
Content-Type: application/json

{
  "name": "Laptop Pro",
  "description": "High-performance laptop",
  "price": 1299.99,
  "quantity": 10,
  "category": "Electronics"
}
```

### Get All Products (paginated)

```
GET /api/v1/products?page=0&size=10&sortBy=createdAt&sortDir=desc
```

### Get Product by ID

```
GET /api/v1/products/{id}
```

### Get by Category

```
GET /api/v1/products/category/Electronics
```

### Search by Name

```
GET /api/v1/products/search?name=laptop
```

### Filter by Price Range

```
GET /api/v1/products/price-range?min=100&max=500
```

### Update a Product

```
PUT /api/v1/products/{id}
Content-Type: application/json

{
  "name": "Laptop Pro Max",
  "description": "Updated description",
  "price": 1499.99,
  "quantity": 5,
  "category": "Electronics"
}
```

### Delete a Product

```
DELETE /api/v1/products/{id}
```

### Health Check

```
GET /actuator/health
```

---

## Response Format

All endpoints return a consistent JSON wrapper:

```json
{
  "success": true,
  "message": "Product created successfully",
  "data": { ... },
  "timestamp": "2025-01-01T10:00:00"
}
```

Validation errors return HTTP 400 with field-level details:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "price": "Price must be greater than 0",
    "name": "Name is required"
  }
}
```
