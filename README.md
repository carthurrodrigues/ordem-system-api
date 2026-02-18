# 🛒 Order System API

API REST para gerenciamento de pedidos desenvolvida como projeto de portfólio.

## 🚀 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2 | Framework principal |
| Spring Security | 6 | Autenticação e autorização |
| Spring Data JPA | 3.2 | Persistência de dados |
| MySQL | 8.0 | Banco de dados |
| JWT (jjwt) | 0.11.5 | Autenticação stateless |
| JUnit 5 + Mockito | - | Testes unitários |
| Docker + Compose | - | Containerização |
| Lombok | - | Redução de boilerplate |

---

## 📋 Funcionalidades

- ✅ **Autenticação JWT** — Registro e login com token Bearer
- ✅ **Controle de Acesso** — Roles `USER` e `ADMIN`
- ✅ **CRUD de Produtos** — Criação, listagem, busca, atualização e remoção
- ✅ **Gerenciamento de Pedidos** — Criação, consulta e atualização de status
- ✅ **Controle de Estoque** — Decremento automático ao criar pedido; restauração ao cancelar
- ✅ **Tratamento de Erros** — Respostas padronizadas com mensagens em português
- ✅ **Validações** — Bean Validation em todas as entradas
- ✅ **Testes Unitários** — Service layer com JUnit 5 e Mockito
- ✅ **Docker** — Multi-stage build + docker-compose completo

---

## 🗂️ Estrutura do Projeto

```
src/
├── main/java/com/portfolio/ordersystem/
│   ├── config/          # SecurityConfig, DataInitializer
│   ├── controller/      # AuthController, ProductController, OrderController
│   ├── dto/             # AuthDTO, ProductDTO, OrderDTO
│   ├── entity/          # User, Product, Order, OrderItem
│   ├── exception/       # GlobalExceptionHandler, exceptions customizadas
│   ├── repository/      # UserRepository, ProductRepository, OrderRepository
│   ├── security/        # JwtUtils, JwtAuthenticationFilter
│   └── service/         # AuthService, ProductService, OrderService
└── test/java/com/portfolio/ordersystem/
    └── service/         # ProductServiceTest, OrderServiceTest
```

---

## ▶️ Como Rodar

### Com Docker (recomendado)

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/order-system.git
cd order-system

# 2. Suba os containers
docker-compose up --build

# A API estará disponível em: http://localhost:8080
```

### Localmente

Pré-requisitos: Java 17, Maven, MySQL 8

```bash
# Configure o banco de dados no application.properties
# e rode:
mvn spring-boot:run
```

### Rodando os Testes

```bash
mvn test
```

---

## 🔐 Autenticação

### 1. Registrar usuário
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123"
}
```

### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao@email.com",
  "password": "senha123"
}
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "joao@email.com",
  "name": "João Silva",
  "role": "USER"
}
```

Use o token nas requisições seguintes:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

> **Admin padrão criado automaticamente:**
> - Email: `admin@ordersystem.com`
> - Senha: `admin123`

---

## 📦 Endpoints

### Produtos

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/api/products` | Público | Listar produtos |
| GET | `/api/products?search=note` | Público | Buscar por nome |
| GET | `/api/products?availableOnly=true` | Público | Apenas em estoque |
| GET | `/api/products/{id}` | Público | Detalhes de um produto |
| POST | `/api/products` | ADMIN | Criar produto |
| PUT | `/api/products/{id}` | ADMIN | Atualizar produto |
| DELETE | `/api/products/{id}` | ADMIN | Remover produto |

### Pedidos

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/api/orders` | USER | Criar pedido |
| GET | `/api/orders/my-orders` | USER | Meus pedidos |
| GET | `/api/orders/{id}` | USER | Detalhes de um pedido |
| GET | `/api/orders` | ADMIN | Todos os pedidos |
| PATCH | `/api/orders/{id}/status` | ADMIN | Atualizar status |

### Status de Pedido (fluxo)
`PENDING` → `CONFIRMED` → `PREPARING` → `SHIPPED` → `DELIVERED`  
Qualquer status pode ir para `CANCELLED` (exceto `DELIVERED` e `CANCELLED`)

---

## 📝 Exemplos de Uso

### Criar Pedido
```http
POST /api/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "items": [
    { "productId": 1, "quantity": 1 },
    { "productId": 2, "quantity": 2 }
  ],
  "deliveryAddress": "Rua das Flores, 123, São Paulo - SP",
  "notes": "Entregar após 18h"
}
```

### Atualizar Status (Admin)
```http
PATCH /api/orders/1/status
Authorization: Bearer {admin-token}
Content-Type: application/json

{ "status": "CONFIRMED" }
```

---

## 🧪 Cobertura de Testes

- `ProductServiceTest` — 6 testes (findAll, findById, create, update, delete, findAvailable)
- `OrderServiceTest` — 5 testes (createOrder, estoque insuficiente, status inválido, findMyOrders, not found)

---

## 📐 Decisões de Design

- **Stateless**: JWT em vez de sessões, ideal para APIs escaláveis
- **Multi-stage Docker build**: imagem final leve com apenas o JRE Alpine
- **Transações**: `@Transactional` nos métodos de escrita para garantir consistência
- **DTOs**: separação entre camada de apresentação e entidades JPA
- **Tratamento de erros centralizado**: `@RestControllerAdvice` com respostas padronizadas

---

## 👤 Autor

Desenvolvido como projeto de portfólio para demonstrar conhecimento em desenvolvimento Back-end Java.
