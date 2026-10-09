# Análise do teste de carga

## Cenário

- Alvo: [QuickPizza](https://quickpizza.grafana.com), o app que a Grafana mantém para testes com k6.
- 100 VUs por 5 minutos (30s de ramp-up, 4m estável, 30s de ramp-down).
- Cada iteração faz `POST /api/pizza` (recomendação, com cálculo de combinações de ingredientes) e `GET /api/ratings` (leitura), com 1–3s de pausa entre as chamadas.
- Comecei apontando para o `test-api.k6.io`, mas ele não funciona mais como API (`/user/register/` devolve 403 do CloudFront e `/public/crocodiles/` devolve o HTML de uma SPA). Troquei para o QuickPizza.

## Resultados

Rodei o mesmo cenário cinco vezes. As runs 1–4 usaram os thresholds da primeira versão do script; a run 5 já usa os thresholds recalibrados (ver abaixo) e é a que gerou `summary-load.html` e `summary-load.json` nesta pasta.

| Métrica                        | Run 1      | Run 2    | Run 3    | Run 4    | Run 5    |
|---------------------------------|------------|----------|----------|----------|----------|
| Requisições                     | 12.432     | 12.530   | 12.464   | 12.512   | 12.460   |
| http_req_failed (rate)          | 0,17%      | 0%       | 0%       | 0%       | 0%       |
| checks (rate)                   | 99,88%     | 100%     | 100%     | 100%     | 100%     |
| http_req_duration p95           | 251,7 ms   | 249,7 ms | 250,3 ms | 251,2 ms | 256,9 ms |
| http_req_duration p99           | —          | —        | —        | —        | 271,5 ms |
| http_req_duration max           | 10.153 ms  | 353,9 ms | 349,4 ms | 442,9 ms | 465,0 ms |
| recommendation_duration (p95)   | 258,1 ms   | 256,0 ms | 255,5 ms | 256,5 ms | 262,6 ms |
| ratings_duration (p95)          | 153,2 ms   | 154,3 ms | 151,6 ms | 155,1 ms | 157,6 ms |

O p99 só aparece na run 5 porque o summary padrão do k6 não exibe p99; adicionei `summaryTrendStats` para ele entrar no relatório.

## O que os números mostram

**100 VUs não estressam o sistema.** O p95 ficou entre 249 e 257 ms nas cinco execuções. Com o threshold inicial (`p95<800ms`) havia ~3x de folga, ou seja, a latência poderia dobrar e o teste continuaria verde.

**A run 1 teve um pico de ~10,15 s** nos dois endpoints praticamente ao mesmo tempo (diferença de 11 ms). Como são endpoints com lógica diferente, o mais provável é algo compartilhado na frente deles (fila, conexão, cold start da infra pública), e não um problema de um endpoint específico. Não reproduziu nas runs 2–5, então trato como pontual, mas cinco amostras não são suficientes para descartar algo raro.

**`POST /api/pizza` é ~40% mais lento que `GET /api/ratings`** (p95 ~260 ms contra ~155 ms). Faz sentido, já que a recomendação calcula combinações e ratings só lê. Se o tráfego de recomendação crescer, é o primeiro endpoint que eu olharia.

## O que mudei depois das runs 1–4

- Thresholds recalibrados para perto da baseline: `http_req_duration` p95<400 ms e p99<800 ms, `recommendation_duration` p95<400 ms e `ratings_duration` p95<250 ms. Agora uma regressão de ~50% já quebra o teste, e o threshold por endpoint mostra qual dos dois piorou.
- Perfil `smoke` (5 VUs por ~35s), que roda no CI a cada push. O perfil `load` completo continua manual (`workflow_dispatch`), porque são 5 minutos contra um ambiente público.

## O que ainda falta

- Investigar o pico de 10 s com output bruto por timestamp (`k6 run --out json=raw.json`) para ver se acontece em algum momento específico.
- Subir para 200–500 VUs: 100 VUs não chegou perto do limite, então o ponto de quebra ainda é desconhecido.

## Conclusão

O QuickPizza aguentou 100 VUs por 5 minutos sem violar threshold em cinco execuções, e na run 5 isso já foi verificado com limites que de fato pegam regressão. O pico isolado da run 1 continua em aberto.
