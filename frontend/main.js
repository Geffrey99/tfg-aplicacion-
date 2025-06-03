const { app, BrowserWindow } = require('electron');
const path = require('path');


let mainWindow;

app.on('ready', () => {
  mainWindow = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true,
    },
  });

  // Cargar la aplicación Angular desde dist/web-geffrey
  const startUrl = path.resolve(__dirname, 'dist/web-geffrey/browser/index.html');

  mainWindow.loadFile(startUrl).catch(err => {
    console.error("Error al cargar el archivo:", err);
  });

  // Abrir herramientas de desarrollo (opcional, para depurar)
  mainWindow.webContents.openDevTools();
});