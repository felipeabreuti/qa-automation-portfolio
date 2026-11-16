import { test } from '../src/fixtures/pages.fixture';
import { performanceGlitchUser, standardUser } from '../src/data/users';
import { customer } from '../src/data/checkout';

test.describe('Checkout E2E', () => {
  test.beforeEach(async ({ loginPage, inventoryPage, cartPage }) => {
    await loginPage.open();
    await loginPage.login(standardUser);
    await inventoryPage.expectLoaded();

    await inventoryPage.addBackpackToCart();
    await inventoryPage.expectCartBadge(1);

    await inventoryPage.openCart();
    await cartPage.expectLoadedWithItems(1);
  });

  test('navegação do carrinho até o formulário de checkout', async ({ cartPage, checkoutPage }) => {
    await cartPage.proceedToCheckout();
    await checkoutPage.expectInformationForm();
  });

  test('formulário sem nome bloqueia o avanço', async ({ cartPage, checkoutPage }) => {
    await cartPage.proceedToCheckout();
    await checkoutPage.fillInformation({ ...customer, firstName: '' });
    await checkoutPage.continue();
    await checkoutPage.expectInformationError('Error: First Name is required');
  });

  test('compra completa até a confirmação do pedido', async ({ cartPage, checkoutPage }) => {
    await cartPage.proceedToCheckout();
    await checkoutPage.fillInformation(customer);
    await checkoutPage.continue();
    await checkoutPage.expectOverview(1, 'Item total: $29.99');

    await checkoutPage.finish();
    await checkoutPage.expectComplete('Thank you for your order!');
  });
});

test.describe('Checkout E2E com performance_glitch_user', () => {
  test('compra completa mesmo com a aplicação lenta', async ({
    loginPage,
    inventoryPage,
    cartPage,
    checkoutPage,
  }) => {
    await loginPage.open();
    await loginPage.login(performanceGlitchUser);
    await inventoryPage.expectLoaded();

    await inventoryPage.addBackpackToCart();
    await inventoryPage.openCart();
    await cartPage.expectLoadedWithItems(1);

    await cartPage.proceedToCheckout();
    await checkoutPage.fillInformation(customer);
    await checkoutPage.continue();
    await checkoutPage.expectOverview(1, 'Item total: $29.99');

    await checkoutPage.finish();
    await checkoutPage.expectComplete('Thank you for your order!');
  });
});
