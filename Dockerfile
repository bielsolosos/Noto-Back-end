# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar arquivos de dependência
COPY package.json yarn.lock ./
COPY prisma ./prisma/

# Instalar todas as dependências (incluindo dev)
RUN yarn install --frozen-lockfile

# Copiar código fonte
COPY . .

# Garantir permissões de execução
RUN chmod -R +x node_modules/.bin

# Gerar Prisma Client
RUN yarn prisma generate

# Build da aplicação
RUN yarn build

# Stage 2: Production
FROM node:20-alpine AS runner

WORKDIR /app

# Copiar arquivos necessários
COPY package.json yarn.lock ./
COPY prisma ./prisma/

# Instalar apenas dependências de produção
RUN yarn install --frozen-lockfile --production

# Copiar build e prisma gerado do stage anterior
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules/.prisma ./node_modules/.prisma
COPY --from=builder /app/node_modules/@prisma ./node_modules/@prisma

EXPOSE 8080

# Comando de produção
CMD ["yarn", "start"]
