FROM node:20-alpine

WORKDIR /app

# Instalar dependências de desenvolvimento
COPY package*.json ./
COPY prisma ./prisma/

RUN npm install

# Copiar o resto do código
COPY . .

# Gerar cliente Prisma
RUN npx prisma generate

# Construir a aplicação
RUN npm run build

EXPOSE 8080

# Em desenvolvimento
CMD ["npm", "run", "dev"]
