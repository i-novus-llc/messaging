ARG APP_ENV=dev

# Stage 1a - build sources
FROM node:7.10 as dev-build-deps
WORKDIR /usr/src/app
COPY package.json public src ./
ARG NPM_REGISTRY
RUN npm install --registry ${NPM_REGISTRY}
COPY build ./
RUN npm run build

# Stage 1b - external build
FROM scratch as ext-build-deps
WORKDIR /usr/src/app
COPY build ./

# Stage 2 - the production environment
FROM nginx:1.12-alpine
COPY --from=${APP_ENV}-build-deps /usr/src/app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
