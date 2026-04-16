# =============================================
# Stage 1: Build
# =============================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache do Docker
# (as dependências só são baixadas novamente se o pom.xml mudar)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila, pulando os testes
COPY src ./src
RUN mvn package -DskipTests -B

# =============================================
# Stage 2: Runtime
# =============================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Cria um usuário não-root por segurança
RUN addgroup -S noto && adduser -S noto -G noto

# Copia o JAR gerado pelo build
COPY --from=build /app/target/*.jar app.jar

# Ajusta a propriedade do arquivo para o usuário não-root
RUN chown noto:noto app.jar

USER noto

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
