package tf;

import java.util.Objects;

public class Veiculo {
    private String placa;

    public Veiculo(String placa) {
        this.placa = placa.toUpperCase().trim();
    }

    public String getPlaca() {
        return placa;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Veiculo)) return false;

        Veiculo outro = (Veiculo) obj;
        return placa.equals(outro.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }

    @Override
    public String toString() {
        return placa;
    }
}