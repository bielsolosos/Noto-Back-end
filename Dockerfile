FROM node:20-alpine

WORKDIR /app

# Instalar dependências de desenvolvimento
COPY package.json yarn.lock ./
COPY prisma ./prisma/

RUN yarn install --frozen-lockfile

# Copiar o resto do código
COPY . .

# Gerar cliente Prisma
RUN yarn prisma generate

# Construir a aplicação
RUN yarn build

EXPOSE 8080

# Em desenvolvimento
CMD ["yarn", "dev"]
