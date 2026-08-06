import { defineConfig } from '@playwright/test';

// Test di sola verifica dati (Excel <-> DB): NESSUN browser. Il runner Playwright serve solo
// per la convenzione .spec.ts, il report e le asserzioni. Timeout ampio perche' legge Excel + DB.
export default defineConfig({
  testDir: '.',
  testMatch: /.*\.spec\.ts/,
  timeout: 180_000,
  fullyParallel: false,
  reporter: [['list']],
});
