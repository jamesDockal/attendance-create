# Desafio Full Stack - Atendimentos

Aplicação para abertura de atendimentos de uma clínica. A recepção informa nome e CPF, o backend registra o atendimento e busca o protocolo no HTTPBin em segundo plano. O frontend acompanha o status e mostra o protocolo assim que ele fica pronto.

## Stack

- `frontend/`: Next.js, React, TypeScript
- `backend/`: Java, Spring Boot
- `database/`: MySQL via Docker Compose

## Como rodar

Pré-requisitos: Docker, Java 17+ e Node 20.9+.

```bash
./run.sh
```

Isso sobe o MySQL, o backend e o frontend, tudo de uma vez. O script mostra os endereços no final. Se a porta padrão (3000 ou 8080) já estiver em uso, ele escolhe a próxima livre. Para parar tudo, use Ctrl+C (o MySQL continua rodando).

Se preferir rodar cada parte na mão:

```bash
cd database && docker compose up -d
cd backend && ./mvnw spring-boot:run
cd frontend && npm install && npm run dev
```

## API

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | `/api/attendances` | Abre um atendimento. Body: `{ "name": "...", "cpf": "..." }` |
| GET | `/api/attendances/{id}` | Consulta um atendimento |

Status possíveis: `PENDING`, `PROCESSING`, `COMPLETED`, `FAILED`.

## Como funciona o processamento

A cada 5s o backend busca atendimentos pendentes e consulta o HTTPBin para cada um, em paralelo, sem travar os demais. Se a chamada falhar, tenta de novo até 5 vezes antes de marcar como `FAILED`. Se o backend for reiniciado no meio do processo, o atendimento volta para a fila sozinho.

## Testes

```bash
cd backend && ./mvnw test
```

Precisa do MySQL rodando.
