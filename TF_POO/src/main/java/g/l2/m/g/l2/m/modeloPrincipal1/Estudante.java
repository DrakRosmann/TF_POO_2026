package g.l2.m.modeloPrincipal1;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Estudante extends ClientePre {
    private double valor;
    public Estudante(String nome, String cpf){
        super(nome, cpf);
    }

    @Override
    public boolean adicionarPlaca(String placa) {
        if (placas.isEmpty()){
            placas.add(placa);
        }
        return false;
    }

    @Override
    public double calculaCusto(LocalDateTime entrada, LocalDateTime saida) {
        long dias = ChronoUnit.DAYS.between(entrada.toLocalDate(), saida.toLocalDate());
        return 12.0 * (dias + 1);
    }
}
