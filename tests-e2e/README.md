# Testes E2E — SauceDemo

Testes web do [SauceDemo](https://www.saucedemo.com) com Playwright + TypeScript, em Chromium.

## Pré-requisitos

Node.js 24, a mesma versão usada no CI. O Playwright exige Node.js 20 ou superior.

## Execução

```bash
cd tests-e2e
npm ci
npx playwright install chromium
npm test
```

Para ver o browser: `npm run test:headed`. Para abrir o relatório: `npm run test:report`.

## Última execução

15/11/2026, local: 8 testes, 8 passaram. O relatório fica em [evidence/](evidence/index.html), com um screenshot da tela final de cada teste. Quando um teste falha, o trace completo também vai para o relatório.

## O que está coberto

| Spec | Cenário | O que eu verifico |
|---|---|---|
| `login.spec.ts` | `standard_user` | URL `/inventory.html` e título `Products` |
| `login.spec.ts` | `performance_glitch_user` | chega na inventory apesar do login lento (~5s); o tempo medido vai como anotação no relatório |
| `login.spec.ts` | `locked_out_user` | mensagem exata de bloqueio e continua na tela de login |
| `login.spec.ts` | senha errada | mensagem exata e continua na tela de login |
| `checkout.spec.ts` | carrinho → formulário | URL, título e campos do formulário |
| `checkout.spec.ts` | formulário sem nome | `Error: First Name is required` e não sai da etapa 1 |
| `checkout.spec.ts` | compra completa | resumo com 1 item e `Item total: $29.99`, depois `Thank you for your order!` |
| `checkout.spec.ts` | compra completa com `performance_glitch_user` | o mesmo fluxo, com a aplicação lenta |

Os testes de checkout partem de um carrinho com 1 item (badge e carrinho conferidos no `beforeEach`).

## Estrutura

```
tests-e2e/
├── playwright.config.ts
├── evidence/                # relatório HTML da última execução
├── src/
│   ├── data/                # usuários e dados do formulário
│   ├── fixtures/            # entrega os Page Objects prontos para cada teste
│   ├── locators/            # só seletores, um arquivo por página
│   └── pages/               # LoginPage, InventoryPage, CartPage, CheckoutPage
└── tests/
    ├── login.spec.ts
    └── checkout.spec.ts
```

## Por que ficou assim

Separei seletores, páginas e testes em três pastas. Quando o SauceDemo mudar um id, eu altero um arquivo de `locators/` e nenhum teste. Os Page Objects chegam aos testes por fixture, cada um num `BrowserContext` próprio, e por isso a suíte roda com `fullyParallel` sem um teste interferir no outro.

O `performance_glitch_user` foi escolhido de propósito: o login dele demora uns 5 segundos. Esse teste existe para provar que a suíte não depende de `waitForTimeout` nem de sleep. Quem segura a espera é o auto-wait do Playwright, com o limite do `expect` (10s) configurado em um lugar só.

Nas mensagens de erro eu comparo o texto exato, não só se o elemento está visível. Um erro genérico no lugar do esperado tem que quebrar o teste.

Deixei o trace só para falhas (`retain-on-failure`). Com trace em todos os testes o relatório passava de 6 MB e mudava a cada execução, o que não faz sentido para algo versionado. Os screenshots continuam em todos os testes.
