package io.github.felipeabreuti.api.support;

import io.github.felipeabreuti.api.data.TestData;
import io.github.felipeabreuti.api.data.UserPayload;

import static io.github.felipeabreuti.api.support.ApiSpec.SERVEREST;
import static io.restassured.RestAssured.given;

public final class Accounts {

    private Accounts() {
    }

    public static String register(UserPayload user) {
        return given().spec(SERVEREST).body(user)
                .when().post("/usuarios")
                .then().statusCode(201)
                .extract().path("_id");
    }

    public static String bearerTokenFor(UserPayload user) {
        return given().spec(SERVEREST).body(user.credentials())
                .when().post("/login")
                .then().statusCode(200)
                .extract().path("authorization");
    }

    public static String newAdminBearerToken() {
        UserPayload admin = TestData.admin();
        register(admin);
        return bearerTokenFor(admin);
    }
}
