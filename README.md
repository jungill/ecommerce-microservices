# E-Commerce Microservices — Spring Boot 3 + Angular 17

Application e-commerce full-stack construite avec une architecture microservices. Elle permet de gérer des produits et des commandes via une interface web moderne.

---

## Démo en ligne

L'application est déployée et accessible publiquement sur Render :

| Service | URL |
|---|---|
| **Frontend (UI)** | https://frontend-xxxx.onrender.com |
| **Gateway** | https://gateway-service-l5oh.onrender.com |
| **Product Service** | https://product-service-s05a.onrender.com |
| **Order Service** | https://order-service-davn.onrender.com |

> **Note :** Sur le plan gratuit Render, les services s'endorment après 15 minutes d'inactivité. Le premier chargement peut prendre 30 à 60 secondes.

---

## Architecture

```
Navigateur (Angular :4200)
        │
        ▼
  Gateway (:8080)          ← point d'entrée unique, gère le CORS
        │
        ├──/api/products/**──► product-service (:8081)
        │                           │
        │                      Base PostgreSQL
        │                       (schéma: product)
        │
        └──/api/orders/**───► order-service (:8082)
                                    │
                               Base PostgreSQL
                               (schéma: order_schema)
                                    │
                               Appelle product-service
                               via WebClient HTTP
```

### Services

| Service | Port | Rôle |
|---|---|---|
| `gateway-service` | 8080 | Point d'entrée unique — routing + CORS |
| `product-service` | 8081 | CRUD produits, recherche, validation |
| `order-service` | 8082 | Gestion commandes, appel WebClient vers product-service |
| `frontend` | 4200 | Interface Angular 17 standalone |

---

## Stack technique

**Back-end**
- Java 21
- Spring Boot 3.2.5
- Spring Cloud Gateway
- Spring Data JPA + Hibernate
- PostgreSQL
- Lombok
- Maven

**Front-end**
- Angular 17 (composants standalone)
- TypeScript
- HttpClient + Intercepteur d'erreurs
- Reactive Forms

**Infrastructure**
- Docker + Docker Compose
- Render (déploiement cloud)
- PostgreSQL (deux schémas séparés dans une même base)

---

## Endpoints disponibles

Tous les appels passent par le gateway (`:8080` en local, URL Render en production).

### Produits

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/products` | Lister tous les produits |
| `POST` | `/api/products` | Créer un produit |
| `GET` | `/api/products/{id}` | Détail d'un produit |
| `GET` | `/api/products/search?name=xxx` | Rechercher par nom |
| `PUT` | `/api/products/{id}` | Modifier un produit |
| `DELETE` | `/api/products/{id}` | Supprimer un produit |

**Exemple de body pour créer un produit :**
```json
{
  "name": "Clavier mécanique",
  "description": "Clavier RGB switches bleus",
  "price": 79.99,
  "stock": 15,
  "category": "Informatique"
}
```

### Commandes

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/orders` | Lister toutes les commandes |
| `POST` | `/api/orders` | Passer une commande |
| `GET` | `/api/orders/{id}` | Détail d'une commande |
| `PATCH` | `/api/orders/{id}/confirm` | Confirmer une commande |
| `PATCH` | `/api/orders/{id}/cancel` | Annuler une commande |

**Exemple de body pour passer une commande :**
```json
{
  "productId": 1,
  "quantity": 2
}
```

---

## Lancer le projet en local

### Prérequis

Avant de commencer, veuillez vous assurer d'avoir installé les outils suivants :

| Outil | Version minimale | Vérification |
|---|---|---|
| Java | 21 | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Node.js | 18+ | `node -v` |
| Docker Desktop | Dernière version | `docker -v` |

### Option 1 — Avec Docker Compose (recommandée)

Il s'agit de la façon la plus simple de lancer le projet — une seule commande suffit à démarrer l'ensemble des services.

```bash
# 1. Cloner le projet
git clone https://github.com/jungill/ecommerce-microservices.git
cd ecommerce-microservices

# 2. Lancer tous les services
docker-compose up --build
```

L'application sera accessible sur **http://localhost:4200**

Pour arrêter l'ensemble des services :
```bash
docker-compose down
```

### Option 2 — Sans Docker (lancement manuel)

#### Étape 1 — Lancer PostgreSQL

```bash
docker run --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  -d postgres
```

Créer la base de données et les schémas :
```bash
docker exec -it postgres psql -U postgres -c "CREATE DATABASE ecommercedb;"
docker exec -it postgres psql -U postgres -d ecommercedb -c "CREATE SCHEMA product;"
docker exec -it postgres psql -U postgres -d ecommercedb -c "CREATE SCHEMA order_schema;"
```

#### Étape 2 — Lancer les services Spring Boot

Veuillez ouvrir **4 terminaux** et lancer les commandes dans l'ordre suivant :

**Terminal 1 — product-service**
```bash
cd product-service
mvn spring-boot:run
```
Attendez le message `Started ProductServiceApplication` avant de continuer.

**Terminal 2 — order-service**
```bash
cd order-service
mvn spring-boot:run
```

**Terminal 3 — gateway-service**
```bash
cd gateway-service
mvn spring-boot:run
```

**Terminal 4 — Frontend Angular**
```bash
cd frontend
npm install   # uniquement lors du premier lancement
npm start
```

#### Étape 3 — Accéder à l'application

Ouvrez **http://localhost:4200** dans votre navigateur.

---

## Structure du projet

```
ecommerce-microservices/
│
├── product-service/                    ← Service produits (port 8081)
│   └── src/main/java/com/ecommerce/product/
│       ├── controller/                 ← ProductController, GlobalExceptionHandler
│       ├── service/                    ← ProductService (logique métier)
│       ├── repository/                 ← ProductRepository (JPA)
│       ├── model/                      ← Product (entité JPA)
│       ├── dto/                        ← ProductRequest, ProductResponse
│       └── config/                     ← CorsConfig
│
├── order-service/                      ← Service commandes (port 8082)
│   └── src/main/java/com/ecommerce/order/
│       ├── controller/                 ← OrderController, GlobalExceptionHandler
│       ├── service/                    ← OrderService, ProductClient (WebClient)
│       ├── repository/                 ← OrderRepository (JPA)
│       ├── model/                      ← Order, OrderStatus
│       ├── dto/                        ← OrderRequest, OrderResponse, ProductResponse
│       └── config/                     ← WebClientConfig, CorsConfig
│
├── gateway-service/                    ← Gateway (port 8080)
│   └── src/main/resources/
│       └── application.properties      ← Routing + CORS
│
├── frontend/                           ← Application Angular 17
│   └── src/app/
│       ├── core/
│       │   ├── models/                 ← Product, Order (interfaces TypeScript)
│       │   ├── services/               ← ProductService, OrderService (HttpClient)
│       │   └── interceptors/           ← errorInterceptor
│       └── features/
│           ├── products/               ← ProductListComponent, ProductFormComponent
│           └── orders/                 ← OrderListComponent, OrderFormComponent
│
├── docker-compose.yml                  ← Orchestration Docker
└── README.md                           ← Ce fichier
```

---

## Variables d'environnement

### product-service

| Variable | Description | Valeur par défaut |
|---|---|---|
| `JDBC_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5433/ecommercedb` |
| `PORT` | Port d'écoute | `8081` |

### order-service

| Variable | Description | Valeur par défaut |
|---|---|---|
| `JDBC_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5433/ecommercedb` |
| `PRODUCT_SERVICE_URL` | URL de product-service | `http://localhost:8081` |
| `PORT` | Port d'écoute | `8082` |

### gateway-service

| Variable | Description | Valeur par défaut |
|---|---|---|
| `PRODUCT_SERVICE_URL` | URL de product-service | `http://localhost:8081` |
| `ORDER_SERVICE_URL` | URL de order-service | `http://localhost:8082` |
| `PORT` | Port d'écoute | `8080` |

---

## Auteur

**Junior** — Développeur Full-Stack  
GitHub : [@jungill](https://github.com/jungill/ecommerce-microservices.git)