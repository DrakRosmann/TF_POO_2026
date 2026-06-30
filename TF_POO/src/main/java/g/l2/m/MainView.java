package g.l2.m;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
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

        // --- CABEÇALHO ---
        H1 titulo = new H1("Sistema de Controle de Estacionamento");
        
        Button btnSalvarCSV = new Button("Salvar Dados (CSV)");
        btnSalvarCSV.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnSalvarCSV.addClickListener(event -> {
            cadastro.salvarDadosNoCSV();
            mostrarNotificacao("Dados persistidos com sucesso no arquivo CSV!", NotificationVariant.LUMO_SUCCESS);
        });

        HorizontalLayout cabecalho = new HorizontalLayout(titulo, btnSalvarCSV);
        cabecalho.setVerticalComponentAlignment(Alignment.CENTER, btnSalvarCSV);
        cabecalho.setWidthFull();
        cabecalho.setJustifyContentMode(JustifyContentMode.BETWEEN);
        add(cabecalho);

        // --- SISTEMA DE ABAS ---
        Tab abaCatraca = new Tab("Catraca");
        Tab abaGestao = new Tab("Gestão de Clientes");
        Tab abaVisaoGeral = new Tab("Visão Geral");

        Tabs tabs = new Tabs(abaCatraca, abaGestao, abaVisaoGeral);
        tabs.setWidthFull();

        // --- CRIAÇÃO DOS PAINÉIS (CONTEÚDO DAS ABAS) ---
        VerticalLayout painelCatraca = criarPainelCatraca();
        VerticalLayout painelGestao = criarPainelGestao();
        VerticalLayout painelVisaoGeral = criarPainelVisaoGeral();

        // Esconde os painéis que não são a aba inicial (Catraca)
        painelGestao.setVisible(false);
        painelVisaoGeral.setVisible(false);

        // Lógica para alternar entre as abas ao clicar
        tabs.addSelectedChangeListener(event -> {
            painelCatraca.setVisible(abaCatraca.equals(event.getSelectedTab()));
            painelGestao.setVisible(abaGestao.equals(event.getSelectedTab()));
            painelVisaoGeral.setVisible(abaVisaoGeral.equals(event.getSelectedTab()));
        });

        add(tabs, painelCatraca, painelGestao, painelVisaoGeral);

        atualizarGridCompleta();
    }

    // ==========================================
    // ABA 1: CATRACA (Entrada e Saída)
    // ==========================================
    private VerticalLayout criarPainelCatraca() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "20px");

        H2 subTitulo = new H2("Controle de Acesso");

        TextField placaField = new TextField("Placa do Veículo");
        placaField.setPlaceholder("ABC1234");
        placaField.setClearButtonVisible(true);

        Button btnEntrada = new Button("Registrar Entrada");
        btnEntrada.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnSaida = new Button("Registrar Saída");
        btnSaida.addThemeVariants(ButtonVariant.LUMO_ERROR);

        btnEntrada.addClickListener(event -> registrarEntrada(placaField.getValue()));
        btnSaida.addClickListener(event -> registrarSaida(placaField.getValue()));

        HorizontalLayout painelBotoes = new HorizontalLayout(placaField, btnEntrada, btnSaida);
        painelBotoes.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        H3 subTituloLista = new H3("Histórico de Movimentações");
        gridMovimentacoes = new Grid<>();
        gridMovimentacoes.setHeight("400px");

        gridMovimentacoes.addColumn(Movimentacao::getPlaca).setHeader("Placa").setAutoWidth(true);
        gridMovimentacoes.addColumn(Movimentacao::getOperacao).setHeader("Operação").setAutoWidth(true);
        gridMovimentacoes.addColumn(Movimentacao::getHorarioFormatado).setHeader("Horário").setAutoWidth(true);
        gridMovimentacoes.addColumn(Movimentacao::getDetalhes).setHeader("Detalhes").setAutoWidth(true);

        layout.add(subTitulo, painelBotoes, subTituloLista, gridMovimentacoes);
        return layout;
    }

    // ==========================================
    // ABA 2: GESTÃO DE CLIENTES
    // ==========================================
    private VerticalLayout criarPainelGestao() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "20px");

        // --- CADASTRO ---
        H3 tituloCadastro = new H3("1. Cadastrar Novo Cliente");
        FormLayout formCadastro = new FormLayout();
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

        formCadastro.add(txtNome, txtDoc, txtCelular, cbCategoria, numSaldo);

        Button btnCadastrar = new Button("Salvar Cliente");
        btnCadastrar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnCadastrar.addClickListener(event -> {
            try {
                cadastrarNovoCliente(txtDoc.getValue(), txtNome.getValue(), txtCelular.getValue(), cbCategoria.getValue(), numSaldo.getValue());
                txtNome.clear(); txtDoc.clear(); txtCelular.clear(); cbCategoria.setValue(CategoriaCliente.AVULSO);
            } catch (Exception e) {
                mostrarNotificacao("Erro ao cadastrar: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        // --- VINCULAR PLACA ---
        H3 tituloPlaca = new H3("2. Vincular Veículo");
        FormLayout formPlaca = new FormLayout();
        TextField txtDocBusca = new TextField("CPF/CNPJ do Cliente");
        TextField txtNovaPlaca = new TextField("Placa do Veículo");
        formPlaca.add(txtDocBusca, txtNovaPlaca);

        Button btnVincular = new Button("Adicionar Placa");
        btnVincular.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        btnVincular.addClickListener(event -> {
            try {
                vincularPlacaAoCliente(txtDocBusca.getValue(), txtNovaPlaca.getValue());
                txtDocBusca.clear(); txtNovaPlaca.clear();
            } catch (Exception e) {
                mostrarNotificacao("Erro ao vincular: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        // --- RECARGA ---
        H3 tituloRecarga = new H3("3. Recarga de Estudante");
        FormLayout formRecarga = new FormLayout();
        TextField txtDocRecarga = new TextField("CPF do Estudante");
        NumberField numValorRecarga = new NumberField("Valor da Recarga (R$)");
        formRecarga.add(txtDocRecarga, numValorRecarga);

        Button btnRecarregar = new Button("Adicionar Saldo");
        btnRecarregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnRecarregar.addClickListener(event -> {
            try {
                realizarRecarga(txtDocRecarga.getValue(), numValorRecarga.getValue());
                txtDocRecarga.clear(); numValorRecarga.clear();
            } catch (Exception e) {
                mostrarNotificacao("Erro na recarga: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        layout.add(tituloCadastro, formCadastro, btnCadastrar, 
                   new com.vaadin.flow.component.html.Hr(), 
                   tituloPlaca, formPlaca, btnVincular, 
                   new com.vaadin.flow.component.html.Hr(), 
                   tituloRecarga, formRecarga, btnRecarregar);
        return layout;
    }

    // ==========================================
    // ABA 3: VISÃO GERAL (Tabela de Clientes)
    // ==========================================
    private VerticalLayout criarPainelVisaoGeral() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "20px");

        H2 subTitulo = new H2("Banco de Dados - Clientes");

        gridClientes = new Grid<>();
        gridClientes.setHeight("500px");
        
        gridClientes.addColumn(Cliente::getNome).setHeader("Nome").setAutoWidth(true);
        gridClientes.addColumn(Cliente::getCpf_cnpj).setHeader("CPF/CNPJ").setAutoWidth(true);
        gridClientes.addColumn(c -> {
            if (c instanceof Professor) return "Professor";
            if (c instanceof Estudante) return "Estudante";
            if (c instanceof Empresa) return "Empresa";
            return "Desconhecido";
        }).setHeader("Tipo").setAutoWidth(true);
        
        gridClientes.addColumn(c -> {
            if (c instanceof Estudante) return String.format("Saldo: R$ %.2f", ((Estudante) c).getCreditos());
            else if (c instanceof Empresa) return String.format("Débito: R$ %.2f", (double) ((Empresa) c).getDebitos());
            return "Isento";
        }).setHeader("Financeiro").setAutoWidth(true);

        gridClientes.addColumn(c -> String.join(", ", c.getPlacas_veiculos())).setHeader("Placas");

        layout.add(subTitulo, gridClientes);
        return layout;
    }

    // ==========================================
    // LÓGICAS DE NEGÓCIO E COMPORTAMENTOS
    // ==========================================
    private void atualizarGridCompleta() {
        if (cadastro != null && gridClientes != null) {
            gridClientes.setItems(cadastro.listarTodos());
        }
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
            
            String detalhes = String.format("R$ %.2f (%s)", ticket.getValorCobrado(), ticket.getCategoria());
            adicionarMovimentacaoGrid(new Movimentacao(placaFormatada, "SAÍDA", agora, detalhes));

            atualizarGridCompleta();
        } catch (Exception e) {
            mostrarNotificacao(e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void realizarRecarga(String doc, Double valor) {
        if (doc == null || doc.trim().isEmpty() || valor == null || valor <= 0) {
            throw new IllegalArgumentException("Informe um CPF válido e um valor de recarga maior que zero.");
        }
        String docFormatado = doc.trim();
        Cliente cliente = cadastro.buscarPorCpfCnpj(docFormatado);
        if (cliente == null) throw new IllegalArgumentException("Nenhum cliente encontrado com este documento.");
        if (!(cliente instanceof Estudante)) throw new IllegalArgumentException("Recargas são permitidas apenas para contas de Estudantes.");

        Estudante estudante = (Estudante) cliente;
        estudante.setCreditos(estudante.getCreditos() + valor);
        
        atualizarGridCompleta();
        mostrarNotificacao(String.format("Recarga de R$ %.2f realizada com sucesso para %s!", valor, estudante.getNome()), NotificationVariant.LUMO_SUCCESS);
    }

    private void adicionarMovimentacaoGrid(Movimentacao novaMovimentacao) {
        historicoMovimentacoes.add(0, novaMovimentacao);
        gridMovimentacoes.setItems(historicoMovimentacoes);
    }

    private void cadastrarNovoCliente(String doc, String nome, String celular, CategoriaCliente categoria, Double saldo) {
        if (doc == null || doc.trim().isEmpty() || nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e Documento são obrigatórios.");
        }
        String docFormatado = doc.trim();
        Cliente novoCliente;
        switch (categoria) {
            case PROFESSOR: novoCliente = new Professor(docFormatado, nome.trim(), celular); break;
            case ESTUDANTE: 
                double saldoInicial = (saldo != null) ? saldo : 0.0;
                novoCliente = new Estudante(docFormatado, nome.trim(), celular, saldoInicial); break;
            case EMPRESA: novoCliente = new Empresa(docFormatado, nome.trim(), celular); break;
            default: mostrarNotificacao("Clientes 'Avulsos' não precisam de pré-cadastro.", NotificationVariant.LUMO_PRIMARY); return;
        }

        cadastro.adicionarCliente(novoCliente);
        atualizarGridCompleta();
        mostrarNotificacao("Cliente " + nome + " (" + categoria + ") cadastrado com sucesso!", NotificationVariant.LUMO_SUCCESS);
    }

    private void vincularPlacaAoCliente(String doc, String placa) {
        if (doc == null || doc.trim().isEmpty() || placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento e Placa são obrigatórios.");
        }
        String docFormatado = doc.trim(); 
        String placaFormatada = placa.trim().toUpperCase();

        Cliente cliente = cadastro.buscarPorCpfCnpj(docFormatado);
        if (cliente == null) throw new IllegalArgumentException("Cliente com o documento '" + docFormatado + "' não foi encontrado.");

        cliente.cadastraVeiculo(placaFormatada);
        cadastro.adicionarCliente(cliente);
        atualizarGridCompleta();

        mostrarNotificacao("Placa " + placaFormatada + " vinculada ao cliente " + cliente.getNome(), NotificationVariant.LUMO_SUCCESS);
    }

    private void mostrarNotificacao(String mensagem, NotificationVariant tema) {
        Notification notificacao = Notification.show(mensagem, 4000, Notification.Position.TOP_CENTER);
        notificacao.addThemeVariants(tema);
    }

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
            return horario.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")); 
        }
    }
}