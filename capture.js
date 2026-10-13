const puppeteer = require('puppeteer-core');
const fs = require('fs');
const path = require('path');

const url = process.argv[2];
const outputPath = process.argv[3];

if (!url || !outputPath) {
    console.error("Uso: node capture.js <url> <output_path>");
    process.exit(1);
}

// Cerca l'eseguibile di Chrome o Edge disponibile
function getExecutablePath() {
    const chromePath = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
    const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';
    if (fs.existsSync(chromePath)) return chromePath;
    if (fs.existsSync(edgePath)) return edgePath;
    throw new Error("Nessun browser Chromium (Chrome o Edge) trovato nel sistema.");
}

(async () => {
    let browser = null;
    try {
        const executablePath = getExecutablePath();
        browser = await puppeteer.launch({
            executablePath: executablePath,
            headless: 'new',
            args: [
                '--no-sandbox',
                '--disable-setuid-sandbox',
                '--disable-gpu',
                '--disable-dev-shm-usage',
                '--window-size=1280,960'
            ]
        });

        const page = await browser.newPage();
        await page.setViewport({ width: 1280, height: 960 });
        await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36');

        // Naviga verso l'URL attendendo il primo caricamento del DOM
        try {
            await page.goto(url, { waitUntil: 'domcontentloaded', timeout: 15000 });
        } catch (e) {
            // Continua anche se scatta un timeout minore su risorse lente
        }

        // Breve pausa per rendering visivo di animazioni / layout
        await new Promise(r => setTimeout(r, 1200));

        // Assicura che la directory esista
        const dir = path.dirname(outputPath);
        if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir, { recursive: true });
        }

        await page.screenshot({ path: outputPath, type: 'jpeg', quality: 80 });
        console.log("SCREENSHOT_SUCCESS: " + outputPath);
    } catch (err) {
        console.error("SCREENSHOT_ERROR: " + err.message);
        process.exit(1);
    } finally {
        if (browser) {
            try {
                await browser.close();
            } catch (e) {}
        }
    }
})();
