package g.l2.m.modeloPrincipal1;

import java.util.Set;

public abstract class ClientePre {
    private String nome;
    private String identificador;
    protected Set<String> placas;
    private double valor;
    public ClientePre(String nome, String identificador){
        this.nome = nome;
        this.identificador = identificador;
    }

    public abstract boolean adicionarPlaca(String placa);
}
