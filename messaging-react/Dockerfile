# Stage 1 - build sources
FROM node:16.20.0-alpine3.17 as build-deps
WORKDIR /usr/src/app
COPY package.json public src .env yarn.lock ./
ARG NPM_REGISTRY
RUN yarn install --no-cache --registry ${NPM_REGISTRY}
# probably this copy can be removed:
COPY . ./

RUN yarn run build

# Stage 2 - the production environment
FROM nginx:1.12-alpine
COPY --from=build-deps /usr/src/app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
