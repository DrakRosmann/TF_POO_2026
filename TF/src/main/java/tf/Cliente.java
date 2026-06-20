package tf;

import java.util.HashSet;
import java.util.Set;

public abstract class Cliente {
    private String nome;
    private String documento;
    protected Set<String> placas;

    public Cliente(String nome, String documento) {
        this.nome = nome;
        this.documento = documento;
        this.placas = new HashSet<>(); //Para não ter duplicação de placas
    }

    public String getNome() {
        return nome;
    }

    public String getDocumento() {
        return documento;
    }

    public Set<String> getPlacas() {
        return placas;
    }

    public boolean removerPlaca(String placa) {
        return placas.remove(placa);
    }

    public abstract boolean adicionarPlaca(String placa);

    public abstract <TipoCliente> TipoCliente getTipoCliente();
}