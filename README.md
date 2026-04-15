# Order System API

API REST para gerenciamento de produtos, pedidos, usuarios e estoque. O projeto concentra uma base de back-end Java com Spring Boot, autenticacao JWT, autorizacao por perfil, JPA/Hibernate, MySQL, Docker, testes unitarios e documentacao de API.

## Visao geral tecnica

- API REST organizada por camadas, com controllers, services, repositories, DTOs e entidades JPA.
- Autenticacao stateless com JWT e autorizacao por perfil usando Spring Security.
- Regras de negocio para fluxo de pedidos, atualizacao de status e controle de estoque.
- Tratamento global de excecoes e validacao de entrada com Bean Validation.
- Testes unitarios para regras da camada de servico.
- Ambiente local com Docker Compose, MySQL e variaveis de configuracao por `.env`.
- Documentacao interativa com Swagger/OpenAPI e pipeline de CI executando `mvn test`.

## Stack

| Area | Tecnologias |
| --- | --- |
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2 |
| API | Spring Web, Bean Validation, DTOs |
| Seguranca | Spring Security, JWT, roles USER/ADMIN |
| Persistencia | Spring Data JPA, Hibernate, MySQL 8 |
| Testes | JUnit 5, Mockito, H2 |
| DevOps | Docker, Docker Compose, GitHub Actions |
| Documentacao | Swagger/OpenAPI |

## Funcionalidades

- Registro e login de usuarios com retorno de token JWT.
- Controle de acesso por perfil `USER` e `ADMIN`.
- Listagem publica de produtos.
- CRUD de produtos protegido para administradores.
- Criacao e consulta de pedidos por usuario autenticado.
- Consulta geral e atualizacao de status por administrador.
- Controle de estoque ao criar e cancelar pedidos.
- Tratamento global de erros com respostas padronizadas.
- Validacao de entrada com Bean Validation.

## Arquitetura

```text
src/main/java/com/portfolio/ordersystem
|-- config        # Security, CORS, data seed, OpenAPI
|-- controller    # AuthController, ProductController, OrderController
|-- dto           # Requests and responses
|-- entity        # User, Product, Order, OrderItem
|-- exception     # GlobalExceptionHandler and custom exceptions
|-- repository    # Spring Data JPA repositories
|-- security      # JWT utilities and authentication filter
`-- service       # Business rules
```

## Como rodar com Docker

```bash
git clone https://github.com/carthurrodrigues/ordem-system-api.git
cd ordem-system-api
cp .env.example .env
docker compose up --build
```

A API ficara disponivel em:

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Como rodar localmente

Pre-requisitos:

- Java 17
- Maven
- MySQL 8

```bash
cp .env.example .env
mvn spring-boot:run
```

## Testes

```bash
mvn test
```

O workflow `.github/workflows/ci.yml` executa os testes automaticamente a cada push ou pull request para a branch `main`.

## Autenticacao

### Registrar usuario

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Carlos Demo",
  "email": "demo@example.com",
  "password": "demo12345"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "demo@example.com",
  "password": "demo12345"
}
```

Resposta esperada:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "demo@example.com",
  "name": "Carlos Demo",
  "role": "USER"
}
```

Use o token nas rotas protegidas:

```http
Authorization: Bearer <token>
```

Usuario administrador criado no seed:

```text
Email: admin@ordersystem.com
Senha: admin123
```

## Endpoints principais

| Metodo | Endpoint | Acesso | Descricao |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | Publico | Registrar usuario |
| POST | `/api/auth/login` | Publico | Autenticar usuario |
| GET | `/api/products` | Publico | Listar produtos |
| GET | `/api/products/{id}` | Publico | Detalhar produto |
| POST | `/api/products` | ADMIN | Criar produto |
| PUT | `/api/products/{id}` | ADMIN | Atualizar produto |
| DELETE | `/api/products/{id}` | ADMIN | Remover produto |
| POST | `/api/orders` | USER | Criar pedido |
| GET | `/api/orders/my-orders` | USER | Listar meus pedidos |
| GET | `/api/orders/{id}` | USER | Detalhar pedido |
| GET | `/api/orders` | ADMIN | Listar todos os pedidos |
| PATCH | `/api/orders/{id}/status` | ADMIN | Atualizar status |

Arquivo com exemplos de requisicoes: `docs/api.http`.

## Fluxo de status do pedido

```text
PENDING -> CONFIRMED -> PREPARING -> SHIPPED -> DELIVERED
```

Pedidos podem ir para `CANCELLED`, exceto quando ja estao `DELIVERED` ou `CANCELLED`.

## Decisoes tecnicas

- JWT evita sessao em servidor e facilita escalabilidade.
- DTOs separam contratos de API das entidades JPA.
- `@RestControllerAdvice` centraliza tratamento de erros.
- `@Transactional` protege operacoes de escrita.
- Docker Compose reduz friccao para avaliacao tecnica.
- Swagger acelera a validacao manual por recrutadores e tech leads.

## Autor

Carlos Rodrigues

- LinkedIn: https://www.linkedin.com/in/carlos-rodrigues-323161377/
- GitHub: https://github.com/carthurrodrigues
