# Étape 1 : Utiliser une image Node pour construire l'application
FROM node:18 AS build

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier les fichiers package.json et package-lock.json dans le conteneur
COPY package.json package-lock.json ./

# Installer les dépendances
RUN npm install

# Copier le code source dans le conteneur
COPY . .

# Construire l'application pour la production
RUN npm run build

# Étape 2 : Utiliser Nginx pour servir les fichiers statiques
FROM nginx:1.25

# Copier les fichiers construits dans le répertoire par défaut d'Nginx
COPY --from=build /app/build /usr/share/nginx/html

# Exposer le port 80
EXPOSE 80

# Commande par défaut pour démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
