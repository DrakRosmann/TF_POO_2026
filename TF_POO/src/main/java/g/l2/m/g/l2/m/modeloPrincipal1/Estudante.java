package g.l2.m.modeloPrincipal1;

public class Estudante extends ClientePre {
    private double valor;
    public Estudante(String nome, String cpf){
        super(nome, cpf);
    }

    @Override
    public boolean adicionarPlaca(String placa) {
        if (placas.isEmpty()){
            placas.add(placa);
        }
        return false;
    }
}
