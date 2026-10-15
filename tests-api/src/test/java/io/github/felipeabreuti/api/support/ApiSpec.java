package io.github.felipeabreuti.api.support;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Objects;

public final class ApiSpec {

    private static final String BASE_URL =
            Objects.requireNonNullElse(System.getenv("BASE_URL"), "https://serverest.dev");

    public static final RequestSpecification SERVEREST = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .setConfig(RestAssuredConfig.config()
                    .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
            .addFilter(new AllureRestAssured())
            .build();

    private ApiSpec() {
    }
}
