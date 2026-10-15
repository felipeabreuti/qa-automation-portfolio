package io.github.felipeabreuti.api.data;

public record Credentials(String email, String password) {

    public Credentials withPassword(String newPassword) {
        return new Credentials(email, newPassword);
    }
}
