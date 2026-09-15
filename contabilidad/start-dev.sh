#!/bin/bash

# Script para iniciar el backend en modo desarrollo con optimizaciones
# Uso: ./start-dev.sh

cd "$(dirname "$0")"

echo "Iniciando Sistema Finca - Backend (Contabilidad)"
echo "================================================"

# Configuración JVM optimizada para desarrollo
JVM_OPTS="-Xms256m -Xmx512m"
JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=200"
JVM_OPTS="$JVM_OPTS -XX:+TieredCompilation"
JVM_OPTS="$JVM_OPTS -XX:TieredStopAtLevel=1"

echo "JVM Options: $JVM_OPTS"
echo "Puerto: 9908"
echo "Perfil: dev"
echo ""

./mvnw spring-boot:run -DskipTests -Dspring-boot.run.jvmArguments="$JVM_OPTS"
