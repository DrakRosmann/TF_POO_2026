package g.l2.m.modeloPrincipal1;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Estudante extends Cliente{
    private static final double VALOR_DIARIA = 12;
    private static final int MAX_VEICULOS = 1;
    private double creditos;
    public Estudante(String cpf, String nome, String celular, double saldoInicial){
        super(cpf,nome,celular);
        creditos = saldoInicial;
    }

    public void setCreditos(double creditos) {
        this.creditos = creditos;
    }

    public double getCreditos() {
        return creditos;
    }

    @Override
    public void cadastraVeiculo(String placa) {
        if (placas_veiculos.isEmpty()){
            super.cadastraVeiculo(placa);
        }else {
            throw new IllegalArgumentException("LIMITE DE VEICULOS EXCEDIDO");
        }
    }

    @Override
    public TicketEntradaSaida calculaCusto(TicketEntradaSaida ticket, LocalDateTime horaSaida) {
        ticket.setHoraSaida(horaSaida);

        long dias = ChronoUnit.DAYS.between(ticket.getHoraEntrada().toLocalDate(), horaSaida.toLocalDate());
        double custoTotal = VALOR_DIARIA;

        if (dias > 0) {
            custoTotal += (dias * VALOR_DIARIA);
        }

        ticket.setValorCalculado(custoTotal);
        ticket.setDesconto(0.0);
        ticket.setValorCobrado(custoTotal);

        creditos -= custoTotal;

        return ticket;
    }
}
