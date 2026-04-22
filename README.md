# E-Commerce — Spring Boot 3 + Angular 17

## Architecture

```
Angular (port 4200)
    └── Gateway (port 8080)  ← CORS configuré ici
          ├── /api/products/**  →  product-service (port 8081)
          └── /api/orders/**    →  order-service   (port 8082)
```

Pas d'Eureka. Communication inter-services via WebClient HTTP statique.

---

## Lancer le projet

### Backend (3 terminaux)
```bash
cd product-service && mvn spring-boot:run
cd order-service   && mvn spring-boot:run
cd gateway-service && mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start       # → http://localhost:4200
```

---

## Endpoints Backend (via gateway :8080)

### Products
| Méthode | URL                           | Description          |
|---------|-------------------------------|----------------------|
| GET     | /api/products                 | Lister              |
| POST    | /api/products                 | Créer               |
| GET     | /api/products/{id}            | Détail              |
| GET     | /api/products/search?name=x   | Recherche           |
| PUT     | /api/products/{id}            | Modifier            |
| DELETE  | /api/products/{id}            | Supprimer           |

### Orders
| Méthode | URL                           | Description          |
|---------|-------------------------------|----------------------|
| GET     | /api/orders                   | Lister              |
| POST    | /api/orders                   | Créer               |
| GET     | /api/orders/{id}              | Détail              |
| PATCH   | /api/orders/{id}/confirm      | Confirmer           |
| PATCH   | /api/orders/{id}/cancel       | Annuler             |

---

## Consoles H2 (dev)
- http://localhost:8081/h2-console  (jdbc:h2:mem:productdb)
- http://localhost:8082/h2-console  (jdbc:h2:mem:orderdb)
