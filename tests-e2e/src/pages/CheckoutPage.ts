import { Page, expect } from '@playwright/test';
import { checkoutLocators } from '../locators/checkout.locators';
import { CustomerInfo } from '../data/checkout';

export class CheckoutPage {
  constructor(private readonly page: Page) {}

  async fillInformation({ firstName, lastName, postalCode }: CustomerInfo) {
    await this.page.locator(checkoutLocators.firstNameInput).fill(firstName);
    await this.page.locator(checkoutLocators.lastNameInput).fill(lastName);
    await this.page.locator(checkoutLocators.postalCodeInput).fill(postalCode);
  }

  async continue() {
    await this.page.locator(checkoutLocators.continueButton).click();
  }

  async finish() {
    await this.page.locator(checkoutLocators.finishButton).click();
  }

  async expectInformationForm() {
    await expect(this.page).toHaveURL(/\/checkout-step-one\.html$/);
    await expect(this.page.locator(checkoutLocators.title)).toHaveText('Checkout: Your Information');
    await expect(this.page.locator(checkoutLocators.firstNameInput)).toBeVisible();
    await expect(this.page.locator(checkoutLocators.lastNameInput)).toBeVisible();
    await expect(this.page.locator(checkoutLocators.postalCodeInput)).toBeVisible();
    await expect(this.page.locator(checkoutLocators.continueButton)).toBeEnabled();
  }

  async expectInformationError(message: string) {
    await expect(this.page.locator(checkoutLocators.errorMessage)).toHaveText(message);
    await expect(this.page).toHaveURL(/\/checkout-step-one\.html$/);
  }

  async expectOverview(itemCount: number, subtotal: string) {
    await expect(this.page).toHaveURL(/\/checkout-step-two\.html$/);
    await expect(this.page.locator(checkoutLocators.title)).toHaveText('Checkout: Overview');
    await expect(this.page.locator(checkoutLocators.summaryItem)).toHaveCount(itemCount);
    await expect(this.page.locator(checkoutLocators.subtotalLabel)).toHaveText(subtotal);
  }

  async expectComplete(header: string) {
    await expect(this.page).toHaveURL(/\/checkout-complete\.html$/);
    await expect(this.page.locator(checkoutLocators.title)).toHaveText('Checkout: Complete!');
    await expect(this.page.locator(checkoutLocators.completeHeader)).toHaveText(header);
  }
}
