const BASE_URL = __ENV.BASE_URL || 'https://quickpizza.grafana.com';
const AUTH_TOKEN = __ENV.AUTH_TOKEN || 'abcdef0123456789';
const PROFILE = __ENV.PROFILE || 'load';

const profiles = {
  smoke: [
    { duration: '10s', target: 5 },
    { duration: '20s', target: 5 },
    { duration: '5s', target: 0 },
  ],
  load: [
    { duration: '30s', target: 100 },
    { duration: '4m', target: 100 },
    { duration: '30s', target: 0 },
  ],
};

if (!profiles[PROFILE]) {
  throw new Error(`PROFILE inválido: "${PROFILE}". Use: ${Object.keys(profiles).join(', ')}`);
}

export const config = {
  baseUrl: BASE_URL,
  authHeader: `token ${AUTH_TOKEN}`,
  profile: PROFILE,
  stages: profiles[PROFILE],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<400', 'p(99)<800'],
    recommendation_duration: ['p(95)<400'],
    ratings_duration: ['p(95)<250'],
    checks: ['rate>0.99'],
  },
};
