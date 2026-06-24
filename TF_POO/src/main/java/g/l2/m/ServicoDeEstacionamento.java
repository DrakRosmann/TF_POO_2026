import java.time.LocalDateTime;
import java.util.*;

public class ServicoDeEstacionamento {
    private static final int MAX_VAGAS = 500;
    private static final double VALOR_DIARIA = 35.0;
    private static final double VALOR_HORA = 5.0;
    private Map<String, TicketEntradaSaida> ticketsAtivos;
    private List<TicketEntradaSaida> ticketEncerrados;
    private Set<String> bloqueados;

    public ServicoDeEstacionamento(){
        ticketsAtivos = new HashMap<>();
        ticketEncerrados = new ArrayList<>();
        bloqueados = new HashSet<>();
    }
    public void entrada(String placa, LocalDateTime horarioEntrada){

    }

    public TicketEntradaSaida calculaCustoSaida(String placa, LocalDateTime horarioSaida){

    }

    public TicketEntradaSaida saida(String placa, LocalDateTime horarioSaida){

    }
}
