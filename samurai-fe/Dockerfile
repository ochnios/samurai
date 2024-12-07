FROM node:22.3.0-alpine3.20 AS build

WORKDIR /usr/src/app

COPY package.json yarn.lock .yarnrc.yml ./

COPY .yarn ./.yarn

RUN --mount=type=cache,target=/usr/src/app/.cache \
    yarn install

COPY . .

ARG VITE_BASE_URL
ENV VITE_BASE_URL=${VITE_BASE_URL}

RUN yarn build

FROM nginxinc/nginx-unprivileged:alpine3.20-perl

COPY --link nginx.conf /etc/nginx/conf.d/default.conf

COPY --link --from=build usr/src/app/dist /usr/share/nginx/html

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]