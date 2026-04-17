# CI/CD Guide - Noto Back-end

## Objetivo
Este documento explica como funciona o CI do back-end, quais etapas bloqueiam merge, quais sao informativas e como reproduzir os checks localmente.

## Workflow
- Arquivo: `.github/workflows/ci.yml`
- Nome: `CI - Backend Quality Gate`
- Gatilhos:
  - Push: `main`, `develop`
  - Pull request: `main`, `develop`

## Jobs e Steps

### 1) verify (bloqueante)
Objetivo: validar testes, cobertura e qualidade base do build.

Steps principais:
1. Checkout do codigo
2. Setup Java 21 + cache Maven
3. `mvn -B clean verify`
4. Publicacao de artefatos:
   - `backend-jacoco-report`
   - `backend-surefire-reports`

Infra de teste:
- PostgreSQL `15-alpine` como service no workflow
- Variaveis injetadas no job:
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/noto_test`
  - `SPRING_DATASOURCE_USERNAME=postgres`
  - `SPRING_DATASOURCE_PASSWORD=test`

### 2) build-package (bloqueante)
Objetivo: testar empacotamento da aplicacao, separado dos testes.

Steps principais:
1. Checkout do codigo
2. Setup Java 21 + cache Maven
3. `mvn -B -DskipTests clean package`
4. Publicacao do artefato `backend-jar`

### 3) sonarcloud (informativo)
Objetivo: analise estatica adicional sem bloquear merge.

Comportamento:
- Depende de `verify`
- Roda apenas se `SONAR_TOKEN` estiver configurado
- Etapa Sonar usa `continue-on-error: true`
- Se faltar `SONAR_TOKEN`, o job registra resumo e encerra sem erro

Comando base usado:
- `mvn -B -DskipTests test-compile sonar:sonar ...`

## Bloqueio de merge
Checks bloqueantes:
- `verify`
- `build-package`

Check informativo:
- `sonarcloud`

## Secrets e Variaveis

### Secrets
- `SONAR_TOKEN` (opcional para Sonar)
- `GITHUB_TOKEN` (fornecido automaticamente pelo GitHub Actions)

### Variaveis
Nao ha variaveis custom obrigatorias para o workflow atual.

## Como rodar localmente

Prerequisitos:
- Java 21
- PostgreSQL ativo para os testes

Comandos:
```bash
mvn -B clean verify
mvn -B -DskipTests clean package
```

Opcional (Sonar local):
```bash
mvn -B -DskipTests test-compile sonar:sonar \
  -Dsonar.projectKey=bielsolosos_Noto-Back-end \
  -Dsonar.organization=bielsolosos \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=<seu-token>
```

## Troubleshooting rapido

### 1) Falha de conexao com PostgreSQL nos testes
- Confirme se o banco local esta ativo
- Confirme credenciais/URL do profile de teste

### 2) Falha de cobertura JaCoCo
- Revise resultados em `target/site/jacoco/index.html`
- Garanta que testes novos cobrem regras de negocio

### 3) Sonar nao executa no CI
- Verifique se `SONAR_TOKEN` esta configurado no repositorio
- Sem token, o job e pulado por design

### 4) Pipeline repetido em pushes rapidos
- O workflow usa `concurrency` para cancelar runs antigos da mesma branch
