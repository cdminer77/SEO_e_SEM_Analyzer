@echo off
echo ========================================================
echo   Avvio SEO ^& SEM Analyzer (Spring Boot + Java 21)
echo ========================================================
echo.

:: Verifica presenza di Java nel sistema
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERRORE] Java non e' stato trovato nel sistema!
    echo Per eseguire l'applicazione e' necessario avere Java 17 o 21 installato.
    echo Puoi scaricarlo da: https://adoptium.net oppure tramite:
    echo   winget install Microsoft.OpenJDK.21
    echo.
    pause
    exit /b 1
)

echo [1/2] Verifica pacchetto eseguibile...
if exist "target\seo-sem-analyzer-1.0.0.jar" (
    set JAR_PATH=target\seo-sem-analyzer-1.0.0.jar
) else if exist "seo-sem-analyzer-1.0.0.jar" (
    set JAR_PATH=seo-sem-analyzer-1.0.0.jar
) else (
    echo [INFO] File JAR non trovato. Tento la compilazione con Maven...
    if exist "mvnw.cmd" (
        call mvnw.cmd clean package -DskipTests
    ) else (
        call mvn clean package -DskipTests
    )
    set JAR_PATH=target\seo-sem-analyzer-1.0.0.jar
)

echo [2/2] Avvio del server web su porta 8080...
echo.
echo L'applicazione sara' accessibile all'indirizzo:
echo   http://localhost:8080
echo.
echo Premi CTRL+C in questa finestra per terminare il server.
echo ========================================================
echo.

java -jar "%JAR_PATH%"
pause
