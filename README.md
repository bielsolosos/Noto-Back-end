📝 Noto - Backend
=================

O **Noto** é um sistema de anotações pessoais inspirado no Notion, criado para permitir que cada usuário tenha controle total sobre suas páginas de anotações, organizando ideias, tarefas e projetos de maneira eficiente.

Pode conferir o front-end aqui: [Front-end] (https://github.com/bielsolosos/Noto-Front-end)
🏛️ Arquitetura do Projeto
--------------------------

O projeto segue uma organização modular, inspirada em conceitos de design orientado a domínio (DDD), mas com adaptações práticas:

*   **Domain**:
    *   Responsável pela modelagem do domínio.
    *   Contém as entidades centrais do sistema.
    *   No momento, inclui a entidade _Page_, representando uma anotação pessoal com atributos como título, data de criação e última atualização.
*   **Core**:
    *   Camada de **utilitários e serviços auxiliares** da aplicação.
    *   Inclui funções genéricas, validações, helpers e módulos de suporte reutilizáveis.
    *   Facilita a manutenção, evitando repetição de código em diferentes partes do projeto.
*   **API**:
    *   Responsável pela interface com o mundo externo.
    *   Gerencia as rotas, middlewares, segurança e exposição da API RESTful.
    *   Atualmente, expõe operações de CRUD sobre as páginas.

🎯 Objetivo e Proposta
----------------------

Criar uma plataforma de anotações pessoais, onde cada usuário possa ter suas próprias páginas privadas para organizar ideias e conteúdos.

A longo prazo, o projeto busca evoluir para uma aplicação completa de produtividade, mantendo-se simples e extensível.

📄 Estado Atual
---------------

*   ✅ CRUD de **Páginas** implementado e funcional.
*   ✅ Estrutura modular com separação entre domínio, utilitários e exposição de API.
*   ✅ Deploy realizado via Railway.

🚧 Próximos Passos (Roadmap)
----------------------------

1.  🔐 **Implementar autenticação via JWT**:
    *   Incluir geração de _Access Token_ e _Refresh Token_.
    *   Garantir a segurança das rotas e das páginas.
2.  👤 **Criar sistema de cadastro e autenticação de usuários**:
    *   Permitir que cada usuário tenha sua conta.
    *   As páginas ficarão associadas ao usuário autenticado.
3.  🕶️ **Gerenciar a visibilidade das páginas**:
    *   Cada página será visível apenas para o dono.
    *   Possibilitar no futuro o compartilhamento entre usuários.

🛠️ Considerações Técnicas
--------------------------

A aplicação é construída em **TypeScript**, utilizando **Express** como framework HTTP e **Prisma** como ORM para persistência dos dados.

Toda a estrutura foi pensada para ser modular e de fácil manutenção, com separação clara de responsabilidades entre: **modelos de domínio**, **funções utilitárias** e **camada de exposição**.

🤝 Contribuições
----------------

O projeto está aberto a melhorias, seja na arquitetura, segurança, ou na adição de novas funcionalidades.

* * *

🚀 Projeto criado com ❤ por **bielsolosos**
