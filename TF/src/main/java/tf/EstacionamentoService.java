package tf;

import java.util.*;

public class EstacionamentoService {

    private Map<String, ClientePre> clientesPorDocumento;
    private Map<String, ClientePre> donoPorPlaca;
    private Map<String, RegistroEstacionamento> veiculosEstacionados;
    private List<RegistroEstacionamento> registrosFinalizados;
    private Set<String> placasBloqueadas;

    public EstacionamentoService() {
        clientesPorDocumento = new HashMap<>();
        donoPorPlaca = new HashMap<>();
        veiculosEstacionados = new HashMap<>();
        registrosFinalizados = new ArrayList<>();
        placasBloqueadas = new HashSet<>();
    }

    public boolean cadastrarCliente(String documento, ClientePre cliente) {
        if (clientesPorDocumento.containsKey(documento)) {
            return false;
        }

        clientesPorDocumento.put(documento, cliente);
        return true;
    }

    public boolean cadastrarPlaca(String documento, String placa) {
        ClientePre cliente = clientesPorDocumento.get(documento);

        if (cliente == null) {
            return false;
        }

        placa = placa.toUpperCase().trim();

        if (donoPorPlaca.containsKey(placa)) {
            return false;
        }

        boolean adicionou = cliente.adicionarPlaca(placa);

        if (adicionou) {
            donoPorPlaca.put(placa, cliente);
        }

        return adicionou;
    }

    public boolean autorizarEntrada(String placa) {
        placa = placa.toUpperCase().trim();

        if (placasBloqueadas.contains(placa)) {
            return false;
        }

        if (veiculosEstacionados.containsKey(placa)) {
            return false;
        }

        ClientePre cliente = donoPorPlaca.get(placa);
        TipoCliente tipo;
        String documento = null;

        if (cliente == null) {
            tipo = TipoCliente.AVULSO;
        } else {
            documento = buscarDocumentoCliente(cliente);

            if (cliente instanceof Professor) {
                tipo = TipoCliente.PROFESSOR;
            } else if (cliente instanceof Estudante) {
                tipo = TipoCliente.ESTUDANTE;
            } else {
                tipo = TipoCliente.EMPRESA;
            }
        }

        RegistroEstacionamento registro =
                new RegistroEstacionamento(placa, tipo, documento);

        veiculosEstacionados.put(placa, registro);
        return true;
    }

    public RegistroEstacionamento registrarSaida(String placa, double valorPago) {
        placa = placa.toUpperCase().trim();

        RegistroEstacionamento registro = veiculosEstacionados.remove(placa);

        if (registro == null) {
            return null;
        }

        registro.registrarSaida();

        double valorBruto = calcularValor(registro);
        registro.setValorBruto(valorBruto);
        registro.setValorDesconto(0);
        registro.setValorDevido(valorBruto);
        registro.setValorPago(valorPago);

        if (valorPago < valorBruto && registro.getTipoCliente() == TipoCliente.AVULSO) {
            placasBloqueadas.add(placa);
        }

        registrosFinalizados.add(registro);
        return registro;
    }

    private double calcularValor(RegistroEstacionamento registro) {
        long horas = registro.getHorasPermanencia();

        if (registro.getTipoCliente() == TipoCliente.PROFESSOR) {
            return 0;
        }

        if (registro.getTipoCliente() == TipoCliente.ESTUDANTE) {
            if (registro.passouDaMeiaNoite()) {
                return 24.0;
            }
            return 12.0;
        }

        if (registro.getTipoCliente() == TipoCliente.EMPRESA) {
            if (registro.passouDaMeiaNoite()) {
                return 80.0;
            }
            return 30.0;
        }

        if (horas <= 6) {
            return horas * 12.0;
        }

        return 30.0;
    }

    private String buscarDocumentoCliente(ClientePre cliente) {
        for (String documento : clientesPorDocumento.keySet()) {
            if (clientesPorDocumento.get(documento) == cliente) {
                return documento;
            }
        }
        return null;
    }

    public Set<String> getPlacasBloqueadas() {
        return placasBloqueadas;
    }

    public List<RegistroEstacionamento> getRegistrosFinalizados() {
        return registrosFinalizados;
    }

    public Map<String, RegistroEstacionamento> getVeiculosEstacionados() {
        return veiculosEstacionados;
    }
}