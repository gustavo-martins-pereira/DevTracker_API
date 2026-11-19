# DevTracker API

**Gerencie clientes, projetos de desenvolvimento e finanças por meio de uma API REST em Java.**

[![Read this README in English](https://img.shields.io/badge/README-Read_in_English-0969DA?style=for-the-badge)](readme.en.md)

![Java 25](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square)
![Spring Boot 4.1.1](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square)
![Banco H2](https://img.shields.io/badge/Banco-H2-09476B?style=flat-square)
![Docker Compose](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square)

A DevTracker API relaciona clientes a projetos de desenvolvimento e registra suas receitas e despesas. Os projetos podem ter preço fixo ou cobrança por hora, e as transações financeiras podem ser consultadas em reais (BRL) ou convertidas para dólares (USD) com uma cotação obtida de um provedor externo.

Desenvolvido como projeto de aprendizado em backend, reúne endpoints REST, regras de negócio, persistência, testes automatizados e execução em contêineres.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Autenticação e segurança](#autenticação-e-segurança)
- [Arquitetura](#arquitetura)
- [Como executar](#como-executar)
- [Execução com Docker](#execução-com-docker)
- [Endpoints da API](#endpoints-da-api)
- [Coleção do Postman](#coleção-do-postman)
- [Exemplos de requisições](#exemplos-de-requisições)
- [Validações e erros](#validações-e-erros)
- [Testes](#testes)
- [Configuração e dados](#configuração-e-dados)
- [Autor](#autor)

## Funcionalidades

- Cadastrar usuários e efetuar login com emissão de JWT.
- Proteger as operações com autenticação stateless e senhas armazenadas como hash BCrypt.
- Criar, consultar, atualizar e desativar clientes.
- Criar projetos com preço fixo e orçamento ou cobrança por hora e valor da hora.
- Acompanhar horas trabalhadas, datas e status dos projetos.
- Filtrar projetos por status: `IN_PROGRESS`, `COMPLETED` ou `CANCELLED`.
- Registrar receitas e despesas vinculadas aos projetos.
- Consultar transações financeiras por ID ou projeto.
- Retornar valores em BRL ou convertê-los para USD.
- Validar campos das requisições e retornar erros estruturados.
- Executar a API com Docker Compose e armazenamento persistente em H2.

> A exclusão de clientes é lógica (soft delete): o cliente é marcado como inativo, sem remover seu registro do banco de dados.

## Tecnologias

| Tecnologia | Finalidade |
| --- | --- |
| Java 25 | Linguagem e ambiente de execução |
| Spring Boot 4.1.1 / Spring MVC | API REST e configuração da aplicação |
| Spring Security / BCrypt | Autenticação, controle de acesso e hash de senhas |
| Auth0 Java JWT 4.6.2 | Emissão e verificação de tokens JWT |
| Spring Data JPA / Hibernate | Persistência e mapeamento de entidades |
| H2 | Banco de dados embarcado |
| Jakarta Bean Validation | Validação das requisições |
| MapStruct | Mapeamento entre entidades e DTOs |
| Lombok | Geração de construtores e métodos de acesso |
| JUnit Jupiter, Mockito e MockMvc | Testes automatizados de serviços e controllers |
| Maven Wrapper | Build e gerenciamento de dependências |
| Docker / Docker Compose | Criação da imagem, configuração e armazenamento persistente |

## Autenticação e segurança

Cadastre um usuário em `POST /auth/register` e use as mesmas credenciais em `POST /auth/login`. Ambos recebem `username` e `password` obrigatórios. O cadastro retorna `204 No Content`, rejeita nomes de usuário já utilizados com `409` e armazena a senha como hash BCrypt.

O login retorna `200 OK` com um JSON no formato `{"token":"<jwt>"}`. O JWT é assinado com HS256, identifica o emissor como `DevTracker API`, contém o username em `sub` e expira em duas horas. Efetue login novamente para obter outro token após a expiração.

A configuração libera requisições `POST /auth/**`. As demais rotas exigem autenticação, incluindo clientes, projetos, transações, câmbio e o console H2. Envie o token nas requisições protegidas pelo cabeçalho `Authorization`.

```http
Authorization: Bearer <jwt>
```

O `SecurityFilter` verifica o JWT, consulta o usuário e registra suas autoridades no contexto do Spring Security antes da autorização. Todos os usuários recebem `ROLE_USER`. As consultas atuais compartilham clientes, projetos e transações entre os usuários autenticados, sem filtro por proprietário.

### Chave de assinatura

Na execução local, defina `JWT_SECRET` no ambiente antes de iniciar a API, inclusive ao executar o JAR.

```powershell
$env:JWT_SECRET = "substitua-por-uma-chave-aleatoria-forte"
.\mvnw.cmd spring-boot:run
```

O `application.properties` utiliza `api.security.jwt.secret=${JWT_SECRET:12345}`. O valor `12345` é o fallback de desenvolvimento. A variável precisa estar disponível ao processo Java. Um arquivo `.env` sozinho não é carregado pelo comando Maven nem pelo JAR.

## Arquitetura

A aplicação separa o tratamento das requisições HTTP, as operações de negócio, a persistência e o mapeamento dos DTOs.

```text
Requisição HTTP
    |
    v
SecurityFilter (JWT) -> Spring Security -> Controller -> Service -> Repository -> H2
                 |
                 +-> Mapper -> DTO de resposta
                 |
                 +-> Integração de câmbio, quando solicitada a conversão para USD
```

```text
src/
├── main/
│   ├── java/com/gustavomp/devtrackerapi/
│   │   ├── annotations/    # Anotações de validação personalizadas
│   │   ├── configs/       # Configuração de segurança e integrações
│   │   ├── filters/       # Filtro de autenticação JWT
│   │   ├── controllers/   # Endpoints HTTP
│   │   ├── dtos/          # Records de requisição e resposta
│   │   ├── exceptions/    # Exceções e tratamento de erros HTTP
│   │   ├── integrations/  # Cliente da API externa
│   │   ├── mappers/       # Mapeamentos com MapStruct
│   │   ├── models/        # Entidades e enums
│   │   ├── repositories/  # Repositórios Spring Data
│   │   ├── services/      # Operações de negócio
│   │   └── validators/    # Regras de validação personalizadas
│   └── resources/
│       └── application.properties
└── test/java/com/gustavomp/devtrackerapi/
    ├── controllers/
    ├── services/
    └── suites/
```

## Como executar

### Pré-requisitos

- JDK 25.
- Git.
- Docker Desktop com contêineres Linux e Docker Compose, caso utilize a execução em contêineres.

O Maven é disponibilizado pelo Wrapper incluído no repositório.

### Clonar e compilar

Os comandos abaixo utilizam o Windows PowerShell.

```powershell
git clone https://github.com/gustavo-martins-pereira/DevTracker_API.git
cd DevTracker_API
.\mvnw.cmd clean verify
```

No Linux ou macOS, utilize `./mvnw` no lugar de `.\mvnw.cmd`. Se necessário, execute primeiro `chmod +x mvnw`.

### Executar localmente

```powershell
.\mvnw.cmd spring-boot:run
```

A URL base da API local é `http://localhost:8080`. Para consultar `/clients`, envie uma requisição com o token obtido no login.

Também é possível executar o JAR gerado:

```powershell
java -jar target/devtrackerapi-0.0.1-SNAPSHOT.jar
```

> A execução local utiliza H2 em memória e `create-drop`. Os dados são temporários e não permanecem após o encerramento da aplicação.

## Execução com Docker

### 1. Configurar a senha do banco

```powershell
Copy-Item .env.example .env
```

Edite o arquivo `.env` e defina uma senha para o ambiente de desenvolvimento local:

```dotenv
DB_PASSWORD=substitua-pela-sua-senha-local
JWT_SECRET=substitua-por-uma-chave-aleatoria-forte
```

O `.env.example` também contém `JWT_SECRET`, mas o `compose.yaml` atual repassa apenas as variáveis declaradas em `environment`. Para fornecer sua chave ao contêiner, acrescente a linha abaixo nesse bloco antes de iniciar o Compose e defina `JWT_SECRET` no `.env`.

```yaml
JWT_SECRET: "${JWT_SECRET:?Set JWT_SECRET in .env}"
```

Sem esse repasse, o contêiner utiliza o fallback `12345` de `application.properties`.


O arquivo `.env` está excluído do Git. Não inclua credenciais nos commits.

### 2. Gerar o JAR e iniciar a aplicação

```powershell
.\mvnw.cmd clean verify
docker compose up -d --build
```

O Dockerfile copia o JAR de `target/`; ele não compila o código Java. Após alterar o código, gere novamente o JAR antes de reconstruir a imagem.

A URL base no Docker é `http://localhost:8081`. Consulte `/clients` enviando o token obtido no login. O Compose mapeia a porta `8081` do computador para a porta `8080` do contêiner e limita o acesso ao localhost.

### 3. Consultar o status e os logs

```powershell
docker compose ps
docker compose logs -f api
```

### 4. Parar ou reiniciar

```powershell
docker compose down
docker compose up -d
```

O volume nomeado preserva os arquivos do banco H2 mesmo quando o contêiner é substituído.

> **Exclusão dos dados:** `docker compose down -v` também remove o volume do banco. Utilize esse comando somente quando quiser descartar os dados armazenados.

## Endpoints da API

Utilize `http://localhost:8080` na execução local ou `http://localhost:8081` com Docker Compose.

### Autenticação

| Método | Endpoint | Acesso | Resposta de sucesso |
| --- | --- | --- | --- |
| POST | `/auth/register` | Público | `204`, sem corpo |
| POST | `/auth/login` | Público | `200`, com `token` |

Todos os endpoints de negócio abaixo exigem o cabeçalho Bearer.

### Clientes

| Método | Endpoint | Descrição |
| --- | --- | --- |
| POST | `/clients` | Criar um cliente |
| GET | `/clients` | Listar clientes ativos |
| GET | `/clients?name=Ana` | Buscar um cliente ativo pelo nome |
| PUT | `/clients?id=1` | Atualizar um cliente |
| DELETE | `/clients?id=1` | Desativar um cliente |

### Projetos

| Método | Endpoint | Descrição |
| --- | --- | --- |
| POST | `/projects` | Criar um projeto |
| GET | `/projects` | Listar projetos |
| GET | `/projects?projectStatus=COMPLETED` | Filtrar projetos por status |
| GET | `/projects/1` | Buscar um projeto por ID |
| PUT | `/projects?id=1` | Atualizar os dados de um projeto |
| PATCH | `/projects?id=1` | Atualizar o status de um projeto |

### Transações financeiras

| Método | Endpoint | Descrição |
| --- | --- | --- |
| POST | `/financial-transactions` | Criar uma transação |
| GET | `/financial-transactions?projectId=1` | Listar transações de um projeto |
| GET | `/financial-transactions?projectId=1&currency=USD` | Listar transações com valores em USD |
| GET | `/financial-transactions/1` | Buscar uma transação por ID |
| GET | `/financial-transactions/1?currency=USD` | Buscar uma transação com o valor em USD |
| DELETE | `/financial-transactions/1` | Excluir uma transação |

BRL é a moeda padrão das respostas. A conversão para USD altera o valor retornado, sem modificar o valor armazenado da transação.

### Cotação de câmbio

| Método | Endpoint | Descrição |
| --- | --- | --- |
| GET | `/awesomeapi/currency/usd-brl` | Consultar a cotação USD/BRL |

A conversão de moeda e o endpoint de cotação dependem de acesso ao provedor externo.

## Coleção do Postman

Importe a [coleção do DevTracker no Postman](postman/DevTracker.postman_collection.json) para explorar a API sem escrever manualmente as requisições HTTP.

A coleção contém 21 requisições organizadas em cinco pastas:

| Pasta | Requisições |
| --- | --- |
| User | Cadastrar usuário e efetuar login |
| Client | Criar, listar, buscar por nome, atualizar e desativar |
| Project | Criar, listar, filtrar por status, buscar por ID, atualizar dados e status |
| Financial Transaction | Criar, listar por projeto, buscar por ID, consultar valores em USD e excluir |
| Awesome API | Consultar a cotação; inclui uma requisição de conversão ainda não implementada |

Na pasta **User**, execute primeiro **Register** (`POST /auth/register`) e depois **Login** (`POST /auth/login`). Ambas incluem um corpo JSON com `username` e `password`. Os valores `dev` são exemplos de preenchimento. Copie o valor de `token` da resposta do login e configure **Authorization → Bearer Token** nas requisições protegidas. A coleção exige configurar o Bearer manualmente, pois não inclui autenticação herdada nem script para armazenar o token automaticamente.

### Importar e configurar

1. Abra o Postman, selecione **Import** e escolha `postman/DevTracker.postman_collection.json`.
2. Abra a coleção importada e acesse a aba **Variables**.
3. Defina `CARREIRA_BACKEND_JAVA_CHECKPOINT_1_DEVTRACKER` com a URL base:

   | Modo de execução | Valor da variável |
   | --- | --- |
   | Aplicação Spring Boot local | `http://localhost:8080` |
   | Docker Compose | `http://localhost:8081` |

4. Salve as configurações da coleção e abra uma requisição.
5. Confira o corpo JSON, os parâmetros e os IDs antes de selecionar **Send**.

A variável exportada está vazia. As requisições utilizam essa variável desta forma:

```text
{{CARREIRA_BACKEND_JAVA_CHECKPOINT_1_DEVTRACKER}}/clients
```

Após cadastrar o usuário, efetuar login e configurar o Bearer Token, crie um cliente, depois um projeto vinculado a ele e, por fim, as transações do projeto. Substitua os IDs de exemplo pelos IDs retornados pela API. Na busca de cliente por nome, substitua o valor de exemplo `name` pelo nome de um cliente existente.

> A coleção inclui `GET /awesomeapi/currency/convert?amount=100`, ainda não implementado no controller atual. Para utilizar a conversão disponível, consulte os endpoints de transações com `currency=USD`.

## Exemplos de requisições

Os IDs abaixo são exemplos. Utilize os IDs retornados ao criar seus próprios recursos.

### Cadastro e login

Os dois endpoints recebem o mesmo formato de entrada. Execute primeiro `POST /auth/register` e depois `POST /auth/login`.

```json
{
  "username": "dev",
  "password": "example-password"
}
```

O login devolve:

```json
{
  "token": "<jwt>"
}
```

Exemplo do fluxo em PowerShell, usando a URL do Docker:

```powershell
$baseUrl = "http://localhost:8081"
$credentials = @{ username = "dev"; password = "example-password" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "$baseUrl/auth/register" -ContentType "application/json" -Body $credentials
$login = Invoke-RestMethod -Method Post -Uri "$baseUrl/auth/login" -ContentType "application/json" -Body $credentials
$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Method Get -Uri "$baseUrl/clients" -Headers $headers
```

### Criar um cliente

`POST /clients`

```json
{
  "name": "Ana",
  "email": "ana@example.com",
  "phone": "12345678",
  "active": true
}
```

Exemplo com PowerShell e a URL do Docker:

```powershell
$body = @{
    name = "Ana"
    email = "ana@example.com"
    phone = "12345678"
    active = $true
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8081/clients" -ContentType "application/json" -Headers $headers -Body $body
```

### Criar um projeto com preço fixo

`POST /projects`

```json
{
  "name": "Portfolio Website",
  "description": "Design and development of a portfolio website",
  "workedHours": 0,
  "startDate": "2026-01-01",
  "dueDate": "2026-01-30",
  "projectStatus": "IN_PROGRESS",
  "clientId": 1,
  "projectType": "FIXED_PRICE",
  "budget": 2500
}
```

Para um projeto com cobrança por hora, utilize `"projectType": "HOURLY_RATE"` e substitua `budget` por um `hourlyRate` positivo.

### Registrar uma receita do projeto

`POST /financial-transactions`

```json
{
  "description": "Initial project payment",
  "amount": 500,
  "date": "2026-01-01",
  "transactionType": "INCOME",
  "projectId": 1
}
```

Utilize `EXPENSE` para registrar uma despesa. O valor deve permanecer positivo; o tipo da transação indica se é receita ou despesa.

### Atualizar o status de um projeto

`PATCH /projects?id=1`

```json
{
  "projectStatus": "COMPLETED"
}
```

## Validações e erros

As requisições são validadas antes da execução dos serviços. As regras incluem campos obrigatórios, formato de e-mail, telefone do cliente com oito dígitos, valores monetários positivos e valores válidos dos enums.

A criação de projetos também valida o tipo de cobrança: projetos com preço fixo exigem orçamento positivo, enquanto projetos por hora exigem valor da hora positivo.

| Status | Significado |
| --- | --- |
| 200 | Consulta ou atualização concluída |
| 201 | Recurso criado |
| 204 | Cadastro de usuário, exclusão ou desativação concluída, sem corpo na resposta |
| 400 | Requisição inválida, falha de conversão ou argumento inválido |
| 401 | Credenciais inválidas no login ou ausência de autenticação nas rotas protegidas |
| 404 | Recurso não encontrado |
| 409 | Conflito com um recurso existente |
| 502 | Falha no provedor externo de câmbio |
| 500 | Erro inesperado no servidor |

Os erros tratados por `GlobalExceptionHandler` contêm `timestamp`, `status` e `error`. O campo `error` pode conter uma mensagem ou um mapa de erros por campo.

Exemplo ilustrativo de resposta de validação:

```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 400,
  "error": {
    "email": "Invalid email format"
  }
}
```

As chaves e os valores dos enums nos exemplos mantêm o contrato original da API. As mensagens de erro podem estar em inglês.

Falhas lançadas no filtro JWT seguem o processamento de erros da cadeia Servlet. O handler dos controllers não garante o mesmo JSON ou status para tokens inválidos ou expirados.

## Testes

O projeto utiliza JUnit Jupiter e Mockito nos testes de serviços, além de MockMvc nos testes de requisições e respostas dos controllers.

Execute o ciclo de verificação do Maven:

```powershell
.\mvnw.cmd clean verify
```

Para executar explicitamente as suítes de controllers e serviços:

```powershell
.\mvnw.cmd "-Dtest=AllTestsSuite" test
```

No IntelliJ, execute `AllTestsSuite` para visualizar os testes organizados por controller e entidade dos serviços:

```text
All Tests
├── Controllers
│   ├── Client Controller
│   ├── Project Controller
│   ├── Financial Transaction Controller
│   └── Authentication Controller
└── Services
    ├── Client
    ├── Financial Transaction
    ├── Project
    └── User
```

Os testes de autenticação cobrem cadastro, login, validação dos DTOs, consulta de usuário e conflitos de username. Os testes de controllers usam MockMvc com `standaloneSetup`, sem exercitar a cadeia completa de segurança. Confira também o acesso sem token e com token válido executando a aplicação.

As suítes organizam os testes existentes. Em cada execução, escolha a suíte agregadora ou a descoberta convencional de testes para evitar executar os mesmos casos duas vezes.

## Configuração e dados

| Configuração | Padrão local | Docker Compose |
| --- | --- | --- |
| Porta no computador | 8080 | 8081 |
| Porta no contêiner | Não se aplica | 8080 |
| Armazenamento H2 | Em memória | Arquivo em `/data/devtracker` |
| Gerenciamento do schema pelo Hibernate | `create-drop` | `update` |
| Senha do banco | Vazia | Definida por `DB_PASSWORD` |
| Chave JWT | `JWT_SECRET`, com fallback `12345` | Fallback `12345` até configurar o repasse da variável |
| Console web do H2 | Habilitado em `/h2-console`, sujeito à segurança | Desabilitado |

O Compose monta o volume `devtracker-data` em `/data`. O nome real do volume no Docker normalmente inclui o prefixo do projeto Compose.

Esta configuração é voltada ao aprendizado. Antes de disponibilizar a API publicamente, revise o controle de acesso, os avisos de dependências, as migrações do banco e as configurações de implantação.

## Autor

Desenvolvido por [Gustavo Martins Pereira](https://github.com/gustavo-martins-pereira).
