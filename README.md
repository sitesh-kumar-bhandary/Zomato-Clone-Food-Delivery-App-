# 🍔 Zomato Clone — Food Delivery Backend

A backend-focused food delivery system built using **Spring Boot**, designed to handle real-world use cases such as authentication, restaurant & menu management, order processing, and **high-performance search using Elasticsearch**.

This project emphasizes **scalable backend architecture**, clean API design, and production-level features like **fuzzy search, ranking, and autocomplete**.

---

## 📌 Overview

The system provides RESTful APIs to manage:

* Restaurants and menu items
* User authentication and authorization
* Order placement and tracking
* Global search across restaurants and menu items

It integrates **Elasticsearch** to replace traditional database search, enabling:

* Fast full-text search
* Typo-tolerant queries
* Relevance-based ranking

Additionally, the system incorporates **Redis** for caching and performance optimization, along with **Redis-based distributed locking** to ensure safe concurrent operations such as order processing.

Background tasks are handled using **schedulers**, which manage operations like updating order/payment status asynchronously.

The project also includes **Swagger UI** for interactive API documentation and testing, and a structured **logging system** to monitor application behavior and assist in debugging.

Overall, the system is designed with a focus on **scalability, performance, and real-world backend practices**.


---

## 🛠 Tech Stack

#### Backend: Spring Boot

#### Language: Java 17

#### Database: MySQL

#### Caching : Redis

#### Search Engine: Elasticsearch

#### Security: Spring Security (JWT-based)

#### Logging: SLF4J / Logback

#### Build Tool: Maven

---

## ✨ Key Features

### 🔐 Authentication & Security

* JWT-based authentication
* Role-based access control (User / Admin / Restaurant Owner)
* Secure API endpoints

---

### 🏪 Restaurant Management

* Create, update, delete restaurants
* Retrieve all restaurants
* Fetch restaurant by ID

---

### 🍽️ Menu Item Management

* Create, update, delete menu items
* Public menu retrieval
* Owner-specific menu management

---

### 🛒 Order & Payment System

* Place orders
* Track order status
* Payment workflow handling

---

### 🔍 Elasticsearch-Based Search

Implemented a **production-grade search system**:

* Global search across restaurants and menu items
* Fuzzy search (handles typos like `piza → pizza`)
* Field boosting for ranking:

  * `name > city > description`
* Prefix-based autocomplete suggestions
* Pagination support for scalable results

---

## 🔍 Search Architecture

Instead of using:

```id="7rbcj7"
WHERE name LIKE '%pizza%'
```

The system uses **Elasticsearch**, enabling:

* ⚡ Faster search using inverted index
* 🎯 Relevance-based ranking
* 🔍 Typo tolerance (fuzzy search)
* 📈 Better scalability for large datasets

---

## 📡 API Endpoints

### 🔍 Global Search

```id="j8zy1b"
GET /search?query=pizza&page=0&size=10
```

---

### ⚡ Autocomplete

```id="0w2v4g"
GET /search/suggest?keyword=pi
```

---

## 📁 Project Structure

```id="v2u8yv"
controller/        → REST APIs (entry point for all client requests)
service/           → Business logic implementation
repository/        → Data access layer (MySQL + Elasticsearch)
document/          → Elasticsearch documents (indexed data models)
entity/            → JPA entities (database models)
dto/               → Request & response transfer objects
mapper/            → Entity ↔ DTO conversion logic
config/            → Security, Elasticsearch, and app configurations
filter/            → Request filters (JWT authentication, etc.)
exception/         → Global exception handling
enums/             → Application constants & enums
scheduler/         → Scheduled tasks (background jobs)
```

---

## ⚙️ Performance Optimization

* Replaced DB search with Elasticsearch
* Implemented fuzzy search and ranking
* Added pagination to handle large datasets efficiently
* Optimized query execution for faster response times

---

## 🧠 Design Decisions

* Used **MySQL** for transactional operations (CRUD, orders)
* Used **Elasticsearch** only for search use cases
* Avoided overusing ES for simple queries (best practice)
* Maintained separation between DB and search layer

---

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Maven
* MySQL
* Redis
* Elasticsearch (running on port 9200)

---

### Run Locally

```id="6k6v2v"
git clone https://github.com/sitesh-kumar-bhandary/Zomato-Clone-Food-Delivery-App-
cd Zomato-Clone-Food-Delivery-App-
mvn clean install
mvn spring-boot:run
```

---

### Run Elasticsearch (Docker)

```id="9az0pm"
docker run -d -p 9200:9200 \
-e "discovery.type=single-node" \
-e "xpack.security.enabled=false" \
docker.elastic.co/elasticsearch/elasticsearch:8.11.1
```

---

## 📊 DB vs Elasticsearch

| Feature       | MySQL         | Elasticsearch |
| ------------- | ------------- | ------------- |
| Search Speed  | Slow (`LIKE`) | ⚡ Fast        |
| Typo Handling | ❌ No          | ✅ Yes         |
| Ranking       | ❌ No          | ✅ Yes         |
| Scalability   | Limited       | High          |

---

## 🚧 In Progress

* API Rate Limiting
* Email Notifications

---

## 📈 Future Enhancements

* Audit Logs
* Promo Codes
* Google Maps API
* Deployment (AWS / Render)
* Monitoring and observability

---

## 👤 Author

**Sitesh Kumar Bhandary**
Backend Developer | Java | Spring Boot

* GitHub: https://github.com/sitesh-kumar-bhandary
* Linkedin : https://www.linkedin.com/in/sitesh-kumar-bhandary/
* LeetCode : https://leetcode.com/u/_sitesh_kumar/
