package tf;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class RegistroEstacionamento {
    private String placa;
    private TipoCliente tipoCliente;
    private String documentoCliente;

    private LocalDateTime entrada;
    private LocalDateTime saida;

    private double valorBruto;
    private String desconto;
    private double valorDesconto;
    private double valorDevido;
    private double valorPago;

    public RegistroEstacionamento(String placa, TipoCliente tipoCliente, String documentoCliente) {
        this.placa = placa;
        this.tipoCliente = tipoCliente;
        this.documentoCliente = documentoCliente;
        this.entrada = LocalDateTime.now();
        this.desconto = "nenhum";
    }

    public void registrarSaida() {
        this.saida = LocalDateTime.now();
    }

    public long getHorasPermanencia() {
        LocalDateTime fim = saida != null ? saida : LocalDateTime.now();
        long minutos = Duration.between(entrada, fim).toMinutes();

        long horas = minutos / 60;

        if (minutos % 60 != 0) {
            horas++;
        }

        return Math.max(horas, 1);
    }

    public boolean passouDaMeiaNoite() {
        if (saida == null) {
            return false;
        }

        LocalDate dataEntrada = entrada.toLocalDate();
        LocalDate dataSaida = saida.toLocalDate();

        return dataSaida.isAfter(dataEntrada);
    }

    public String getPlaca() {
        return placa;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public double getValorBruto() {
        return valorBruto;
    }

    public void setValorBruto(double valorBruto) {
        this.valorBruto = valorBruto;
    }

    public String getDesconto() {
        return desconto;
    }

    public void setDesconto(String desconto) {
        this.desconto = desconto;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorDevido() {
        return valorDevido;
    }

    public void setValorDevido(double valorDevido) {
        this.valorDevido = valorDevido;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }
}