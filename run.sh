#!/bin/bash
set -e
set -m

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

for cmd in docker java node npm; do
  command -v "$cmd" >/dev/null 2>&1 || { echo "$cmd não encontrado no PATH"; exit 1; }
done

port_free() {
  ! lsof -iTCP:"$1" -sTCP:LISTEN -P >/dev/null 2>&1
}

pick_port() {
  local port=$1
  while ! port_free "$port"; do
    port=$((port + 1))
  done
  echo "$port"
}

BACKEND_PORT=$(pick_port 8080)
FRONTEND_PORT=$(pick_port 3000)

BACKEND_PID=""
FRONTEND_PID=""

cleanup() {
  echo ""
  echo "Encerrando..."
  [ -n "$BACKEND_PID" ] && kill -- "-$BACKEND_PID" 2>/dev/null
  [ -n "$FRONTEND_PID" ] && kill -- "-$FRONTEND_PID" 2>/dev/null
  sleep 1
  local pids
  pids=$(lsof -t -iTCP:"$BACKEND_PORT" -sTCP:LISTEN 2>/dev/null)
  [ -n "$pids" ] && kill -9 $pids 2>/dev/null
  pids=$(lsof -t -iTCP:"$FRONTEND_PORT" -sTCP:LISTEN 2>/dev/null)
  [ -n "$pids" ] && kill -9 $pids 2>/dev/null
  wait "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null
}
trap cleanup EXIT INT TERM

echo "Subindo MySQL..."
(cd "$ROOT/database" && docker compose up -d)

echo "Aguardando MySQL responder..."
until docker exec fullstack-challenge-mysql mysqladmin ping -uclinic -pclinic --silent >/dev/null 2>&1; do
  sleep 1
done

echo "Subindo backend na porta $BACKEND_PORT..."
(cd "$ROOT/backend" && ./mvnw -q spring-boot:run -Dspring-boot.run.arguments="--server.port=$BACKEND_PORT") &
BACKEND_PID=$!

until curl -s -o /dev/null "http://localhost:$BACKEND_PORT"; do
  sleep 1
  if ! kill -0 "$BACKEND_PID" 2>/dev/null; then
    echo "O backend não subiu, veja o erro acima"
    exit 1
  fi
done
echo "Backend pronto em http://localhost:$BACKEND_PORT"

if [ ! -d "$ROOT/frontend/node_modules" ]; then
  echo "Instalando dependências do frontend..."
  (cd "$ROOT/frontend" && npm install)
fi

echo "Subindo frontend na porta $FRONTEND_PORT..."
(cd "$ROOT/frontend" && NEXT_PUBLIC_API_URL="http://localhost:$BACKEND_PORT" npm run dev -- -p "$FRONTEND_PORT") &
FRONTEND_PID=$!

echo ""
echo "Frontend: http://localhost:$FRONTEND_PORT"
echo "Backend:  http://localhost:$BACKEND_PORT"
echo "Ctrl+C para parar (o MySQL continua rodando)"
echo ""

wait
