# 🚀 NOTO - Back-end API (Spring Boot)

> **Sistema de anotações pessoais moderno e seguro** - inspirado no Notion, agora refatorado para **Java + Spring Boot** com foco em privacidade, performance e arquitetura escalável.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

[V1 do Projeto com Express e Typescript](https://github.com/bielsolosos/Noto-Back-end/tree/release-1)
- 🌐 [Front-end do projeto](https://github.com/bielsolosos/Noto-Front-end)
- 📱 [Demo ao vivo](https://noto.bielsolososdev.space/)

## 🏗️ Arquitetura Atual

### **Stack Tecnológica**

- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.5 (Web, Validation, Security, Data JPA)
- **Banco de dados**: PostgreSQL
- **Migrações**: Flyway
- **Autenticação**: JWT (access token) + refresh token
- **Build**: Maven Wrapper (`mvnw` / `mvnw.cmd`)
- **Testes**: JUnit 5 (estrutura inicial)

### **Arquitetura em Camadas (DDD-inspired)**

```text
src/main/java/space/bielsolososdev/noto/
├── api/
│   ├── annotations/         # Anotações de autorização (@IsAdmin)
│   ├── controller/rest/     # Endpoints REST
│   ├── mapper/              # Mapeamento DTO <-> domínio
│   └── model/               # DTOs de request/response
├── core/
│   ├── exception/           # Exceções de negócio e tratamento global
│   ├── security/            # Configuração de segurança e filtro JWT
│   └── utils/               # Utilitários (JWT)
├── domain/
│   ├── pages/               # Entidade, repositório e serviço de páginas
│   └── users/               # Entidades, repositórios e serviços de usuários
└── infrastructure/
    └── NotoProperties       # Propriedades tipadas da aplicação
```

### **Fluxo de Requisição**

1. **Cliente** envia request com/sem JWT
2. **SecurityFilterChain** valida rotas públicas e protegidas
3. **JwtAuthenticationFilter** autentica o usuário via token
4. **Controller REST** recebe DTO e delega ao serviço
5. **Service Layer** aplica regras de negócio/permissão
6. **Repository (JPA)** persiste/consulta no PostgreSQL
7. **GlobalExceptionHandler** padroniza respostas de erro

## 🎯 Funcionalidades Implementadas

### **🔐 Autenticação e Sessão**

- ✅ Login com usuário/senha
- ✅ Geração de JWT assinado (HMAC)
- ✅ Refresh token com rotação (token antigo é consumido)
- ✅ Rotas protegidas por Spring Security
- ✅ Senhas protegidas com BCrypt

### **📝 Gestão de Páginas Privadas**

- ✅ Criar página
- ✅ Listar páginas do usuário autenticado
- ✅ Buscar página por ID
- ✅ Atualizar título/conteúdo
- ✅ Excluir página
- ✅ Isolamento entre usuários (validação de permissão por owner)

### **👤 Gestão de Usuário**

- ✅ Registro (controlado por flag `REGISTRATION_ENABLED`)
- ✅ Consulta de perfil autenticado (`/api/me`)
- ✅ Edição de username/email
- ✅ Alteração de senha com validação da senha atual
- ✅ Regras de unicidade para username/email

### **🛡️ Administração (ROLE_ADMIN)**

- ✅ Listagem paginada de usuários com filtros
- ✅ Edição de credenciais de qualquer usuário
- ✅ Alteração de senha de usuário
- ✅ Ativar/desativar conta
- ✅ Remover usuário

### **⚙️ Confiabilidade e Segurança**

- ✅ Validação de entrada com Jakarta Validation
- ✅ Tratamento global de exceções com respostas padronizadas
- ✅ Migrações versionadas com Flyway
- ✅ CORS configurado para front-end local

## 🌐 Endpoints Principais

### **Auth**

- `POST /api/auth/login` - login e emissão de tokens
- `POST /api/auth/refresh` - renova access token com refresh token

### **Usuário autenticado**

- `GET /api/me` - dados do usuário logado
- `POST /api/users/change-password` - altera senha própria
- `POST /api/users/edit-credentials` - altera username/email

### **Cadastro**

- `POST /api/users/register` - cria usuário (quando habilitado)

### **Páginas**

- `GET /api/pages/list` - lista resumida de páginas
- `GET /api/pages/{id}` - detalhe da página
- `POST /api/pages` - cria nova página
- `PUT /api/pages/{id}` - atualiza página
- `DELETE /api/pages/{id}` - remove página

### **Admin**

- `GET /api/admin/users` - lista paginada com filtros (`filter`, `isActive`, `createdAfter`, `createdBefore`)
- `GET /api/admin/users/list` - listagem simples (endpoint auxiliar/legado)
- `PATCH /api/admin/users/{id}/credentials` - edita credenciais
- `PATCH /api/admin/users/{id}/password` - altera senha
- `PATCH /api/admin/users/{id}/toggle-active` - ativa/desativa usuário
- `DELETE /api/admin/users/{id}` - exclui usuário

## 🔧 Configuração de Ambiente

Crie um arquivo `.env` baseado no `.env.example`:

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/noto-db
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Server
PORT=8080

# JPA
SHOW_SQL=false

# JWT
JWT_SECRET=base64-secret-key
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=86400000

# App
REGISTRATION_ENABLED=false
LOG_LEVEL=DEBUG
```

## 🚦 Como Executar

### **Pré-requisitos**

- Java 21+
- PostgreSQL em execução

### **Desenvolvimento local**

```bash
# 1) Clone o repositório
git clone https://github.com/bielsolosos/Noto-Back-end.git
cd Noto-Back-end

# 2) Crie o arquivo de ambiente
cp .env.example .env

# 3) Suba a API (Flyway executa automaticamente no startup)
./mvnw spring-boot:run
```

No Windows (Prompt/PowerShell), use:

```bash
copy .env.example .env
mvnw.cmd spring-boot:run
```

API disponível em: `http://localhost:8080`

### **Rodar testes**

Os testes usam sempre o profile `test` e um PostgreSQL externo. Configure as variaveis abaixo no ambiente:

```env
TEST_DB_URL=jdbc:postgresql://localhost:5432/noto-test-db
TEST_DB_USERNAME=postgres
TEST_DB_PASSWORD=postgres
TEST_SHOW_SQL=false
```

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

## 🗄️ Banco de Dados e Migrações

- Migrações em `src/main/resources/db/migration`
- Versões atuais:
  - `V1__create_users_table.sql`
  - `V2__insert_default_admin.sql`
  - `V3__migrate_to_uuid.sql`
  - `V4__create_pages_table.sql`

### **Usuário admin padrão (ambiente dev)**

- **Login**: `admin`
- **Senha**: `admin123`

> Recomenda-se alterar a senha do admin imediatamente após o primeiro login em qualquer ambiente real.

## 📌 Observações Importantes

- O refresh token atual é mantido em memória da aplicação (in-memory). Em ambiente distribuído/escala horizontal, o ideal é persistir em Redis ou banco.
- O cadastro público pode ser desligado com `REGISTRATION_ENABLED=false`.
- No estado atual, não há Swagger/OpenAPI nem Actuator expostos.

## 🔮 Roadmap (Próximos Passos)

- 📖 Documentação OpenAPI/Swagger
- ❤️ Health checks e observabilidade com Actuator
- 🧪 Testes de integração para fluxos críticos
- 📦 Persistência de refresh token (Redis/PostgreSQL)
- 🗂️ Exposição de endpoints de arquivamento de páginas

---

**🎯 NOTO - Back-end Java/Spring Boot**

Criado com ❤️ por **[bielsolosos](https://discord.com/users/bielsolosos)**

📧 **Contato**: Discord para dúvidas e colaborações
