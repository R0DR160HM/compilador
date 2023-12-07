package analyzer;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Semantico implements Constants {

    private String operadorRelacional = "";
    private String codigoObjeto = "";
    private Stack<String> pilhaTipos = new Stack<>();
    private Stack<String> pilhaRotulos = new Stack<>();
    private List<String> listaIdentificadores = new LinkedList<String>();

    private Hashtable<String, Simbolo> tabelaSimbolos = new Hashtable<>();

    public void executeAction(int action, Token token) throws SemanticError {
        switch (action) {
            case 100:
                acao100();
                break;
            case 101:
                acao101();
                break;
            case 102:
                acao102();
                break;
            case 103:
                acao103();
                break;
            case 104:
                acao104();
                break;
            case 105:
                acao105();
                break;
            case 106:
                acao106();
                break;
            case 107:
                acao107();
                break;
            case 108:
                acao108(token.getLexeme());
                break;
            case 109:
                acao109();
                break;
            case 110:
                acao110();
                break;
            case 111:
                acao111();
                break;
            case 112:
                acao112();
                break;
            case 113:
                acao113();
                break;
            case 114:
                acao114(token.getLexeme());
                break;
            case 115:
                acao115(token.getLexeme());
                break;
            case 116:
                acao116(token.getLexeme());
                break;
            case 117:
                acao117();
                break;
            case 118:
                acao118(token);
                break;
            case 119:
                acao119();
                break;
            case 120:
                acao120();
                break;
            case 121:
                acao121();
                break;
            case 122:
                acao122(token);
                break;
            case 123:
                acao123();
                break;
            case 124:
                acao124(token);
                break;
            case 125:
                acao125(token.getLexeme());
                break;
            case 126:
                acao126(token);
                break;
            case 127:
                acao127(token);
                break;
            case 128:
                acao128(token);
                break;
            case 129:
                acao129(token);
                break;
            case 130:
                acao130(token);
                break;
            case 131:
                acao131(token);
                break;
            default:
                System.err.println("Operação " + action + " não identificada!");
        }
        
    }

    public String getCodigoObjeto() {
        return codigoObjeto;
    }

    // Cabeçalho
    private void acao100() {
        this.append(".assembly extern mscorlib {}");
        this.append(".assembly _projeto{}");
        this.append(".module _projeto.exe");
        this.append(".class public _projeto{");
        this.append("   .method static public void _principal(){");
        this.append("       .entrypoint");
    }

    // Rodapé
    private void acao101() {
        this.append("ret");
        this.append("}");
        this.append("}");
    }

    // cmd_output (saída)
    private void acao102() {
        String tipo = this.pilhaTipos.pop();
        if (tipo.equals("int64")) {
            this.append("conv.i8");
        }
        this.append("call void [mscorlib]System.Console::WriteLine(" + tipo + ")");
    }

    // & (and)
    private void acao103() {
        this.logica();
        this.append("and");
    }

    // | (or)
    private void acao104() {
        this.logica();
        this.append("or");
    }

    // true
    private void acao105() {
        this.pilhaTipos.push("bool");
        this.append("ldc.i4.1");
    }

    // false
    private void acao106() {
        this.pilhaTipos.push("bool");
        this.append("ldc.i4.0");
    }

    // not
    private void acao107() {
        this.append("ldc.i4.1");
        this.append("xor");
    }

    // Salva operador relacional
    private void acao108(String token) {
        this.operadorRelacional = token;
    }
    
    // Realizar comparação
    private void acao109() {
        pilhaTipos.pop();
        pilhaTipos.pop();
        this.pilhaTipos.push("bool");
        switch (this.operadorRelacional) {
            case "==":
                this.append("ceq");
                break;
            case ">":
                this.append("cgt");
                break;
            case "<":
                this.append("clt");
                break;
            case "!=":
                this.append("ceq");
                this.append("ldc.i4.1");
                this.append("xor");
                break;
        }
    }

    // + (add)
    private void acao110() {
        this.aritmetica();
        this.append("add");
        if (this.pilhaTipos.peek().equals("int64")) {
            this.append("conv.r8");
        }
    }

    // - (subtract)
    private void acao111() {
        this.aritmetica();
        this.append("sub");
        if (this.pilhaTipos.peek().equals("int64")) {
            this.append("conv.r8");
        }
    }

    // * (multiply)
    private void acao112() {
        this.aritmetica();
        this.append("mul");
        if (this.pilhaTipos.peek().equals("int64")) {
            this.append("conv.r8");
        }
    }

    // / (divide)
    private void acao113() {
        this.aritmetica();
        this.append("div");
        if (this.pilhaTipos.peek().equals("int64")) {
            this.append("conv.r8");
        }
    }

    // constante_int
    private void acao114(String token) {
        this.pilhaTipos.push("int64");
        this.append("ldc.i8 " + token);
        this.append("conv.r8");
    }

    // constate_float
    private void acao115(String token) {
        this.pilhaTipos.push("float64");
        this.append("ldc.r8 " + token);
    }

    // constante_string
    private void acao116(String token) {
        this.pilhaTipos.push("string");
        this.append("ldstr " + token);
    }

    // Número negativo
    private void acao117() {
        String tipo = pilhaTipos.pop();
        if (tipo.equals("int64")) {
            this.append("ldc.i8 -1");
            this.append("conv.r8");
        } else if (tipo.equals("float64")) {
            this.append("ldc.r8 -1.0");
        }
        this.append("mul");
    }

    // if
    private void acao118(Token token) throws SemanticError {
        String tipo = pilhaTipos.pop();
        if (tipo.equals("bool")) {
            String rotulo = "novo_rotulo1";
            this.append("brfalse novo_rotulo1");
            pilhaRotulos.push(rotulo);
        } else {
            throw new SemanticError("expressão incompatível em comando de seleção", token.getPosition());
        }
    }

    private void acao119() {
        this.desempilharRotulo();
    }

    private void acao120() {
        String rotulo = "novo_rotulo2";
        this.append("br novo_rotulo2");
        this.desempilharRotulo();
        pilhaRotulos.push(rotulo);
    }

    private void acao121() {
        String rotulo = "novo_rotulo1";
        this.append(rotulo + ":");
        this.pilhaRotulos.push(rotulo);
    }

    private void acao122(Token token) throws SemanticError {
        String tipo = this.pilhaTipos.pop();
        if (tipo.equals("bool")) {
            String rotulo = "novo_rotulo2";
            this.append("brfalse " + rotulo);
            pilhaRotulos.push(rotulo);
        } else {
            throw new SemanticError("expressão incompatível em comando de repetição", token.getPosition());
        }
    }

    private void acao123() {
        String rotulo2 = pilhaRotulos.pop();
        String rotulo1 = pilhaRotulos.pop();
        this.append("br " + rotulo1);
        this.append(rotulo2 + ":");
    }

    private void acao124(Token token) throws SemanticError {
        String tipo = pilhaTipos.pop();
        if (tipo.equals("bool")) {
            String rotulo = pilhaRotulos.pop();
            this.append("brtrue " + rotulo);
        } else {
            throw new SemanticError("expressão incompatívem em comando de repetição", token.getPosition());
        }
    }

    private void acao125(String id) {
        this.listaIdentificadores.add(id);
    }

    private void acao126(Token token) throws SemanticError {
        for (String id : listaIdentificadores) {
            if (this.tabelaSimbolos.get(id) != null) {
                throw new SemanticError(id + " já declarado", token.getPosition());
            } else {
                this.tabelaSimbolos.put(id, new Simbolo(id, token.getLexeme()));
            }
        }
        this.listaIdentificadores = new LinkedList<>();
    }

    private void acao127(Token token) throws SemanticError {
        for (String id : listaIdentificadores) {
            if (this.tabelaSimbolos.get(id) != null) {
                throw new SemanticError(id + " já declarado", token.getPosition());
            } else {
                Simbolo s = new Simbolo(id);
                this.tabelaSimbolos.put(id, s);
                this.append(".locals(" + s.getTipo() + " " + id + ")");
            }
        }
        this.listaIdentificadores = new LinkedList<>();
    }

    private void acao128(Token token) throws SemanticError {
        String tipo = pilhaTipos.pop();
        for (int i = 0; i < listaIdentificadores.size() - 1; i++) {
            this.append("dup");
        }
        for (String id : listaIdentificadores) {
            if (tabelaSimbolos.get(id) != null) {
                if (tipo.startsWith("_i")) {
                    this.append("conv.i8");
                }
                this.append("stloc " + id);
            } else {
                throw new SemanticError(id + " não declarado", token.getPosition());
            }
        }
        this.listaIdentificadores = new LinkedList<>();
    }

    private void acao129(Token token) throws SemanticError {
        for (String id : listaIdentificadores) {
            Simbolo s = this.tabelaSimbolos.get(id);
            if (s != null) {
                // switch (s.getTipo()) {
                //     case "int64":
                //         this.append("ldc.i8 " + s.getValor());
                //         break;
                //     case "float64":
                //         this.append("ldc.r8 " + s.getValor());
                //         break;
                //     case "string":
                //         this.append("ldstr " + s.getValor());
                //         break;
                //     case "bool":
                //         this.append("ldc.i4." + s.getValorMaquina());
                //         break;
                // }
                this.append("stloc " + id);
            } else {
                throw new SemanticError(id + " não declarado", token.getPosition());
            }
        }
        this.listaIdentificadores = new LinkedList<>();
    }

    private void acao130(Token token) {
        this.append("ldstr " + token.getLexeme());
        this.append("call void [mscorlib]System.Console::Write(string)");
        this.append("call string [mscorlib]System.Console::ReadLine()");
        this.append("call int64 [mscorlib]System.Int64::Parse(string)");
    }

    // expressão
    private void acao131(Token token) throws SemanticError {
        String id = token.getLexeme();
        Simbolo s = this.tabelaSimbolos.get(id);
        if (s != null) {
            if (id.startsWith("_")) {
                this.append("ldloc " + s.getId());
                if (s.getTipo().equals("int64")) {
                    this.append("conv.r8");
                } 
                this.pilhaTipos.push(s.getTipo());
            } else {
                switch (s.getTipo()) {
                    case "int64":
                        this.append("ldc.i8 " + s.getValor());
                        this.append("conv.r8");
                        break;
                    case "float64":
                        this.append("ldc.r8 " + s.getValor());
                        break;
                    case "string":
                        this.append("ldstr " + s.getValor());
                        break;
                    case "bool":
                        this.append("ldc.i4." + s.getValorMaquina());
                    break;
                }
                this.pilhaTipos.push(s.getTipo());
            }
        } else {
            throw new SemanticError(id + " não declarado", token.getPosition());
        }
    }

    private void append(String value) {
        this.codigoObjeto += "\n" + value;
    }

    private void desempilharRotulo() {
        this.append(this.pilhaRotulos.pop() + ":");
    }

    private void aritmetica() {
        String tipo1 = this.pilhaTipos.pop();
        String tipo2 = this.pilhaTipos.pop();
        if (!tipo1.equals(tipo2)) {
            tipo1 = "float64";
        }
        this.pilhaTipos.push(tipo1);
    }
    
    private void logica() {
        this.pilhaTipos.pop();
        this.pilhaTipos.pop();
        this.pilhaTipos.push("bool");
    }

}
