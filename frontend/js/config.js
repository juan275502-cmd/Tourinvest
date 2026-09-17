// ============================================================
// TourInvest · Configuración del frontend (sin bundler)
// ============================================================
// Este archivo sustituye las variables de entorno de Vite
// (import.meta.env.VITE_*) para que el proyecto funcione como
// scripts clásicos servidos por cualquier servidor estático
// (setup.ps1 run-frontend en :8081, Live Server en :5500, etc.).
//
//  apiMode: "api"   → consume el backend Spring Boot (apiUrl)
//           "local" → modo demo con js/localData.js (sin backend)
window.TourInvestConfig = {
  apiMode: "api",
  apiUrl: "http://localhost:8080",
};
