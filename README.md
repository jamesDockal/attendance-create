# Desafio Full Stack - Atendimentos

Aplicação para abertura de atendimentos de uma clínica. A recepção informa nome e CPF, o backend registra o atendimento e um processo periódico busca o protocolo no HTTPBin. O frontend acompanha o status e mostra o protocolo assim que ele fica disponível.

## Stack

- `frontend/`: Next.js 16, React 19, TypeScript, Tailwind, react-hook-form + zod
- `backend/`: Java 17, Spring Boot 4, Spring Data JPA
- `database/`: MySQL 8.4 via Docker Compose

## Como rodar

Pré-requisitos: Docker, Java 17+ e Node 20.9+.

```bash
cd database
docker compose up -d
```

```bash
cd backend
./mvnw spring-boot:run
```

```bash
cd frontend
npm install
npm run dev
```

Acesse http://localhost:3000. A API sobe em http://localhost:8080.

O `init.sql` só roda quando o volume do MySQL é criado. Se o banco já tinha sido iniciado antes, recrie com `docker compose down -v && docker compose up -d`.

## API

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | `/api/attendances` | Abre um atendimento. Body: `{ "name": "...", "cpf": "..." }` |
| GET | `/api/attendances/{id}` | Consulta um atendimento |

Respostas de erro seguem o formato Problem Details (`detail`, `status` e `errors` por campo na validação).

- `400` dados inválidos
- `404` atendimento não encontrado
- `409` já existe atendimento em andamento para o CPF

Status possíveis: `PENDING`, `PROCESSING`, `COMPLETED`, `FAILED`.

## Processamento

- A cada 5s o `AttendanceProcessor` busca atendimentos `PENDING`.
- Cada atendimento é reservado com um `update ... where status = 'PENDING'`. Só quem consegue mudar para `PROCESSING` processa, então o mesmo atendimento não é enviado duas vezes, mesmo que uma chamada demore mais que o intervalo.
- As chamadas ao HTTPBin rodam em um pool de threads com timeout, então um atendimento lento não segura os outros.
- Se a chamada falhar, o atendimento volta para `PENDING` e soma uma tentativa. Depois de 5 tentativas vira `FAILED`. O registro nunca é perdido.
- Ao iniciar, a aplicação devolve para a fila os atendimentos que ficaram em `PROCESSING` por causa de uma parada no meio do processamento.

Configurações em `backend/src/main/resources/application.properties`:

| Propriedade | Padrão |
| --- | --- |
| `protocol.url` (env `PROTOCOL_URL`) | `https://httpbin.org/uuid` |
| `protocol.timeout` | `10s` |
| `processing.interval` | `5s` |
| `processing.threads` | `10` |
| `processing.max-attempts` | `5` |
| `cors.allowed-origins` (env `CORS_ALLOWED_ORIGINS`) | `http://localhost:[*],http://127.0.0.1:[*]` |

### Simulando falhas

```bash
PROTOCOL_URL=https://httpbin.org/status/500 ./mvnw spring-boot:run
PROTOCOL_URL=https://httpbin.org/delay/15 ./mvnw spring-boot:run
```

## Frontend

- Validação de nome e CPF (dígitos verificadores) antes do envio, com máscara no CPF.
- Cada atendimento aberto vira um card que consulta o backend a cada 2s até chegar em `COMPLETED` ou `FAILED`.
- Erros de validação do backend aparecem nos campos, e erros de conexão durante a consulta são exibidos sem interromper o acompanhamento.
- A URL da API pode ser alterada com `NEXT_PUBLIC_API_URL` (veja `frontend/.env.example`).

## Extras

- **Mais de um paciente:** é possível abrir vários atendimentos seguidos. Cada um é acompanhado de forma independente e o backend processa em paralelo.
- **Gerar novo atendimento:** quando um atendimento termina (com protocolo ou com falha), o card permite abrir um novo para o mesmo paciente. O backend não deixa abrir outro enquanto houver um pendente para o mesmo CPF.

## Testes

```bash
cd backend
./mvnw test
```

O `contextLoads` precisa do MySQL rodando. Os demais testes são unitários e de controller.
