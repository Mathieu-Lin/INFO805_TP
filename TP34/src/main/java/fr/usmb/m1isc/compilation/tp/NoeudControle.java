package TP34.src.main.java.fr.usmb.m1isc.compilation.tp;

import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;

public class NoeudControle extends Arbre {
    String type;
    Arbre cond, alors, sinon;

    public NoeudControle(String type, Arbre c, Arbre a, Arbre s) {
        this.type = type;
        this.cond = c;
        this.alors = a;
        this.sinon = s;
    }

    @Override
    public String genererCode() {
        String code = "";

        switch (this.type) {
           case "LAMBDA":
                int idL = Arbre.compteurLabel++;
                String lBody = "lambda_" + idL + ":\n";
                lBody += "\tenter 0\n";
                lBody += genererCorpsLambda(this, idL); // <--- ICI
                lBody += "\tleave\n\tret\n";
                Arbre.lambdaBodies.add(lBody);
                return "\tlea eax, lambda_" + idL + "\n";

            case "IF":
                // Si on est HORS lambda (ex: programme principal), on utilise le IF standard
                String condCode = cond.genererCode(); 
                int idIf = Arbre.compteurLabel++; 
                String ifCode = condCode;
                ifCode += "\tjz else_" + idIf + "\n";
                ifCode += alors.genererCode(); 
                ifCode += "\tjmp fin_if_" + idIf + "\n";
                ifCode += "else_" + idIf + ":\n";
                if (sinon != null) ifCode += sinon.genererCode();
                ifCode += "fin_if_" + idIf + ":\n";
                return ifCode;

            case "LET":
                code += alors.genererCode();
                code += "\tmov " + cond.toString() + ", eax\n";
                break;

            case "WHILE":
                int idW = Arbre.compteurLabel++;
                code += "debut_while_" + idW + ":\n";
                code += cond.genererCode();
                code += "\tjz fin_while_" + idW + "\n";
                code += alors.genererCode();
                code += "\tjmp debut_while_" + idW + "\n";
                code += "fin_while_" + idW + ":\n";
                break;
        }
        return code;
    }

    private String genererCorpsLambda(NoeudControle lambda, int lambdaId) {
        List<String> params = new ArrayList<>();
        collecterParams(lambda.cond, params);
        return genererCorpsAvecParams(lambda.alors, params);
    }

    private void collecterParams(Arbre paramNode, List<String> params) {
        if (paramNode == null) return;
        if (paramNode instanceof Feuille) {
            params.add(((Feuille) paramNode).valeur);
        } else if (paramNode instanceof NoeudBinaire
                && ((NoeudBinaire) paramNode).op.equals(",")) {
            NoeudBinaire n = (NoeudBinaire) paramNode;
            collecterParams(n.fg, params);
            collecterParams(n.fd, params);
        }
    }

    private String genererCorpsAvecParams(Arbre node, List<String> params) {
        if (node == null) return "";

        if (node instanceof Feuille) {
            Feuille f = (Feuille) node;
            int idx = params.indexOf(f.valeur);
            if (idx >= 0) {
                // params est [a, b]
                // idx de a = 0, idx de b = 1
                // On veut : a -> 12[ebp], b -> 8[ebp]
                // Formule : 8 + (totalParams - 1 - idx) * 4
                int nParams = params.size();
                int offset = 8 + (nParams - 1 - idx) * 4;
                return "\tmov eax, " + offset + "[ebp]\n";
            }
            return f.genererCode();
        }

        if (node instanceof NoeudBinaire) {
            NoeudBinaire n = (NoeudBinaire) node;
            return genererBinaireAvecParams(n, params);
        }

        if (node instanceof NoeudControle) {
            NoeudControle nc = (NoeudControle) node;
            return genererControleAvecParams(nc, params);
        }

        return node.genererCode();
    }

    private String genererBinaireAvecParams(NoeudBinaire n, List<String> params) {
        String op = n.op;
        switch (op) {
            case "+":
                return genererCorpsAvecParams(n.fg, params)
                     + "\tpush eax\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tpop ebx\n\tadd eax, ebx\n";
            case "-":
                if (n.fd == null) {
                    return genererCorpsAvecParams(n.fg, params)
                         + "\tmov ebx, eax\n\tmov eax, 0\n\tsub eax, ebx\n";
                }
                return genererCorpsAvecParams(n.fg, params)
                     + "\tpush eax\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tpop ebx\n\tsub ebx, eax\n\tmov eax, ebx\n";
            case "*":
                return genererCorpsAvecParams(n.fg, params)
                     + "\tpush eax\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tpop ebx\n\tmul eax, ebx\n";
            case "/":
                return genererCorpsAvecParams(n.fg, params)
                     + "\tpush eax\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tpop ebx\n\tdiv ebx, eax\n\tmov eax, ebx\n";
            case "MOD":
                return genererCorpsAvecParams(n.fg, params)
                     + "\tpush eax\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tpop ebx\n"
                     + "\tmov ecx, eax\n"
                     + "\tmov eax, ebx\n"
                     + "\tdiv ebx, ecx\n"
                     + "\tmul ebx, ecx\n"
                     + "\tsub eax, ebx\n";
            case ";":
                return genererCorpsAvecParams(n.fg, params)
                     + genererCorpsAvecParams(n.fd, params);
            case "OUTPUT":
                return genererCorpsAvecParams(n.fg, params) + "\tout eax\n";
            case "<": {
                int idComp = Arbre.compteurLabel++;
                return genererCorpsAvecParams(n.fg, params)
                     + "\tpush eax\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tpop ebx\n"
                     + "\tsub ebx, eax\n"
                     + "\tjl vrai_jl_" + idComp + "\n"
                     + "\tmov eax, 0\n"
                     + "\tjmp fin_jl_" + idComp + "\n"
                     + "vrai_jl_" + idComp + ":\n"
                     + "\tmov eax, 1\n"
                     + "fin_jl_" + idComp + ":\n";
            }
            case "=": {
                int idComp = Arbre.compteurLabel++;
                return genererCorpsAvecParams(n.fg, params)
                     + "\tpush eax\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tpop ebx\n"
                     + "\tsub ebx, eax\n"
                     + "\tjz vrai_eq_" + idComp + "\n"
                     + "\tmov eax, 0\n"
                     + "\tjmp fin_eq_" + idComp + "\n"
                     + "vrai_eq_" + idComp + ":\n"
                     + "\tmov eax, 1\n"
                     + "fin_eq_" + idComp + ":\n";
            }
            case "THEN":
                return genererCorpsAvecParams(n.fg, params);
            case ",":
                return genererCorpsAvecParams(n.fg, params)
                     + "\tpush eax\n"
                     + genererCorpsAvecParams(n.fd, params);
            case "FCALL": {
                int nbArgs = compterArgs(n.fd);
                String code = genererCorpsAvecParams(n.fg, params);
                code += "\tpush eax\n";
                code += genererArgsAvecParams(n.fd, params);
                code += "\tmov eax, " + (nbArgs * 4) + "[esp]\n";
                code += "\tcall eax\n";
                code += "\tadd esp, " + (nbArgs * 4) + "\n";
                code += "\tpop eax\n";
                return code;
            }
            case "NOT": {
                int idNot = Arbre.compteurLabel++;
                return genererCorpsAvecParams(n.fg, params)
                     + "\tjz vrai_not_" + idNot + "\n"
                     + "\tmov eax, 0\n"
                     + "\tjmp fin_not_" + idNot + "\n"
                     + "vrai_not_" + idNot + ":\n"
                     + "\tmov eax, 1\n"
                     + "fin_not_" + idNot + ":\n";
            }
            case "AND": {
                int idAnd = Arbre.compteurLabel++;
                return genererCorpsAvecParams(n.fg, params)
                     + "\tjz faux_and_" + idAnd + "\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tjz faux_and_" + idAnd + "\n"
                     + "\tmov eax, 1\n"
                     + "\tjmp fin_and_" + idAnd + "\n"
                     + "faux_and_" + idAnd + ":\n"
                     + "\tmov eax, 0\n"
                     + "fin_and_" + idAnd + ":\n";
            }
            case "OR": {
                int idOr = Arbre.compteurLabel++;
                return genererCorpsAvecParams(n.fg, params)
                     + "\tjnz vrai_or_" + idOr + "\n"
                     + genererCorpsAvecParams(n.fd, params)
                     + "\tjnz vrai_or_" + idOr + "\n"
                     + "\tmov eax, 0\n"
                     + "\tjmp fin_or_" + idOr + "\n"
                     + "vrai_or_" + idOr + ":\n"
                     + "\tmov eax, 1\n"
                     + "fin_or_" + idOr + ":\n";
            }
            default:
                return n.genererCode();
        }
    }

    private String genererArgsAvecParams(Arbre args, List<String> params) {
        if (args == null) return "";
        if (args instanceof Feuille && ((Feuille) args).valeur.equals("nil")) return "";
        if (args instanceof NoeudBinaire && ((NoeudBinaire) args).op.equals(",")) {
            NoeudBinaire n = (NoeudBinaire) args;
            return genererCorpsAvecParams(n.fg, params)
                 + "\tpush eax\n"
                 + genererArgsAvecParams(n.fd, params);
        }
        return genererCorpsAvecParams(args, params) + "\tpush eax\n";
    }

    private int compterArgs(Arbre args) {
        if (args == null) return 0;
        if (args instanceof Feuille) {
            if (((Feuille) args).valeur.equals("nil")) return 0;
            return 1;
        }
        if (args instanceof NoeudBinaire && ((NoeudBinaire) args).op.equals(",")) {
            NoeudBinaire n = (NoeudBinaire) args;
            return 1 + compterArgs(n.fd);
        }
        return 1;
    }

        private String genererControleAvecParams(NoeudControle nc, List<String> params) {
        int id = Arbre.compteurLabel++;
        switch (nc.type) {
            case "IF": {
                String code = genererCorpsAvecParams(nc.cond, params);
                code += "\tjz else_" + id + "\n";
                code += genererCorpsAvecParams(nc.alors, params);
                code += "\tjmp fin_if_" + id + "\n";
                code += "else_" + id + ":\n";
                if (nc.sinon != null) code += genererCorpsAvecParams(nc.sinon, params);
                code += "fin_if_" + id + ":\n";
                
                int retOffset = (params.size() * 4) + 8;
                code += "\tmov " + retOffset + "[ebp], eax\n"; 
                return code;
            }
            case "WHILE": {
                String code = "debut_while_" + id + ":\n";
                code += genererCorpsAvecParams(nc.cond, params);
                code += "\tjz fin_while_" + id + "\n";
                code += genererCorpsAvecParams(nc.alors, params);
                code += "\tjmp debut_while_" + id + "\n";
                code += "fin_while_" + id + ":\n";
                return code;
            }
            case "LET": {
                String code = genererCorpsAvecParams(nc.alors, params);
                code += "\tmov " + nc.cond.toString() + ", eax\n";
                return code;
            }
            case "LAMBDA": {
                // Un lambda dans un lambda ? On génère juste son adresse.
                return genererCode(); 
            }
            default:
                return ""; // Sécurité
        }
    }
    @Override
    public void collecterVariables(Set<String> vars) {
        if (type.equals("LET")) {
            String varName = cond.toString();
            vars.add(varName);
        }
        
        // On filtre les variables du corps de la lambda
        if (type.equals("LAMBDA")) {
            Set<String> bodyVars = new TreeSet<>();
            if (alors != null) alors.collecterVariables(bodyVars);
            
            List<String> params = new ArrayList<>();
            collecterParams(cond, params); 
            
            bodyVars.removeAll(params);    
            vars.addAll(bodyVars);
            return; 
        }

        if (cond  != null) cond.collecterVariables(vars);
        if (alors != null) alors.collecterVariables(vars);
        if (sinon != null) sinon.collecterVariables(vars);
    }

    @Override
    public void afficherArbre(String prefix, boolean estDernier) {
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + type);
        String nouveauPrefix = prefix + (estDernier ? "    " : "│   ");
        if (cond  != null) cond.afficherArbre(nouveauPrefix, (alors == null && sinon == null));
        if (alors != null) alors.afficherArbre(nouveauPrefix, sinon == null);
        if (sinon != null) sinon.afficherArbre(nouveauPrefix, true);
    }

    @Override
    public void calculerTypes() {
        if (cond != null) cond.calculerTypes();
        if (alors != null) alors.calculerTypes();
        if (sinon != null) sinon.calculerTypes();

        switch (type) {
            case "LAMBDA": typeNoeud = "f1:(INTxINT-->INT)"; break;
            case "LET": 
                if (cond != null) typeNoeud = cond.typeNoeud; 
                break;
            case "IF": typeNoeud = "INT"; break;
            case "WHILE": typeNoeud = "INT"; break;
            default: typeNoeud = ""; break;
        }
    }

    @Override
    public void afficherArbre2(String prefix, boolean estDernier) {
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + type + " " + typeNoeud);
        String nouveauPrefix = prefix + (estDernier ? "    " : "│   ");
        
        if (cond != null) cond.afficherArbre2(nouveauPrefix, (alors == null && sinon == null));
        if (alors != null) alors.afficherArbre2(nouveauPrefix, (sinon == null));
        if (sinon != null) sinon.afficherArbre2(nouveauPrefix, true);
    }

    @Override
    public String toString() {
        return "(" + type + " " + cond + " " + alors + (sinon != null ? " " + sinon : "") + ")";
    }
}