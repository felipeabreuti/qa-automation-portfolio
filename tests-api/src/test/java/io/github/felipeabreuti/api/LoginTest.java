package io.github.felipeabreuti.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.github.felipeabreuti.api.data.TestData.INVALID_PASSWORD;
import static io.github.felipeabreuti.api.data.TestData.SEED_USER;
import static io.github.felipeabreuti.api.support.ApiSpec.SERVEREST;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

@DisplayName("POST /login")
class LoginTest {

    @Test
    @DisplayName("credenciais válidas retornam 200 e token Bearer")
    void validCredentialsReturnToken() {
        given().spec(SERVEREST).body(SEED_USER)
                .when().post("/login")
                .then()
                .statusCode(200)
                .body("message", equalTo("Login realizado com sucesso"))
                .body("authorization", matchesPattern("^Bearer \\S+$"))
                .body("size()", is(2));
    }

    @Test
    @DisplayName("senha inválida retorna 401 sem token")
    void invalidPasswordReturns401() {
        given().spec(SERVEREST).body(SEED_USER.withPassword(INVALID_PASSWORD))
                .when().post("/login")
                .then()
                .statusCode(401)
                .body("$", equalTo(Map.of("message", "Email e/ou senha inválidos")));
    }

    @Test
    @DisplayName("corpo sem password retorna 400 de validação")
    void missingPasswordReturns400() {
        given().spec(SERVEREST).body(Map.of("email", SEED_USER.email()))
                .when().post("/login")
                .then()
                .statusCode(400)
                .body("$", equalTo(Map.of("password", "password é obrigatório")));
    }
}
