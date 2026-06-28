package g.l2.m;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route("")
public class MainView extends VerticalLayout {

    private final ServicoDeEstacionamento servico;
    private final CadastroCliente cadastro;
    
    // Tabelas (Grids) da interface
    private Grid<Cliente> gridClientes;
    private Grid<Movimentacao> gridMovimentacoes;
    private List<Movimentacao> historicoMovimentacoes;

    @Autowired
    public MainView(CadastroCliente cadastro, ServicoDeEstacionamento servico) {
        this.cadastro = cadastro;
        this.servico = servico;
        this.historicoMovimentacoes = new ArrayList<>();

        setAlignItems(Alignment.CENTER);
        setSpacing(true);
        setPadding(true);

        H1 titulo = new H1("Sistema de Controle de Estacionamento");
        add(titulo);

        HorizontalLayout painelPrincipal = new HorizontalLayout();
        painelPrincipal.setWidthFull();
        painelPrincipal.setSpacing(true);

        VerticalLayout colunaEsquerda = criarPainelEstacionamento();
        VerticalLayout colunaDireita = criarPainelClientes();

        colunaEsquerda.setWidth("50%");
        colunaDireita.setWidth("50%");

        painelPrincipal.add(colunaEsquerda, colunaDireita);
        add(painelPrincipal);
    }

    private VerticalLayout criarPainelEstacionamento() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "20px");

        H3 subTitulo = new H3("Movimentação do Pátio");

        TextField placaField = new TextField("Placa do Veículo");
        placaField.setPlaceholder("ABC1234");
        placaField.setClearButtonVisible(true);
        placaField.setWidthFull();

        Button btnEntrada = new Button("Registrar Entrada");
        btnEntrada.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEntrada.setWidthFull();

        Button btnSaida = new Button("Registrar Saída");
        btnSaida.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnSaida.setWidthFull();

        btnEntrada.addClickListener(event -> registrarEntrada(placaField.getValue()));
        btnSaida.addClickListener(event -> registrarSaida(placaField.getValue()));

        // --- NOVA SEÇÃO: HISTÓRICO DE ENTRADAS E SAÍDAS ---
        H3 subTituloLista = new H3("Histórico da Catraca");
        gridMovimentacoes = new Grid<>();
        gridMovimentacoes.setHeight("350px");

        gridMovimentacoes.addColumn(Movimentacao::getPlaca).setHeader("Placa").setAutoWidth(true);
        gridMovimentacoes.addColumn(Movimentacao::getOperacao).setHeader("Operação").setAutoWidth(true);
        gridMovimentacoes.addColumn(Movimentacao::getHorarioFormatado).setHeader("Horário").setAutoWidth(true);
        gridMovimentacoes.addColumn(Movimentacao::getDetalhes).setHeader("Detalhes").setAutoWidth(true);

        layout.add(subTitulo, placaField, btnEntrada, btnSaida, subTituloLista, gridMovimentacoes);
        return layout;
    }

    private VerticalLayout criarPainelClientes() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "20px");

        H3 subTitulo = new H3("Cadastro de Clientes e Veículos");

        FormLayout form = new FormLayout();
        TextField txtNome = new TextField("Nome");
        TextField txtDoc = new TextField("CPF / CNPJ");
        TextField txtCelular = new TextField("Celular");
        
        ComboBox<CategoriaCliente> cbCategoria = new ComboBox<>("Categoria");
        cbCategoria.setItems(CategoriaCliente.values());
        cbCategoria.setValue(CategoriaCliente.AVULSO);

        NumberField numSaldo = new NumberField("Saldo Inicial (Apenas Estudante)");
        numSaldo.setValue(0.0);
        numSaldo.setVisible(false);

        cbCategoria.addValueChangeListener(e -> {
            numSaldo.setVisible(CategoriaCliente.ESTUDANTE.equals(e.getValue()));
        });

        form.add(txtNome, txtDoc, txtCelular, cbCategoria, numSaldo);

        Button btnCadastrarCliente = new Button("Cadastrar Cliente");
        btnCadastrarCliente.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnCadastrarCliente.setWidthFull();

        btnCadastrarCliente.addClickListener(event -> {
            try {
                cadastrarNovoCliente(txtDoc.getValue(), txtNome.getValue(), txtCelular.getValue(), cbCategoria.getValue(), numSaldo.getValue());
                txtNome.clear();
                txtDoc.clear();
                txtCelular.clear();
                cbCategoria.setValue(CategoriaCliente.AVULSO);
            } catch (Exception e) {
                mostrarNotificacao("Erro ao cadastrar: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        H3 subTituloPlaca = new H3("Vincular Veículo a Cliente");
        FormLayout formPlaca = new FormLayout();
        TextField txtDocBusca = new TextField("CPF/CNPJ do Cliente");
        TextField txtNovaPlaca = new TextField("Placa do Veículo");
        formPlaca.add(txtDocBusca, txtNovaPlaca);

        Button btnVincularPlaca = new Button("Vincular Placa");
        btnVincularPlaca.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        btnVincularPlaca.setWidthFull();

        btnVincularPlaca.addClickListener(event -> {
            try {
                vincularPlacaAoCliente(txtDocBusca.getValue(), txtNovaPlaca.getValue());
                txtDocBusca.clear();
                txtNovaPlaca.clear();
            } catch (Exception e) {
                mostrarNotificacao("Erro ao vincular: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        H3 subTituloLista = new H3("Clientes Cadastrados");
        gridClientes = new Grid<>();
        gridClientes.setHeight("250px");
        
        gridClientes.addColumn(Cliente::getNome).setHeader("Nome");
        gridClientes.addColumn(Cliente::getCpf_cnpj).setHeader("CPF/CNPJ");
        gridClientes.addColumn(Cliente::getCelular).setHeader("Celular");
        gridClientes.addColumn(c -> {
            if (c instanceof Professor) return "Professor";
            if (c instanceof Estudante) return "Estudante";
            if (c instanceof Empresa) return "Empresa";
            return "Desconhecido";
        }).setHeader("Tipo");
        gridClientes.addColumn(c -> String.join(", ", c.getPlacas_veiculos())).setHeader("Placas");

        layout.add(subTitulo, form, btnCadastrarCliente, subTituloPlaca, formPlaca, btnVincularPlaca, subTituloLista, gridClientes);
        return layout;
    }
    
    private void adicionarClienteNaGrid(Cliente cliente) {
        List<Cliente> listaAtual = new ArrayList<>();
        gridClientes.getGenericDataView().getItems().forEach(listaAtual::add);
        listaAtual.removeIf(c -> c.getCpf_cnpj().equals(cliente.getCpf_cnpj()));
        listaAtual.add(cliente);
        gridClientes.setItems(listaAtual);
    }

    private void registrarEntrada(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            mostrarNotificacao("Informe uma placa válida.", NotificationVariant.LUMO_WARNING);
            return;
        }
        String placaFormatada = placa.trim().toUpperCase();
        try {
            LocalDateTime agora = LocalDateTime.now();
            servico.entrada(placaFormatada, agora);
            mostrarNotificacao("Entrada liberada para o veículo: " + placaFormatada, NotificationVariant.LUMO_SUCCESS);
            
            // Registra no histórico visual
            adicionarMovimentacaoGrid(new Movimentacao(placaFormatada, "ENTRADA", agora, "Catraca Liberada"));
        } catch (Exception e) {
            mostrarNotificacao(e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void registrarSaida(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            mostrarNotificacao("Informe uma placa válida.", NotificationVariant.LUMO_WARNING);
            return;
        }
        String placaFormatada = placa.trim().toUpperCase();
        try {
            LocalDateTime agora = LocalDateTime.now();
            TicketEntradaSaida ticket = servico.saida(placaFormatada, agora);
            String mensagem = String.format("Saída liberada. Categoria: %s | Valor cobrado: R$ %.2f", 
                    ticket.getCategoria(), ticket.getValorCobrado());
            mostrarNotificacao(mensagem, NotificationVariant.LUMO_SUCCESS);
            
            // Registra no histórico visual
            String detalhes = String.format("R$ %.2f (%s)", ticket.getValorCobrado(), ticket.getCategoria());
            adicionarMovimentacaoGrid(new Movimentacao(placaFormatada, "SAÍDA", agora, detalhes));

            Cliente c = cadastro.buscarPorPlaca(placaFormatada);
            if (c != null) adicionarClienteNaGrid(c);
        } catch (Exception e) {
            mostrarNotificacao(e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void adicionarMovimentacaoGrid(Movimentacao novaMovimentacao) {
        historicoMovimentacoes.add(0, novaMovimentacao); // Adiciona sempre no topo da lista
        gridMovimentacoes.setItems(historicoMovimentacoes);
    }

    private void cadastrarNovoCliente(String doc, String nome, String celular, CategoriaCliente categoria, Double saldo) {
        if (doc == null || doc.trim().isEmpty() || nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e Documento são obrigatórios.");
        }

        String docFormatado = doc.trim();
        Cliente novoCliente;
        switch (categoria) {
            case PROFESSOR:
                novoCliente = new Professor(docFormatado, nome.trim(), celular);
                break;
            case ESTUDANTE:
                double saldoInicial = (saldo != null) ? saldo : 0.0;
                novoCliente = new Estudante(docFormatado, nome.trim(), celular, saldoInicial);
                break;
            case EMPRESA:
                novoCliente = new Empresa(docFormatado, nome.trim(), celular);
                break;
            default:
                mostrarNotificacao("Clientes 'Avulsos' não precisam de pré-cadastro.", NotificationVariant.LUMO_PRIMARY);
                return;
        }

        cadastro.adicionarCliente(novoCliente);
        adicionarClienteNaGrid(novoCliente); 
        mostrarNotificacao("Cliente " + nome + " (" + categoria + ") cadastrado com sucesso!", NotificationVariant.LUMO_SUCCESS);
    }

    private void vincularPlacaAoCliente(String doc, String placa) {
        if (doc == null || doc.trim().isEmpty() || placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento e Placa são obrigatórios.");
        }

        String docFormatado = doc.trim(); 
        String placaFormatada = placa.trim().toUpperCase();

        Cliente cliente = cadastro.buscarPorCpfCnpj(docFormatado);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente com o documento '" + docFormatado + "' não foi encontrado.");
        }

        cliente.cadastraVeiculo(placaFormatada);
        cadastro.adicionarCliente(cliente);
        adicionarClienteNaGrid(cliente); 

        mostrarNotificacao("Placa " + placaFormatada + " vinculada ao cliente " + cliente.getNome(), NotificationVariant.LUMO_SUCCESS);
    }

    private void mostrarNotificacao(String mensagem, NotificationVariant tema) {
        Notification notificacao = Notification.show(mensagem, 4000, Notification.Position.TOP_CENTER);
        notificacao.addThemeVariants(tema);
    }

    // --- CLASSE AUXILIAR PARA O GRID DE HISTÓRICO ---
    public static class Movimentacao {
        private String placa;
        private String operacao;
        private LocalDateTime horario;
        private String detalhes;

        public Movimentacao(String placa, String operacao, LocalDateTime horario, String detalhes) {
            this.placa = placa;
            this.operacao = operacao;
            this.horario = horario;
            this.detalhes = detalhes;
        }

        public String getPlaca() { return placa; }
        public String getOperacao() { return operacao; }
        public String getDetalhes() { return detalhes; }
        
        public String getHorarioFormatado() { 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return horario.format(formatter); 
        }
    }
}