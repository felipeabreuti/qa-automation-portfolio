package io.github.felipeabreuti.api.data;

import java.util.Map;

public record UserPayload(String nome, String email, String password, String administrador) {

    public UserPayload withNome(String newNome) {
        return new UserPayload(newNome, email, password, administrador);
    }

    public UserPayload withEmail(String newEmail) {
        return new UserPayload(nome, newEmail, password, administrador);
    }

    public Credentials credentials() {
        return new Credentials(email, password);
    }

    public Map<String, Object> storedAs(String id) {
        return Map.of(
                "nome", nome,
                "email", email,
                "password", password,
                "administrador", administrador,
                "_id", id);
    }
}
