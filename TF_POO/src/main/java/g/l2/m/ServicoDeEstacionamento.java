package g.l2.m;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ServicoDeEstacionamento {
    private static final int MAX_VAGAS = 500;
    private static final double VALOR_DIARIA = 35.0;
    private static final double VALOR_HORA = 5.0;
    
    private Map<String, TicketEntradaSaida> ticketsAtivos;
    private List<TicketEntradaSaida> ticketsEncerrados;
    private Set<String> bloqueados;
    private CadastroCliente cadastroClientes;

    public ServicoDeEstacionamento(CadastroCliente cadastroClientes) {
        this.ticketsAtivos = new HashMap<>();
        this.ticketsEncerrados = new ArrayList<>();
        this.bloqueados = new HashSet<>();
        this.cadastroClientes = cadastroClientes;
    }

    public void entrada(String placa, LocalDateTime horarioEntrada) {
        if (ticketsAtivos.size() >= MAX_VAGAS) {
            throw new IllegalStateException("Estacionamento lotado.");
        }
        if (bloqueados.contains(placa)) {
            throw new IllegalStateException("Veículo bloqueado.");
        }
        if (ticketsAtivos.containsKey(placa)) {
            throw new IllegalArgumentException("Veículo já está no estacionamento.");
        }

        Cliente cliente = cadastroClientes.buscarPorPlaca(placa);
        CategoriaCliente categoria = CategoriaCliente.AVULSO;

        if (cliente != null) {
            if (cliente instanceof Professor) {
                boolean jaPossuiVeiculoEstacionado = false;
                for (TicketEntradaSaida ticketAtivo : ticketsAtivos.values()) {
                    if (cliente.getPlacas_veiculos().contains(ticketAtivo.getPlacaVeiculo())) {
                        jaPossuiVeiculoEstacionado = true;
                        break;
                    }
                }

                if (jaPossuiVeiculoEstacionado) {
                    categoria = CategoriaCliente.AVULSO;
                } else {
                    categoria = CategoriaCliente.PROFESSOR;
                }
                
            } else if (cliente instanceof Estudante) {
                if (((Estudante) cliente).getCreditos() < 0) {
                    throw new IllegalStateException("Estudante com saldo negativo. Entrada bloqueada.");
                }
                categoria = CategoriaCliente.ESTUDANTE;
            } else if (cliente instanceof Empresa) {
                categoria = CategoriaCliente.EMPRESA;
            }
        }

        TicketEntradaSaida ticket = new TicketEntradaSaida(placa, categoria, horarioEntrada);
        ticketsAtivos.put(placa, ticket);
    }

    public TicketEntradaSaida saida(String placa, LocalDateTime horarioSaida) {
        TicketEntradaSaida ticket = ticketsAtivos.get(placa);
        if (ticket == null) {
            throw new IllegalArgumentException("Veículo não encontrado no pátio.");
        }

        if (ticket.getCategoria() == CategoriaCliente.AVULSO) {
            calcularCustoAvulso(ticket, horarioSaida);
        } else {
            Cliente cliente = cadastroClientes.buscarPorPlaca(placa);
            if (cliente != null) {
                ticket = cliente.calculaCusto(ticket, horarioSaida);
            } else {
                calcularCustoAvulso(ticket, horarioSaida);
            }
        }

        ticketsAtivos.remove(placa);
        ticketsEncerrados.add(ticket);
        return ticket;
    }

    private void calcularCustoAvulso(TicketEntradaSaida ticket, LocalDateTime horaSaida) {
        ticket.setHoraSaida(horaSaida);
        long horas = ChronoUnit.HOURS.between(ticket.getHoraEntrada(), horaSaida);
        long minutos = ChronoUnit.MINUTES.between(ticket.getHoraEntrada(), horaSaida) % 60;

        if (minutos > 0) horas++;

        long dias = ChronoUnit.DAYS.between(ticket.getHoraEntrada().toLocalDate(), horaSaida.toLocalDate());
        double custoTotal = 0.0;

        if (dias > 0 || horas > 6) {
            custoTotal = VALOR_DIARIA + (dias * VALOR_DIARIA);
        } else {
            custoTotal = horas * VALOR_HORA;
        }

        ticket.setValorCalculado(custoTotal);

        double desconto = calcularDescontoFrequente(ticket.getPlacaVeiculo(), ticket.getHoraEntrada()) ? custoTotal * 0.10 : 0.0;

        ticket.setDesconto(desconto);
        ticket.setValorCobrado(custoTotal - desconto);
    }

    private boolean calcularDescontoFrequente(String placa, LocalDateTime dataAtual) {
        for (TicketEntradaSaida t : ticketsEncerrados) {
            if (t.getPlacaVeiculo().equals(placa)) {
                long diasDiff = ChronoUnit.DAYS.between(t.getHoraSaida().toLocalDate(), dataAtual.toLocalDate());
                if (diasDiff <= 3 && diasDiff >= 0) return true;
            }
        }
        return false;
    }

    public void registrarCaloteAvulso(String placa) {
        bloqueados.add(placa);
    }
}