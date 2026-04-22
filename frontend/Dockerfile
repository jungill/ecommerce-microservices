# ── Étape 1 : Build Angular ───────────────────────────────────────────────────
FROM node:20-alpine AS build
WORKDIR /app

# Copie package.json et installe les dépendances
COPY frontend/package*.json ./
RUN npm install

# Copie le code source et compile
COPY frontend/ ./
RUN npm run build

# ── Étape 2 : Serveur Nginx pour servir le front ──────────────────────────────
FROM nginx:alpine
COPY --from=build /app/dist/ecommerce-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
