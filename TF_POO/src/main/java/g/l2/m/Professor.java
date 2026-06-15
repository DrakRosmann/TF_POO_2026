import java.util.HashSet;
import java.util.Set;
public class Professor extends ClientePre{
    private Set<String> placas;

    public Professor(String nome, String cpf) {
        super(nome, cpf, 0);
        placas = new HashSet<>();
    }

    public boolean adicionarPlaca(String placa) {
        if (placas.size() >= 2)
            return false;
            return placas.add(placa);
        }

        public Set<String> getPlacas() {
            return placas;
        }
    }
}

