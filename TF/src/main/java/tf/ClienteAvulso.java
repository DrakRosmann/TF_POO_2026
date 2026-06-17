package tf;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

public class ClienteAvulso {
    private String placa;
    private LocalDateTime entrada;
    private HashSet<String> listaDoBarbaNegra = new HashSet<>();

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
        int hora = entrada.getHour();
        long pagarSem = valor * tempo;
        long pagarCom = valor * tempo + valorAdicional;

        if (tempo <= 6) {
            return pagarSem;
        } else if (tempo > 6 && tempo < 24) {
            return pagarCom;
        } else if (hora + tempo > 24 && tempo <= 6) {
            return pagarSem + 12;
        } else if (hora + tempo > 24 && tempo > 6) {
            return pagarCom + 12;
        }

        // Retorno padrão exigido pelo Java caso nenhuma condição seja aceita
        return 0;
    }

    public void quer(boolean pagarSimxNao) {
        if(pagarSimxNao == true) {
            cobranca();
        }
        else {
            listaDoBarbaNegra.add(placa);
        }
    }
}