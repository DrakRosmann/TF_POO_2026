package tf;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Empresa extends ClientePre {
    private double valorAcumulados;
    private boolean inadimplente;

    public Empresa(String nome, String cnpj) {
        super(nome, cnpj);
        this.valorAcumulados = 0;
        this.inadimplente = false;
    }

    @Override
    public boolean adicionarPlaca(String placa) {
        placas.add(placa);
        return true;
    }

    @Override
    public double calculaCusto(LocalDateTime entrada, LocalDateTime saida) {
        long dias = ChronoUnit.DAYS.between(entrada.toLocalDate(), saida.toLocalDate());
        double custoBase = 30.0 * (dias + 1);
        if (saida.getHour() == 0 && saida.getMinute() > 0 || dias > 0) {
            custoBase += 50.0;
        }
        return custoBase;
    }

    public boolean isInadimplente() { return inadimplente; }
    public void adicionarDebito(double valor) { this.valorAcumulados += valor; }
}