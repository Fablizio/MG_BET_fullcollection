# syntax=docker/dockerfile:1

### 1) Install deps (incluse devDeps per compilare con tsup)
FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
# Se hai moduli nativi, abilita gli strumenti (in genere non servono):
# RUN apk add --no-cache python3 make g++
RUN npm ci --no-audit --no-fund

### 2) Build con tsup
FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
COPY tsconfig.json ./
COPY src ./src
COPY --from=deps /app/node_modules ./node_modules
RUN npm run build   # esegue: tsup src/index.ts --format esm --dts --sourcemap --clean

### 3) Deps di produzione (senza devDeps)
FROM node:20-alpine AS prod-deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --omit=dev --no-audit --no-fund

### 4) Runtime
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
# utile per stacktrace con sourcemap generate da tsup
ENV NODE_OPTIONS=--enable-source-maps

COPY package.json ./
COPY --from=prod-deps /app/node_modules ./node_modules
COPY --from=builder /app/dist ./dist

EXPOSE 3000
CMD ["node", "dist/index.js"]
