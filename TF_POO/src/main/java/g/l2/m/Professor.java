import java.time.LocalDateTime;

public class Professor extends Cliente{
    private static final int MAX_VEICULOS = 2;

    public Professor(String cpf, String nome, String celular){
        super(cpf,nome,celular);
    }

    @Override;
    public void cadastraVeiculo(String placa) {
        if (placas_veiculos.size() < MAX_VEICULOS) {
            super.cadastraVeiculo(placa);
        }else {
            throw new IllegalArgumentException("LIMITE DE VEICULOS EXCEDIDO");
        }
    }

    @Override
    public TicketEntradaSaida calculaCusto(TicketEntradaSaida ticket, LocalDateTime horaSaida) {
        ticket.setHoraSaida(horaSaida);
        ticket.setValorCalculado(0.0);
        ticket.setDesconto(0.0);
        ticket.setValorCobrado(0.0);
        return ticket;
    }
}
