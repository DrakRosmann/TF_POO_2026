import g.l2.m.modelo.*;

import java.time.LocalDateTime;
import java.util.*;

public class EstacionamentoService {

    private static final int TOTAL_VAGAS = 9000;

    private static final double VALOR_HORA_AVULSO = 10.0;
    private static final double VALOR_DIARIA_AVULSO = 50.0;
    private static final double VALOR_INGRESSO_ESTUDANTE = 8.0;
    private static final double VALOR_DIARIA_EMPRESA = 30.0;
    private static final double MULTA_EMPRESA = 100.0;

    private Map<String, Cliente> clientesPorDocumento;
    private Map<String, Cliente> clientesPorPlaca;
    private Map<String, RegistroEstacionamento> estacionados;
    private List<RegistroEstacionamento> historico;
    private Set<String> placasBloqueadas;

    public EstacionamentoService() {
        clientesPorDocumento = new HashMap<>();
        clientesPorPlaca = new HashMap<>();
        estacionados = new HashMap<>();
        historico = new ArrayList<>();
        placasBloqueadas = new HashSet<>();
    }

    public boolean cadastrarCliente(Cliente cliente) {
        if (clientesPorDocumento.containsKey(cliente.getDocumento())) {
            return false;
        }

        clientesPorDocumento.put(cliente.getDocumento(), cliente);
        return true;
    }

    public boolean adicionarPlacaCliente(String documento, String placa) {
        Cliente cliente = clientesPorDocumento.get(documento);

        if (cliente == null) {
            return false;
        }

        if (clientesPorPlaca.containsKey(placa)) {
            return false;
        }

        boolean adicionou = cliente.adicionarPlaca(placa);

        if (adicionou) {
            clientesPorPlaca.put(placa, cliente);
        }

        return adicionou;
    }

    public boolean removerPlacaCliente(String documento, String placa) {
        Cliente cliente = clientesPorDocumento.get(documento);

        if (cliente == null) {
            return false;
        }

        boolean removeu = cliente.removerPlaca(placa);

        if (removeu) {
            clientesPorPlaca.remove(placa);
        }

        return removeu;
    }

    public boolean autorizarEntrada(String placa) {
        if (estacionados.size() >= TOTAL_VAGAS) {
            return false;
        }

        if (placasBloqueadas.contains(placa)) {
            return false;
        }

        if (estacionados.containsKey(placa)) {
            return false;
        }

        Cliente cliente = clientesPorPlaca.get(placa);

        if (cliente == null) {
            RegistroEstacionamento registro =
                    new RegistroEstacionamento(placa, TipoCliente.AVULSO, null);

            estacionados.put(placa, registro);
            return true;
        }

        if (cliente instanceof Estudante estudante) {
            if (!estudante.podeEntrar()) {
                return false;
            }
        }

        if (cliente instanceof Empresa empresa) {
            if (empresa.isInadimplente()) {
                return false;
            }
        }

        if (cliente instanceof Professor professor) {
            if (professorJaTemVeiculoEstacionado(professor)) {
                return false;
            }
        }

        RegistroEstacionamento registro =
                new RegistroEstacionamento(placa, cliente.getTipoCliente(), cliente.getDocumento());

        estacionados.put(placa, registro);
        return true;
    }

    public ResultadoSaida processarSaida(String placa, double valorPago, boolean recusouPagar) {
        RegistroEstacionamento registro = estacionados.get(placa);

        if (registro == null) {
            return new ResultadoSaida(placa, 0, "Veículo não está estacionado.");
        }

        registro.registrarSaida();

        double valor = calcularValor(registro);
        double desconto = calcularDesconto(registro, valor);

        double valorDevido = valor - desconto;

        registro.setValorBruto(valor);
        registro.setValorDesconto(desconto);
        registro.setValorDevido(valorDevido);
        registro.setValorPago(valorPago);

        if (desconto > 0) {
            registro.setDesconto("ClienteFrequente");
        } else {
            registro.setDesconto("nenhum");
        }

        Cliente cliente = null;

        if (registro.getDocumentoCliente() != null) {
            cliente = clientesPorDocumento.get(registro.getDocumentoCliente());
        }

        if (cliente instanceof Estudante estudante) {
            estudante.debitar(valorDevido);
        }

        if (cliente instanceof Empresa empresa) {
            empresa.adicionarDebito(valorDevido);
        }

        if (registro.getTipoCliente() == TipoCliente.AVULSO && recusouPagar) {
            placasBloqueadas.add(placa);
        }

        estacionados.remove(placa);
        historico.add(registro);

        return new ResultadoSaida(placa, valorDevido, "Saída liberada.");
    }

    private double calcularValor(RegistroEstacionamento registro) {
        return switch (registro.getTipoCliente()) {
            case AVULSO -> calcularAvulso(registro);
            case PROFESSOR -> 0.0;
            case ESTUDANTE -> calcularEstudante(registro);
            case EMPRESA -> calcularEmpresa(registro);
        };
    }

    private double calcularAvulso(RegistroEstacionamento registro) {
        long horas = registro.getHorasPermanencia();

        if (registro.passouDaMeiaNoite()) {
            return VALOR_DIARIA_AVULSO * 2;
        }

        if (horas > 6) {
            return VALOR_DIARIA_AVULSO;
        }

        return horas * VALOR_HORA_AVULSO;
    }

    private double calcularEstudante(RegistroEstacionamento registro) {
        if (registro.passouDaMeiaNoite()) {
            return VALOR_INGRESSO_ESTUDANTE * 2;
        }

        return VALOR_INGRESSO_ESTUDANTE;
    }

    private double calcularEmpresa(RegistroEstacionamento registro) {
        double valor = VALOR_DIARIA_EMPRESA;

        if (registro.passouDaMeiaNoite()) {
            valor += MULTA_EMPRESA;
        }

        return valor;
    }

    private double calcularDesconto(RegistroEstacionamento registro, double valor) {
        if (registro.getTipoCliente() != TipoCliente.AVULSO) {
            return 0.0;
        }

        LocalDateTime tresDiasAtras = registro.getEntrada().minusDays(3);

        for (RegistroEstacionamento r : historico) {
            if (r.getPlaca().equals(registro.getPlaca())
                    && r.getEntrada().isAfter(tresDiasAtras)
                    && r.getEntrada().isBefore(registro.getEntrada())) {
                return valor * 0.10;
            }
        }

        return 0.0;
    }

    private boolean professorJaTemVeiculoEstacionado(Professor professor) {
        for (RegistroEstacionamento registro : estacionados.values()) {
            if (professor.getDocumento().equals(registro.getDocumentoCliente())) {
                return true;
            }
        }

        return false;
    }

    public List<RegistroEstacionamento> getHistorico() {
        return historico;
    }

    public Collection<RegistroEstacionamento> getEstacionados() {
        return estacionados.values();
    }

    public Set<String> getPlacasBloqueadas() {
        return placasBloqueadas;
    }

    public Cliente buscarClientePorDocumento(String documento) {
        return clientesPorDocumento.get(documento);
    }
}