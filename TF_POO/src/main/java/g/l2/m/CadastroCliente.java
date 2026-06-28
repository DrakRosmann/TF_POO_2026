package g.l2.m;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service // <-- ADICIONE ESTA LINHA AQUI
public class CadastroCliente {
    private Map<String, Cliente> clientes;
    private Map<String, Cliente> placasCliente;

    public CadastroCliente() {
        this.clientes = new HashMap<>();
        this.placasCliente = new HashMap<>();
    }

    // ... resto do seu código continua igual ...

public void adicionarCliente(Cliente cliente) {
    clientes.put(cliente.getCpf_cnpj(), cliente);
    for (String placa : cliente.getPlacas_veiculos()) {
        placasCliente.put(placa, cliente);
    }
}

public Cliente buscarPorCpfCnpj(String documento) {
    return clientes.get(documento);
}

public Cliente buscarPorPlaca(String placa) {
    return placasCliente.get(placa);
}
}
