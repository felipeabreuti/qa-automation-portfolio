# tests-app

Testes mobile com Appium e pytest no [Swag Labs Mobile Sample App](https://github.com/saucelabs/sample-app-mobile) (Android), rodando no [BrowserStack App Automate](https://www.browserstack.com/app-automate).

## Rodando

Precisa de Python 3.12 e de uma conta no BrowserStack com o APK enviado (App Management → Upload). O APK vem das [releases do sample-app-mobile](https://github.com/saucelabs/sample-app-mobile/releases) e não fica no repositório.

Copie `.env.example` para `.env` e preencha:

| Variável | O que é |
|---|---|
| `BROWSERSTACK_USERNAME` | usuário da conta |
| `BROWSERSTACK_ACCESS_KEY` | access key da conta |
| `BROWSERSTACK_APP_URL` | o `bs://...` devolvido pelo upload |

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
pytest
```

Se faltar alguma variável, a suíte para antes de abrir sessão no BrowserStack.

## Testes

| Teste | O que verifica |
|---|---|
| `test_login_valid_user_reaches_catalog` | `standard_user` chega na tela `PRODUCTS` |
| `test_login_locked_out_user_shows_error` | `locked_out_user` vê a mensagem exata de bloqueio e continua no login |
| `test_checkout_form_submission_completes_order` | badge do carrinho, item no carrinho, formulário, resumo e tela final com `THANK YOU FOR YOU ORDER` |

O "YOU ORDER" é o texto do próprio app.

## Resultado

Última execução (09/10/2026): 3 testes, 3 passaram, no Google Pixel 8 com Android 14.

- [report.html](evidence/report.html), com o log das etapas, screenshot da tela final e o link da sessão no BrowserStack (vídeo e logs do device)

## Observações

- No fim de cada teste o conftest manda `passed` ou `failed` para o BrowserStack. Sem isso toda sessão aparece como "Unmarked" no dashboard.
- O log de cada etapa também vai como `annotate` para a timeline da sessão, então as etapas aparecem no relatório e no vídeo.
- Um driver por teste, em série. O plano free do BrowserStack só libera uma sessão por vez.
- As screens só devolvem estado e o `assert` fica no teste.
- Os locators usam accessibility id (`test-<texto>`). O FINISH fica no fim de uma lista, então uso `UiScrollable` para chegar nele.
- No CI o job roda em PR do próprio repositório ou manualmente. PR de fork não recebe os secrets.
