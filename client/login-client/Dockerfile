FROM node:10-alpine as builder

COPY package.json package-lock.json ./

RUN npm install && mkdir /app-login && mv ./node_modules ./app-login

WORKDIR /app-login

COPY . .

RUN npm run ng build -- --deploy-url=/last-logins/ --prod


FROM nginx:alpine

#!/bin/sh

COPY ./.nginx/nginx.conf /etc/nginx/nginx.conf

## Remove default nginx index page
RUN rm -rf /usr/share/nginx/html/*

COPY --from=builder /app-login/dist /usr/share/nginx/html

EXPOSE 4200 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]
