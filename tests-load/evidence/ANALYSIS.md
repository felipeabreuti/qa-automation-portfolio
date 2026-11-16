# Análise do teste de carga

## Cenário

- Alvo: [QuickPizza](https://quickpizza.grafana.com), o app que a Grafana mantém para testes com k6.
- 100 VUs por 5 minutos (30s de ramp-up, 4m estável, 30s de ramp-down).
- Cada iteração faz `POST /api/pizza` (recomendação, com cálculo de combinações de ingredientes) e `GET /api/ratings` (leitura), com 1–3s de pausa entre as chamadas.
- Comecei apontando para o `test-api.k6.io`, mas ele não funciona mais como API (`/user/register/` devolve 403 do CloudFront e `/public/crocodiles/` devolve o HTML de uma SPA). Troquei para o QuickPizza.

## Resultados

Rodei o mesmo cenário seis vezes. As runs 1–4 usaram os thresholds da primeira versão; as runs 5 e 6 usaram os thresholds recalibrados. A run 6, feita em 16/11/2026 com k6 2.4.0 local, gerou os relatórios `summary-load.html` e `summary-load.json` atuais e falhou nos thresholds.

| Métrica                        | Run 1      | Run 2    | Run 3    | Run 4    | Run 5    | Run 6 |
|---------------------------------|------------|----------|----------|----------|----------|-------|
| Requisições                     | 12.432     | 12.530   | 12.464   | 12.512   | 12.460   | 23.679 |
| http_req_failed (rate)          | 0,17%      | 0%       | 0%       | 0%       | 0%       | 59,05% |
| checks (rate)                   | 99,88%     | 100%     | 100%     | 100%     | 100%     | 40,94% |
| http_req_duration p95           | 251,7 ms   | 249,7 ms | 250,3 ms | 251,2 ms | 256,9 ms | 259,1 ms* |
| http_req_duration p99           | —          | —        | —        | —        | 271,5 ms | 474,4 ms* |
| http_req_duration max           | 10.153 ms  | 353,9 ms | 349,4 ms | 442,9 ms | 465,0 ms | 15h53m* |
| recommendation_duration (p95)   | 258,1 ms   | 256,0 ms | 255,5 ms | 256,5 ms | 262,6 ms | 259,9 ms* |
| ratings_duration (p95)          | 153,2 ms   | 154,3 ms | 151,6 ms | 155,1 ms | 157,6 ms | 234,4 ms* |

\* A run 6 teve muitas falhas de conexão e amostras de duração anômalas. As métricas de latência dessa run não são comparáveis com as runs anteriores.

O p99 só aparece na run 5 porque o summary padrão do k6 não exibe p99; adicionei `summaryTrendStats` para ele entrar no relatório.

## O que os números mostram

**As cinco primeiras execuções foram estáveis; a sexta não.** Nas runs 1–5, o p95 ficou entre 249 e 257 ms. Na run 6, 59,05% das requisições falharam e 40,94% dos checks passaram. Os logs incluem timeout, `network is unreachable` e `can't assign requested address`; não dá para atribuir o resultado apenas à capacidade do QuickPizza sem separar um possível limite de rede/portas do gerador local.

**A run 1 teve um pico de ~10,15 s** nos dois endpoints praticamente ao mesmo tempo (diferença de 11 ms). Como são endpoints com lógica diferente, o mais provável é algo compartilhado na frente deles (fila, conexão, cold start da infra pública), e não um problema de um endpoint específico. Não reproduziu nas runs 2–5; a run 6 teve falhas muito mais amplas e precisa ser investigada separadamente.

**`POST /api/pizza` é ~40% mais lento que `GET /api/ratings`** (p95 ~260 ms contra ~155 ms). Faz sentido, já que a recomendação calcula combinações e ratings só lê. Se o tráfego de recomendação crescer, é o primeiro endpoint que eu olharia.

## O que mudei depois das runs 1–4

- Thresholds recalibrados para perto da baseline: `http_req_duration` p95<400 ms e p99<800 ms, `recommendation_duration` p95<400 ms e `ratings_duration` p95<250 ms. Agora uma regressão de ~50% já quebra o teste, e o threshold por endpoint mostra qual dos dois piorou.
- Perfil `smoke` (5 VUs por ~35s), que roda no CI a cada push. O perfil `load` completo continua manual (`workflow_dispatch`), porque são 5 minutos contra um ambiente público.
- A validação do corpo do POST agora só faz parse quando há resposta 200 com corpo; JSON inválido vira check reprovado, sem interromper o VU com uma exceção secundária.

## O que ainda falta

- Repetir a execução completa em um gerador de carga estável e monitorado, depois de confirmar se as falhas de rede vieram do cliente ou do serviço.
- Se a repetição continuar falhando, coletar output bruto por timestamp (`k6 run --out json=raw.json`) e investigar a capacidade do serviço antes de subir o número de VUs.
- Não usar as latências da run 6 como baseline nem tratar o relatório atual como aprovado.

## Conclusão

As runs 1–5 passaram, incluindo a run 5 com thresholds recalibrados. A run 6 falhou e não permite concluir se houve saturação do QuickPizza ou limitação de rede no gerador local. Até investigar e repetir em ambiente controlado, a última execução completa fica registrada como reprovada; o smoke continua sendo a validação confiável para CI.
