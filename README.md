# QA Automation Portfolio

Automação de testes de API, web, mobile e carga.

| Suíte | Ferramenta | Alvo | Última execução | Evidência |
|---|---|---|---|---|
| [tests-api](tests-api/README.md) | Java + REST Assured + Allure | ServeRest | 16/11/2026 · 13 passaram | [evidence](tests-api/evidence/) |
| [tests-e2e](tests-e2e/README.md) | Playwright + TypeScript | SauceDemo | 16/11/2026 · 8 passaram | [evidence](tests-e2e/evidence/) |
| [tests-app](tests-app/README.md) | Appium + Python (BrowserStack) | Swag Labs Android | 16/11/2026 · 3 passaram | [evidence](tests-app/evidence/) |
| [tests-load](tests-load/README.md) | k6 | QuickPizza | 16/11/2026 · smoke passou; carga falhou thresholds | [evidence](tests-load/evidence/) |

O GitHub Actions executa API, E2E e smoke de carga em push e pull request. A suíte mobile roda manualmente ou em PR do repositório, e a carga completa é manual ([workflow](.github/workflows/tests.yml)).
