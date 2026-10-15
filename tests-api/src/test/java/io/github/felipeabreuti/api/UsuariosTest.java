package io.github.felipeabreuti.api;

import io.github.felipeabreuti.api.data.TestData;
import io.github.felipeabreuti.api.data.UserPayload;
import io.github.felipeabreuti.api.support.Accounts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.github.felipeabreuti.api.data.TestData.NONEXISTENT_USER_ID;
import static io.github.felipeabreuti.api.data.TestData.SEED_USER;
import static io.github.felipeabreuti.api.data.TestData.UPDATED_NAME;
import static io.github.felipeabreuti.api.support.ApiSpec.SERVEREST;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

@DisplayName("/usuarios")
class UsuariosTest {

    @Test
    @DisplayName("GET lista usuários com quantidade coerente")
    void listUsers() {
        given().spec(SERVEREST)
                .when().get("/usuarios")
                .then()
                .statusCode(200)
                .body("usuarios", instanceOf(List.class))
                .body("quantidade", response -> equalTo(response.path("usuarios.size()")));
    }

    @Test
    @DisplayName("GET com id inexistente retorna 400")
    void getNonexistentUserReturns400() {
        given().spec(SERVEREST)
                .when().get("/usuarios/{id}", NONEXISTENT_USER_ID)
                .then()
                .statusCode(400)
                .body("$", equalTo(Map.of("message", "Usuário não encontrado")));
    }

    @Test
    @DisplayName("POST cria usuário e o GET devolve o que foi enviado")
    void createUser() {
        UserPayload user = TestData.admin();

        String id = given().spec(SERVEREST).body(user)
                .when().post("/usuarios")
                .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", matchesPattern("^[A-Za-z0-9]{16}$"))
                .body("size()", is(2))
                .extract().path("_id");

        given().spec(SERVEREST)
                .when().get("/usuarios/{id}", id)
                .then()
                .statusCode(200)
                .body("$", equalTo(user.storedAs(id)));
    }

    @Test
    @DisplayName("POST com email já cadastrado retorna 400")
    void createUserWithDuplicateEmailReturns400() {
        given().spec(SERVEREST).body(TestData.admin().withEmail(SEED_USER.email()))
                .when().post("/usuarios")
                .then()
                .statusCode(400)
                .body("$", equalTo(Map.of("message", "Este email já está sendo usado")));
    }

    @Test
    @DisplayName("PUT altera o nome e o GET confirma a alteração")
    void updateUser() {
        UserPayload user = TestData.admin();
        String id = Accounts.register(user);
        UserPayload updated = user.withNome(UPDATED_NAME);

        given().spec(SERVEREST).body(updated)
                .when().put("/usuarios/{id}", id)
                .then()
                .statusCode(200)
                .body("$", equalTo(Map.of("message", "Registro alterado com sucesso")));

        given().spec(SERVEREST)
                .when().get("/usuarios/{id}", id)
                .then()
                .statusCode(200)
                .body("$", equalTo(updated.storedAs(id)));
    }

    @Test
    @DisplayName("DELETE remove o usuário e o GET passa a retornar 400")
    void deleteUser() {
        String id = Accounts.register(TestData.admin());

        given().spec(SERVEREST)
                .when().delete("/usuarios/{id}", id)
                .then()
                .statusCode(200)
                .body("$", equalTo(Map.of("message", "Registro excluído com sucesso")));

        given().spec(SERVEREST)
                .when().get("/usuarios/{id}", id)
                .then()
                .statusCode(400)
                .body("$", equalTo(Map.of("message", "Usuário não encontrado")));
    }
}
