package io.github.felipeabreuti.api.data;

import java.util.UUID;

public final class TestData {

    public static final Credentials SEED_USER = new Credentials("fulano@qa.com", "teste");
    public static final String INVALID_PASSWORD = "senhaerrada";
    public static final String NONEXISTENT_USER_ID = "abcdef0123456789";
    public static final String UPDATED_NAME = "Nome Alterado";

    private TestData() {
    }

    public static UserPayload user() {
        return new UserPayload("QA User " + uniqueSuffix(), uniqueEmail(), "123456", "false");
    }

    public static UserPayload admin() {
        return new UserPayload("QA Admin " + uniqueSuffix(), uniqueEmail(), "123456", "true");
    }

    public static ProductPayload product() {
        return new ProductPayload("Produto QA " + uniqueSuffix(), 100, "Produto gerado pela suíte de testes", 1);
    }

    private static String uniqueEmail() {
        return "qa-user." + uniqueSuffix() + "@mail.com";
    }

    private static String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8) + "." + System.currentTimeMillis();
    }
}
