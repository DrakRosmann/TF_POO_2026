package g.l2.m.modeloPrincipal1;

import java.util.HashSet;
import java.util.Set;
public class Professor extends ClientePre {


    public Professor(String nome, String cpf) {
        super(nome, cpf);
        placas = new HashSet<>();
    }

    public boolean adicionarPlaca(String placa) {
        if (2 >= placas.size())
            return false;
            return placas.add(placa);
        }

        public Set<String> getPlacas() {
            return placas;
        }
    }
}

