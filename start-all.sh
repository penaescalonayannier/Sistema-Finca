#!/bin/bash

# Script para iniciar todo el Sistema Finca (Backend + Frontend)
# Uso: ./start-all.sh

cd "$(dirname "$0")"

echo "=============================================="
echo "       SISTEMA FINCA - Inicio Completo       "
echo "=============================================="
echo ""

# Iniciar Backend en background
echo "[1/2] Iniciando Backend (puerto 9908)..."
cd contabilidad
./start-dev.sh &
BACKEND_PID=$!
cd ..

# Esperar a que el backend inicie
echo "Esperando que el backend inicie..."
sleep 10

# Iniciar Frontend en background
echo "[2/2] Iniciando Frontend (puerto 8080)..."
cd mi-proyecto-vue/mi-proyecto
./start-dev.sh &
FRONTEND_PID=$!
cd ../..

echo ""
echo "=============================================="
echo "Servicios iniciados:"
echo "  - Backend:  http://localhost:9908 (PID: $BACKEND_PID)"
echo "  - Frontend: http://localhost:8080 (PID: $FRONTEND_PID)"
echo "=============================================="
echo ""
echo "Presiona Ctrl+C para detener ambos servicios"

# Esperar y manejar Ctrl+C
trap "echo ''; echo 'Deteniendo servicios...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit 0" SIGINT SIGTERM

wait
