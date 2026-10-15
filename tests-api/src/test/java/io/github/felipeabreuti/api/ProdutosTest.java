package io.github.felipeabreuti.api;

import io.github.felipeabreuti.api.data.ProductPayload;
import io.github.felipeabreuti.api.data.TestData;
import io.github.felipeabreuti.api.data.UserPayload;
import io.github.felipeabreuti.api.support.Accounts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.github.felipeabreuti.api.support.ApiSpec.SERVEREST;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

@DisplayName("/produtos")
class ProdutosTest {

    @Test
    @DisplayName("GET é público e lista produtos com quantidade coerente")
    void listProductsWithoutToken() {
        given().spec(SERVEREST)
                .when().get("/produtos")
                .then()
                .statusCode(200)
                .body("produtos", instanceOf(List.class))
                .body("quantidade", response -> equalTo(response.path("produtos.size()")));
    }

    @Test
    @DisplayName("POST sem token retorna 401")
    void createProductWithoutTokenReturns401() {
        given().spec(SERVEREST).body(TestData.product())
                .when().post("/produtos")
                .then()
                .statusCode(401)
                .body("$", equalTo(Map.of(
                        "message", "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais")));
    }

    @Test
    @DisplayName("POST com token de usuário comum retorna 403")
    void createProductAsRegularUserReturns403() {
        UserPayload regularUser = TestData.user();
        Accounts.register(regularUser);
        String token = Accounts.bearerTokenFor(regularUser);

        given().spec(SERVEREST).header("Authorization", token).body(TestData.product())
                .when().post("/produtos")
                .then()
                .statusCode(403)
                .body("$", equalTo(Map.of("message", "Rota exclusiva para administradores")));
    }

    @Test
    @DisplayName("POST com token de admin cria produto e o GET devolve o que foi enviado")
    void createProductAsAdmin() {
        ProductPayload product = TestData.product();

        String id = given().spec(SERVEREST).header("Authorization", Accounts.newAdminBearerToken()).body(product)
                .when().post("/produtos")
                .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", matchesPattern("^[A-Za-z0-9]{16}$"))
                .body("size()", is(2))
                .extract().path("_id");

        given().spec(SERVEREST)
                .when().get("/produtos/{id}", id)
                .then()
                .statusCode(200)
                .body("$", equalTo(product.storedAs(id)));
    }
}
