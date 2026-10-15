# Testes de API — ServeRest

Testes da API pública [serverest.dev](https://serverest.dev) em Java 17 com REST Assured, JUnit 6 e Allure.

## Como rodar

O CI usa Temurin 17; localmente, precisa de JDK 17 ou superior. O Maven vem pelo wrapper.

```bash
./mvnw test              # roda os testes
./mvnw allure:report     # gera evidence/index.html a partir da última execução
```

No Windows, use `mvnw.cmd`. Para apontar para outro ambiente: `BASE_URL=http://localhost:3000 ./mvnw test`.

## Última execução

15/10/2026, local: 13 testes, 13 passaram. O relatório está em [evidence/index.html](evidence/index.html) (arquivo único, abre direto no navegador). Cada teste tem o request e o response anexados.

## O que está coberto

| Classe | Cenário | O que eu verifico |
|---|---|---|
| `LoginTest` | login válido | 200, mensagem, token `Bearer` e nenhum campo a mais |
| `LoginTest` | senha errada | 401 e corpo exato, sem `authorization` |
| `LoginTest` | sem `password` | 400 de validação |
| `UsuariosTest` | listar | 200 e `quantidade` igual ao tamanho da lista |
| `UsuariosTest` | id inexistente | 400 `Usuário não encontrado` (a ServeRest não usa 404) |
| `UsuariosTest` | criar | 201 e depois um GET devolvendo exatamente o que foi enviado |
| `UsuariosTest` | email duplicado | 400 |
| `UsuariosTest` | alterar | 200 e GET com o nome novo |
| `UsuariosTest` | excluir | 200 e GET passando a dar 400 |
| `ProdutosTest` | listar sem token | 200 (rota pública) |
| `ProdutosTest` | criar sem token | 401 |
| `ProdutosTest` | criar com usuário comum | 403 |
| `ProdutosTest` | criar com admin | 201 e GET devolvendo o produto enviado |

## Estrutura

```
tests-api/
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/          # Maven Wrapper
├── evidence/index.html            # relatório Allure da última execução
└── src/test/
    ├── java/io/github/felipeabreuti/api/
    │   ├── data/                  # records dos payloads e TestData (massa)
    │   ├── support/               # ApiSpec (spec comum) e Accounts (cadastro/login)
    │   ├── LoginTest.java
    │   ├── UsuariosTest.java
    │   └── ProdutosTest.java
    └── resources/junit-platform.properties   # execução paralela
```

## Por que ficou assim

Usei REST Assured porque é o que mais aparece em times de QA com backend Java, e o given/when/then com os matchers do Hamcrest deixa o contrato legível no próprio teste. Como o E2E já é Playwright, também evitei repetir a mesma ferramenta nas duas suítes.

Os testes conversam com um ambiente público e compartilhado, então todo email e nome de produto leva um sufixo aleatório com timestamp. O único dado fixo é o usuário `fulano@qa.com`, que a própria ServeRest mantém. Quando preciso de um admin, crio um na hora (`Accounts.newAdminBearerToken()`); não tem credencial guardada em lugar nenhum.

Para criação, alteração e exclusão eu não confio só na mensagem de sucesso: sempre faço um GET depois para ver se o efeito aconteceu. E comparo o corpo inteiro (`body("$", equalTo(...))`) sempre que dá, porque assim um campo a mais, como um token vazando num 401, também quebra o teste.

Os dados de entrada ficam em `data/`; as mensagens e os status esperados ficam no teste, do lado da asserção, porque são justamente o que está sendo verificado.

Os testes rodam em paralelo (4 threads). Isso só funciona porque nenhum teste depende de outro e a configuração do REST Assured fica numa `RequestSpecification` imutável, sem mexer no estado estático da biblioteca.
