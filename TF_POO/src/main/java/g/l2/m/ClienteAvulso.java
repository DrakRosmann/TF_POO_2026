import java.time.LocalDateTime;
import java.time.Duration;
package g.l2.m.modelo;

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

    public Duration calculaTempo() {
        LocalDateTime saida = LocalDateTime.now();
        return Duration.between(entrada, saida);
    }

    public void cobranca() {
        int valor = 12;
        calculaTempo() = Duration
    }
}