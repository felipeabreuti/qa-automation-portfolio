package io.github.felipeabreuti.api.data;

import java.util.Map;

public record ProductPayload(String nome, int preco, String descricao, int quantidade) {

    public Map<String, Object> storedAs(String id) {
        return Map.of(
                "nome", nome,
                "preco", preco,
                "descricao", descricao,
                "quantidade", quantidade,
                "_id", id);
    }
}
