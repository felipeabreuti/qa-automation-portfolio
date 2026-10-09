# tests-load

Teste de carga com k6 no [QuickPizza](https://quickpizza.grafana.com).

Cada usuário virtual pede uma recomendação de pizza (`POST /api/pizza`) e depois lê as avaliações (`GET /api/ratings`), com 1 a 3 segundos de pausa entre as chamadas.

## Rodando

Precisa do [k6](https://grafana.com/docs/k6/latest/set-up/install-k6/) (usei a 2.3.0).

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

## Resultado

Última execução completa (06/10/2026): 12.460 requisições, 0% de erro, p95 de 256,9 ms. Passou em todos os thresholds.

- [summary-load.html](evidence/summary-load.html)
- [ANALYSIS.md](evidence/ANALYSIS.md), com a comparação das cinco execuções

## Observações

- Comecei com p95 < 800 ms, mas o sistema responde em ~250 ms. Com essa folga a latência podia dobrar sem o teste falhar, então baixei os limites para perto do medido e coloquei um por endpoint.
- No CI roda só o smoke. O completo leva 5 minutos contra um ambiente público, então fica manual (`workflow_dispatch`).
- A pausa entre as chamadas é aleatória para os VUs não ficarem sincronizados.
- O reporter HTML está fixado na versão 3.0.4.
