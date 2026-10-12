#!/usr/bin/env bash
echo "========================================================"
echo "  Avvio SEO & SEM Analyzer (Spring Boot + Java 21)"
echo "========================================================"
echo ""

if ! command -v java &> /dev/null; then
    echo "[ERRORE] Java non è installato o non è nel PATH!"
    echo "Installa OpenJDK 17 o 21 per eseguire l'applicazione."
    exit 1
fi

JAR_PATH="target/seo-sem-analyzer-1.0.0.jar"
if [ ! -f "$JAR_PATH" ]; then
    if [ -f "seo-sem-analyzer-1.0.0.jar" ]; then
        JAR_PATH="seo-sem-analyzer-1.0.0.jar"
    else
        echo "[INFO] Compilazione in corso con Maven wrapper..."
        ./mvnw clean package -DskipTests
    fi
fi

echo "Avvio su http://localhost:8080..."
java -jar "$JAR_PATH"
