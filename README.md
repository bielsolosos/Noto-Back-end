# Noto - Back-end

> API de anotações pessoais inspirada no Notion, com foco em privacidade, organização e extensibilidade.

- [Front-end do projeto](https://github.com/bielsolosos/Noto-Front-end)

## � Funcionalidades Implementadas

- CRUD completo de páginas privadas por usuário
- Autenticação JWT (login, proteção de rotas, refresh token)
- Gerenciamento de usuários (cadastro, login, alteração de senha, exclusão, promoção/demissão de admin)
- Logs estruturados para auditoria
- Validação de dados com Zod
- Documentação automática via Swagger
- Estrutura modular (Domain, Core, API)
- Deploy pronto

## 📄 Estado Atual

- Todas as funcionalidades principais implementadas
- Estrutura pronta para expansão futura (páginas públicas, estatísticas, SEO)
  - Incluir geração de _Access Token_ e _Refresh Token_.
  - Garantir a segurança das rotas e das páginas.

2.  👤 **Criar sistema de cadastro e autenticação de usuários**:
    - Permitir que cada usuário tenha sua conta.
    - As páginas ficarão associadas ao usuário autenticado.
3.  🕶️ **Gerenciar a visibilidade das páginas**:
    - Cada página será visível apenas para o dono.
    - Possibilitar no futuro o compartilhamento entre usuários.

## 🛠️ Considerações Técnicas

A aplicação é construída em **TypeScript**, utilizando **Express** como framework HTTP e **Prisma** como ORM para persistência dos dados.

Toda a estrutura foi pensada para ser modular e de fácil manutenção, com separação clara de responsabilidades entre: **modelos de domínio**, **funções utilitárias** e **camada de exposição**.

## 🤝 Contribuições

O projeto está aberto a melhorias, seja na arquitetura, segurança, ou na adição de novas funcionalidades.

---

🚀 Projeto criado com ❤ por **bielsolosos**
