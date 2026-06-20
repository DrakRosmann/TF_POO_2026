import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public abstract class Cliente {
    private String cpf_cnpj;
    private String nome;
    private String celular;
    protected Set<String> placas_veiculos;

    public Cliente(String cpf_cnpj, String nome, String celular) {
        this.cpf_cnpj = cpf_cnpj;
        this.nome = nome;
        this.celular = celular;
        placas_veiculos = new HashSet<>();
    }

    public String getCpf_cnpj() {
        return cpf_cnpj;
    }

    public String getNome() {
        return nome;
    }

    public Set<String> getPlacas_veiculos() {
        return placas_veiculos;
    }

    public String getCelular() {
        return celular;
    }

    public void cadastraVeiculo(String placa){
        placas_veiculos.add(placa);
    }
    public void removeVeiculo(String placa){
        placas_veiculos.remove(placa);
    }
    public abstract TicketEntradaSaida calculaCusto(TicketEntradaSaida ticket, LocalDateTime horaSaida);
}
