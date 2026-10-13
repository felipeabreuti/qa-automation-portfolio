from appium.webdriver.common.appiumby import AppiumBy

INFORMATION_SCREEN = (AppiumBy.ACCESSIBILITY_ID, 'test-Checkout: Your Info')
FIRST_NAME_INPUT = (AppiumBy.ACCESSIBILITY_ID, 'test-First Name')
LAST_NAME_INPUT = (AppiumBy.ACCESSIBILITY_ID, 'test-Last Name')
POSTAL_CODE_INPUT = (AppiumBy.ACCESSIBILITY_ID, 'test-Zip/Postal Code')
CONTINUE_BUTTON = (AppiumBy.ACCESSIBILITY_ID, 'test-CONTINUE')
OVERVIEW_SCREEN = (AppiumBy.ACCESSIBILITY_ID, 'test-CHECKOUT: OVERVIEW')
# FINISH fica no fim do ScrollView; o UiScrollable rola até ele independente da altura da tela
FINISH_BUTTON = (
    AppiumBy.ANDROID_UIAUTOMATOR,
    'new UiScrollable(new UiSelector().description("test-CHECKOUT: OVERVIEW"))'
    '.scrollIntoView(new UiSelector().description("test-FINISH"))',
)
COMPLETE_SCREEN = (AppiumBy.ACCESSIBILITY_ID, 'test-CHECKOUT: COMPLETE!')
COMPLETE_TITLE_TEXT = (AppiumBy.XPATH, '(//*[@content-desc="test-CHECKOUT: COMPLETE!"]//android.widget.TextView)[1]')
