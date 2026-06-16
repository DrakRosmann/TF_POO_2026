package tf;

import java.util.Set;

public abstract class ClientePC extends Cliente{
    protected Set<String> PlacasVeiculos;


    public abstract boolean AdicionarVeiculo(String placa);
}
