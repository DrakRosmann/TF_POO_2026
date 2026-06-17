package g.l2.m.modeloPrincipal1;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Empresa extends Cliente{
    private static final int DIARIA = 24;
    private static final double MULTA_PERNOITE = 50.0;
    private int debitos;

    public Empresa(String cnpj, String nome, String celular){
        super(cnpj, nome, celular);
        debitos = 0;
    }

    public int getDebitos() {
        return debitos;
    }

    public TicketEntradaSaida calculaCusto(TicketEntradaSaida ticket, LocalDateTime horaSaida) {
        ticket.setHoraSaida(horaSaida);

        long dias = ChronoUnit.DAYS.between(ticket.getHoraEntrada().toLocalDate(), horaSaida.toLocalDate());
        double custoTotal = DIARIA;

        if (dias > 0) {
            custoTotal += (dias * DIARIA) + (dias * MULTA_PERNOITE);
        }

        ticket.setValorCalculado(custoTotal);
        ticket.setDesconto(0.0);
        ticket.setValorCobrado(custoTotal);

        this.debitos += custoTotal;

        return ticket;
    }
}
