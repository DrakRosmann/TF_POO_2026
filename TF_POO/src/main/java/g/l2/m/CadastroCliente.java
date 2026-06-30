package g.l2.m;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class CadastroCliente {
    private Map<String, Cliente> clientes;
    private Map<String, Cliente> placasCliente;
    private static final String CSV_FILE = "clientes.csv";

    public CadastroCliente() {
        this.clientes = new HashMap<>();
        this.placasCliente = new HashMap<>();
    }


    @PostConstruct
    public void carregarDadosDoCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            System.out.println("Arquivo " + CSV_FILE + " não encontrado. Um novo será criado ao salvar.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String linha;
            
            br.readLine(); 

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                
                String[] colunas = linha.split(";");
                if (colunas.length < 5) continue;

                String tipo = colunas[0];
                String doc = colunas[1];
                String nome = colunas[2];
                String celular = colunas[3];
                String infoAdicional = colunas[4]; 

                Cliente cliente = null;
                if (tipo.equalsIgnoreCase("PROFESSOR")) {
                    cliente = new Professor(doc, nome, celular);
                } else if (tipo.equalsIgnoreCase("ESTUDANTE")) {
                    double saldo = Double.parseDouble(infoAdicional);
                    cliente = new Estudante(doc, nome, celular, saldo);
                } else if (tipo.equalsIgnoreCase("EMPRESA")) {
                    cliente = new Empresa(doc, nome, celular);
            
                }

                if (cliente != null) {
                    
                    if (colunas.length > 5 && !colunas[5].trim().isEmpty()) {
                        String[] placas = colunas[5].split(",");
                        for (String placa : placas) {
                            cliente.cadastraVeiculo(placa.trim().toUpperCase());
                        }
                    }
                    
                    adicionarCliente(cliente);
                }
            }
            System.out.println("Dados de clientes carregados com sucesso a partir do CSV!");
        } catch (Exception e) {
            System.err.println("Erro ao carregar o arquivo CSV de clientes: " + e.getMessage());
        }
    }


    public void salvarDadosNoCSV() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(CSV_FILE), StandardCharsets.UTF_8))) {
            
            pw.println("TIPO;CPF_CNPJ;NOME;CELULAR;INFO_ADICIONAL;PLACAS");

            for (Cliente c : clientes.values()) {
                String tipo = "";
                String infoAdicional = "0.0";

                if (c instanceof Professor) {
                    tipo = "PROFESSOR";
                } else if (c instanceof Estudante) {
                    tipo = "ESTUDANTE";
                    infoAdicional = String.valueOf(((Estudante) c).getCreditos());
                } else if (c instanceof Empresa) {
                    tipo = "EMPRESA";
                    infoAdicional = String.valueOf(((Empresa) c).getDebitos());
                }

                String placas = String.join(",", c.getPlacas_veiculos());

                pw.printf("%s;%s;%s;%s;%s;%s%n", 
                        tipo, c.getCpf_cnpj(), c.getNome(), c.getCelular(), infoAdicional, placas);
            }
            System.out.println("Dados salvos em " + CSV_FILE + " com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao salvar o arquivo CSV de clientes: " + e.getMessage());
        }
    }

    public void adicionarCliente(Cliente cliente) {
        
        for (String placa : cliente.getPlacas_veiculos()) {
            Cliente donoAtual = placasCliente.get(placa);
            if (donoAtual != null && !donoAtual.getCpf_cnpj().equals(cliente.getCpf_cnpj())) {
                throw new IllegalArgumentException("A placa " + placa + " já está cadastrada para o cliente " + donoAtual.getNome());
            }
        }
        
        clientes.put(cliente.getCpf_cnpj(), cliente);
        for (String placa : cliente.getPlacas_veiculos()) {
            placasCliente.put(placa, cliente);
        }
    }

    public Cliente buscarPorCpfCnpj(String documento) {
        return clientes.get(documento);
    }

    public Cliente buscarPorPlaca(String placa) {
        return placasCliente.get(placa);
    }

    public java.util.Collection<Cliente> listarTodos() {
        return clientes.values();
    }
}