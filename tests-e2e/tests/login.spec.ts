import { test } from '../src/fixtures/pages.fixture';
import {
  standardUser,
  lockedOutUser,
  performanceGlitchUser,
  invalidPassword,
} from '../src/data/users';

test.describe('Login E2E', () => {
  test('login válido navega para a inventory', async ({ loginPage, inventoryPage }) => {
    await loginPage.open();
    await loginPage.login(standardUser);
    await inventoryPage.expectLoaded();
  });

  test('performance_glitch_user chega à inventory sem espera fixa', async ({ loginPage, inventoryPage }) => {
    await loginPage.open();

    const startedAt = Date.now();
    await loginPage.login(performanceGlitchUser);
    await inventoryPage.expectLoaded();

    test.info().annotations.push({
      type: 'tempo do login (ms)',
      description: String(Date.now() - startedAt),
    });
  });

  test('usuário bloqueado permanece no login com mensagem de erro', async ({ loginPage }) => {
    await loginPage.open();
    await loginPage.login(lockedOutUser);
    await loginPage.expectLoginError('Epic sadface: Sorry, this user has been locked out.');
  });

  test('senha inválida permanece no login com mensagem de erro', async ({ loginPage }) => {
    await loginPage.open();
    await loginPage.login({ ...standardUser, password: invalidPassword });
    await loginPage.expectLoginError(
      'Epic sadface: Username and password do not match any user in this service',
    );
  });
});
