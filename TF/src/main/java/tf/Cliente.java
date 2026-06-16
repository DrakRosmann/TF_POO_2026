package tf;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public abstract class Cliente {
    public Cliente(){
    }
    public abstract double calcularCusto(LocalDateTime entrada, LocalDateTime saida);
}
