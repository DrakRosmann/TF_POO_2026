package tf;

import java.time.LocalDateTime;

public class ClienteAvulsos extends Cliente{
    private LocalDateTime entrada;
    public ClienteAvulsos(){
        entrada = LocalDateTime.now();
    }

    @Override
    public double calcularCusto(LocalDateTime entrada, LocalDateTime saida) {
        return 0;
    }
}
