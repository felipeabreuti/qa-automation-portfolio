# tests-load

Teste de carga com k6 no [QuickPizza](https://quickpizza.grafana.com).

Cada usuário virtual pede uma recomendação de pizza (`POST /api/pizza`) e depois lê as avaliações (`GET /api/ratings`), com 1 a 3 segundos de pausa entre as chamadas.

## Rodando

Precisa do [k6](https://grafana.com/docs/k6/latest/set-up/install-k6/). O CI usa a versão 2.3.0; a execução local de 16/11/2026 foi feita com a 2.4.0.

```bash
k6 run load-test.js                    # 100 VUs por 5 min
k6 run -e PROFILE=smoke load-test.js   # 5 VUs por ~35s
```

O resultado fica em `evidence/summary-<perfil>.html`. Para trocar o alvo: `-e BASE_URL=...` e `-e AUTH_TOKEN=...`.

## Perfis

| Perfil | Ramp-up | Estável | Ramp-down |
|---|---|---|---|
| `smoke` | 10s até 5 VUs | 20s | 5s |
| `load` | 30s até 100 VUs | 4m | 30s |

## Thresholds

| Métrica | Limite |
|---|---|
| `http_req_failed` | < 1% |
| `http_req_duration` | p95 < 400 ms, p99 < 800 ms |
| `recommendation_duration` | p95 < 400 ms |
| `ratings_duration` | p95 < 250 ms |
| `checks` | > 99% |

## Última execução completa

16/11/2026 (k6 2.4.0 local): a execução de 5 minutos terminou, mas falhou nos thresholds. Foram 23.679 requisições, com 59,05% de falhas HTTP e 40,94% de checks aprovados. No POST `/api/pizza`, 25% dos checks passaram. A maior parte das falhas veio de timeout e erro de conexão durante o pico de 100 VUs.

- [summary-load.html](evidence/summary-load.html), relatório da execução reprovada mais recente
- [ANALYSIS.md](evidence/ANALYSIS.md), com a comparação das cinco execuções

O p95 de `http_req_duration{expected_response:true}` foi 286,25 ms, mas não considero essa execução uma aprovação nem uma baseline confiável enquanto as falhas de conexão não forem investigadas.

Última execução smoke (16/11/2026, k6 2.4.0): 32 iterações, 64 requisições, 0% de falhas, 100% de checks e p95 de 256,8 ms. [Relatório smoke](evidence/summary-smoke.html).

## Observações

- Comecei com p95 < 800 ms, mas o sistema responde em ~250 ms. Com essa folga a latência podia dobrar sem o teste falhar, então baixei os limites para perto do medido e coloquei um por endpoint.
- No CI roda só o smoke. O completo leva 5 minutos contra um ambiente público e pode sobrecarregar o serviço, então fica manual (`workflow_dispatch`).
- A checagem do corpo do POST só tenta interpretar JSON quando a resposta é HTTP 200 e tem corpo. Respostas sem corpo ou com JSON inválido reprovam o check sem gerar erro secundário no teste.
- A pausa entre as chamadas é aleatória para os VUs não ficarem sincronizados.
- O reporter HTML está fixado na versão 3.0.4.
