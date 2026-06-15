package g.l2.m.modeloPrincipal1;

public class Empresa extends ClientePre {
    private double valorAcumulado;
    private boolean inadimplente;
    public Empresa(String nome, String CNPJ){
        super(nome, CNPJ);
        valorAcumulado = 0;
        inadimplente = false;
    }
}
