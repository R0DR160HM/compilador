package analyzer;

import java.util.Stack;

public class Semantico implements Constants {

    private String operadorRelacional = "";
    private String codigoObjeto = "";
    private Stack<String> pilhaTipos = new Stack<>();

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
            default:
                System.err.println("Operação " + action + " não identificada!");
        }
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
        this.append("ldc.r4.1");
    }

    // false
    private void acao106() {
        this.pilhaTipos.push("bool");
        this.append("ldc.r4.0");
    }

    // not
    private void acao107() {
        this.append("ldc.i4.1");
        this.append("xor");
    }

    // Salva operador relacional
    private void acao108(String token) {
        this.operadorRelacional = token;
        pilhaTipos.pop();
        pilhaTipos.pop();
        this.pilhaTipos.push("bool");
    }

    // Realizar comparação
    private void acao109() {
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
    }

    // - (subtract)
    private void acao111() {
        this.aritmetica();
        this.append("sub");
    }

    // * (multiply)
    private void acao112() {
        this.aritmetica();
        this.append("mul");
    }

    // / (divide)
    private void acao113() {
        this.aritmetica();
        this.append("div");
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

    private void append(String value) {
        this.codigoObjeto += "\n" + value;
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
