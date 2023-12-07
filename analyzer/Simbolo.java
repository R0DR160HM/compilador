package analyzer;

public class Simbolo {
    
    private String id;
    private String tipo;
    private String valor;

    public Simbolo(String id, String valor) {
        this.id = id;
        setValor(valor);
        setTipo();
    }

    public Simbolo(String id) {
        this.id = id;
        setTipo();
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public String getValorMaquina() {
        if (getTipo() == "bool") {
            if (this.valor.equals("true")) {
                return "1";
            } else if (this.valor.equals("false")) {
                return "0";
            } else {
                return null;
            }
        }
        return getValor();
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getId() {
        return this.id;
    }

    private String setTipo() {
        if (id.startsWith("_i")) {
            this.tipo = "int64";
        } else if (id.startsWith("_f")) {
            this.tipo = "float64";
        } else if (id.startsWith("_s")) {
            this.tipo =  "string";
        } else if (id.startsWith("_b")) {
            this.tipo =  "bool";
        }
        throw new IllegalArgumentException("Identificador " + id + " não possui um tipo válido");
    }

}
