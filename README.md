# SEO & SEM Analyzer Web Application

Applicazione web professionale per l'analisi e la diagnostica di siti web sotto l'aspetto **SEO** e **SEM**, con monitoraggio grafico dei log in tempo reale, report diagnostico compatto per la stampa e archiviazione permanente su database locale.

---

## 🚀 Istruzioni per il Collega (Come Avviare in 1 Clic)

### Requisito Unico:
- **Java 17 o 21** installato sul computer.  
  *(Se non è installato, basta scaricarlo da [Adoptium Temurin](https://adoptium.net) o via terminale con: `winget install Microsoft.OpenJDK.21`).*
- Non è necessario installare Maven: il pacchetto è già compilato e include il Maven Wrapper autonomo (`mvnw.cmd`).

### Avvio su Windows:
Fai doppio clic sul file:
👉 **`avvia.bat`**

### Avvio su Mac / Linux:
Esegui da terminale:
```bash
chmod +x avvia.sh
./avvia.sh
```

### Accesso:
Una volta avviato, apri il browser su:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 📦 Struttura della Cartella

- **`avvia.bat`** / **`avvia.sh`**: Script di avvio automatico rapido (1 clic).
- **`target/seo-sem-analyzer-1.0.0.jar`**: Pacchetto eseguibile autonomo Spring Boot con server Tomcat integrato.
- **`data/`**: Cartella del database locale H2 (`analyzer_db.mv.db`), dove vengono salvati permanentemente tutti i report.
- **`src/`**: Codice sorgente Java e template Thymeleaf.
- **`pom.xml`**, **`mvnw`**, **`mvnw.cmd`**: File di build Maven portabile.
