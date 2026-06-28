package g.l2.m;

import java.time.LocalDateTime;

public class TicketEntradaSaida {
    private String placaVeiculo;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSaida;
    private double valorCalculado;
    private double desconto;
    private double valorCobrado;
    private CategoriaCliente categoria;

    public TicketEntradaSaida(String placaVeiculo, CategoriaCliente categoria, LocalDateTime horaEntrada) {
        if (placaVeiculo == null || placaVeiculo.trim().isEmpty()){
            throw new IllegalArgumentException("A placa do veículo é obrigatória");
        }
        this.placaVeiculo = placaVeiculo;
        this.categoria = categoria;
        this.horaEntrada = horaEntrada;
        this.valorCalculado = 0.0;
        this.desconto = 0.0;
        this.valorCobrado = 0.0;
    }

    public String getPlacaVeiculo() { return placaVeiculo; }
    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public LocalDateTime getHoraSaida() { return horaSaida; }
    public void setHoraSaida(LocalDateTime horaSaida) { this.horaSaida = horaSaida; }
    public double getValorCalculado() { return valorCalculado; }
    public void setValorCalculado(double valorCalculado) { this.valorCalculado = valorCalculado; }
    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }
    public double getValorCobrado() { return valorCobrado; }
    public void setValorCobrado(double valorCobrado) { this.valorCobrado = valorCobrado; }
    public CategoriaCliente getCategoria() { return categoria; }
}