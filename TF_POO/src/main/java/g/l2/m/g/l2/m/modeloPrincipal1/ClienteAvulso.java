package g.l2.m.modeloPrincipal1;

import java.time.Duration;
import java.time.LocalDateTime;


public class ClienteAvulso {
    private String placa;
    private LocalDateTime entrada;

    public ClienteAvulso(String placa) {
        this.placa = placa;
        this.entrada = LocalDateTime.now();
    }

    public String getPlaca() {
        return placa;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public long calculaTempo() {
        LocalDateTime saida = LocalDateTime.now();
        return Duration.between(entrada, saida).toHours();
    }

    public long cobranca() {
        int valor = 12;
        long tempo = calculaTempo();
        int valorAdicional = 24;
        if(tempo <= 6) {
            return valor * tempo;
        }
        if(tempo > 6) {
            return valor * tempo + valorAdicional;
        }
        if(entrada)
    }
}