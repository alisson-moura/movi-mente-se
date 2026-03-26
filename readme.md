# Movimente-se

Sistema de gerenciamento de academias corporativas — permite que empresas disponibilizem uma academia interna para seus funcionários, com agendamento de treinos, controle de vagas e gestão de mensalidades.

> **Status:** Em desenvolvimento ativo

## Funcionalidades

### Disponíveis
- Cadastro de alunos com vínculo a empresa e grupo
- Autenticação via sessão com cookie HTTP-only (token de 96 chars, expiração em 30 dias)
- Suporte a grupos pagantes e não pagantes

### Planejadas
- Agendamento de treinos com vagas limitadas por horário (ex: Pilates 18h–19h, 20 vagas)
- Controle de frequência
- Registro e cobrança de mensalidades
- Notificações
- Administração de agenda

## Tecnologias

- **Java 25**
- **Spring Boot 4.0.2** (MVC, JDBC)
- **PostgreSQL 16**
- **Flyway** — migrações de banco de dados
- **BCrypt** — hash de senhas (custo 12)
- **Docker Compose** — ambiente local
- **Maven**

> Sem JPA/Hibernate. Acesso ao banco via JDBC puro.

## Pré-requisitos

- Java 25+
- Maven 3.9+
- Docker e Docker Compose

## Variáveis de ambiente

A aplicação usa variáveis de ambiente para configurações sensíveis e dinâmicas. Copie o arquivo de exemplo e preencha os valores:

```bash
cp .env.example .env
```

| Variável        | Descrição                          | Padrão                                    |
|-----------------|------------------------------------|-------------------------------------------|
| `DB_URL`        | URL de conexão JDBC com o banco    | `jdbc:postgresql://localhost/movimente-se` |
| `DB_USERNAME`   | Usuário do banco de dados          | `admin`                                   |
| `DB_PASSWORD`   | Senha do banco de dados            | —                                         |
| `DB_POOL_MAX`   | Máximo de conexões no pool         | `10`                                      |
| `DB_POOL_MIN_IDLE` | Mínimo de conexões ociosas      | `5`                                       |

> O arquivo `.env` **não deve ser commitado**. Apenas o `.env.example` (sem valores sensíveis) é versionado.

## Instalação e execução

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/movimente-se.git
cd movimente-se

# 2. Configure as variáveis de ambiente
cp .env.example .env
# edite o .env com os valores corretos

# 3. Suba o banco de dados
docker compose up -d

# 4. Execute a aplicação
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

As migrações do banco são aplicadas automaticamente via Flyway na inicialização.

## Testes

Os testes são E2E e rodam contra o PostgreSQL real. Certifique-se de que o banco está rodando antes de executar.

```bash
# Rodar todos os testes
mvn test

# Rodar um teste específico
mvn test -Dtest=CadastrarAlunoE2ETest
```

## Estrutura do projeto

```
src/
├── main/
│   ├── java/.../
│   │   ├── controller/     # Endpoints REST
│   │   ├── entity/         # Entidades e regras de negócio
│   │   ├── repository/     # Acesso ao banco (JDBC)
│   │   ├── dto/            # Records de entrada/saída
│   │   ├── exception/      # Erros mapeados para HTTP
│   │   └── infra/          # Configurações (Clock, Flyway)
│   └── resources/
│       └── db/migration/   # Migrações SQL (Flyway)
└── test/
    └── java/.../           # Testes E2E por domínio
```

## Endpoints

| Método | Rota              | Descrição                        |
|--------|-------------------|----------------------------------|
| POST   | `/alunos`         | Cadastrar aluno                  |
| POST   | `/alunos/sessoes` | Login (cria sessão)              |
| GET    | `/alunos/sessoes` | Renovar sessão ativa via cookie  |
| GET    | `/status`         | Health check                     |

## Banco de dados

| Tabela    | Descrição                               |
|-----------|-----------------------------------------|
| `empresas`| Empresas conveniadas                    |
| `grupos`  | Grupos de acesso (pagante/não pagante)  |
| `alunos`  | Funcionários cadastrados como alunos    |
| `sessoes` | Sessões autenticadas                    |

## Contribuindo

Contribuições são bem-vindas!

1. Fork o repositório
2. Crie uma branch: `git checkout -b feat/minha-feature`
3. Faça commit das suas alterações: `git commit -m 'feat: descrição'`
4. Push para a branch: `git push origin feat/minha-feature`
5. Abra um Pull Request

## Licença

Este projeto é open source e de uso livre, sem restrições.
