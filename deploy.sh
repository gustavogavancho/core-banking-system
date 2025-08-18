#!/bin/bash
# Script para desplegar el sistema bancario en contenedores

echo "=== Desplegando Core Banking System ==="
echo "Servicios: account, client, mysql"
echo ""

# Verificar que Docker esté disponible
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado o no está en el PATH"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose no está instalado o no está en el PATH"
    exit 1
fi

echo "✅ Docker y Docker Compose están disponibles"

# Construir y levantar los servicios
echo ""
echo "🔨 Construyendo imágenes y desplegando servicios..."
docker-compose up --build -d

echo ""
echo "📊 Estado de los contenedores:"
docker-compose ps

echo ""
echo "📋 Servicios disponibles:"
echo "  - MySQL Database: localhost:3306"
echo "  - Client Service: http://localhost:8081"
echo "  - Account Service: http://localhost:8080"
echo ""
echo "📖 Para ver logs:"
echo "  docker-compose logs -f [service_name]"
echo ""
echo "🛑 Para detener los servicios:"
echo "  docker-compose down"
echo ""
echo "🔄 Para reiniciar los servicios:"
echo "  docker-compose restart"
