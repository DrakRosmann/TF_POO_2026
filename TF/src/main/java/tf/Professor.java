package tf;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
public abstract class Professor extends ClientePre {


    public Professor(String nome, String cpf) {
        super(nome, cpf);
        placas = new HashSet<>();
    }

    public boolean adicionarPlaca(String placa) {
        if (2 >= placas.size()){
            return false;
        }
        return placas.add(placa);
    }

    @Override
    public double calculaCusto(LocalDateTime entrada, LocalDateTime saida) {
        return 0;
    }

    public Set<String> getPlacas() {
            return placas;
        }
}


