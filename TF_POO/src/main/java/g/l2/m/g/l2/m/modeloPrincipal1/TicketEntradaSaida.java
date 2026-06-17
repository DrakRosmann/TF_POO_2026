package g.l2.m.modeloPrincipal1;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TicketEntradaSaida {
    private String placaVeiculo;
    private int numeroVaga;
    private CategoriaCliente categoriaCliente;

    private LocalDateTime horaEntrada;
    private LocalDateTime horaSaida;

    private double valorCalculado;
    private double desconto;
    private double valorCobrado;

    public TicketEntradaSaida(String placaVeiculo,int numeroVaga, CategoriaCliente categoriaCliente, LocalDateTime horaEntrada){
        if (placaVeiculo == null || placaVeiculo.trim().isEmpty()){
            throw new IllegalArgumentException("A placa do veículo é obrigatória");
        }
        this.placaVeiculo = placaVeiculo.toUpperCase();
        this.numeroVaga = numeroVaga;
        this.categoriaCliente = categoriaCliente;
        this.horaEntrada = horaEntrada;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalDateTime getHoraSaida() {
        return horaSaida;
    }

    public CategoriaCliente getCategoriaCliente() {
        return categoriaCliente;
    }

    public void setValorCalculado(double valorCalculado) {
        this.valorCalculado = valorCalculado;
    }

    public void setValorCobrado(double valorCobrado) {
        this.valorCobrado = valorCobrado;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public void setHoraSaida(LocalDateTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public long calcularTotalHoras(){
        if(horaSaida == null) {
            throw new IllegalArgumentException("O ticket ainda não registrou o horário de saída");
            long minutos = ChronoUnit.MINUTES.between(horaEntrada, horaSaida);
            return (long) Math.ceil(minutos / 60.0);
        }
        return -1;
    }
}
