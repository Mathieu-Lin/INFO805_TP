package TP34.src.main.java.fr.usmb.m1isc.compilation.tp;

import java.util.Set;

public class NoeudBinaire extends Arbre {
    String op;
    Arbre fg, fd;

    public NoeudBinaire(String op, Arbre fg, Arbre fd) {
        this.op = op;
        this.fg = fg;
        this.fd = fd;
    }

    @Override
    public String genererCode() {
        String code = "";
        switch (this.op) {
            case "+":
                code += fg.genererCode();
                code += "\tpush eax\n";
                code += fd.genererCode();
                code += "\tpop ebx\n";
                code += "\tadd eax, ebx\n";
                break;

            case "-":
                if (fd == null) {
                    code += fg.genererCode();
                    code += "\tmov ebx, eax\n";
                    code += "\tmov eax, 0\n";
                    code += "\tsub eax, ebx\n";
                } else {
                    code += fg.genererCode();
                    code += "\tpush eax\n";
                    code += fd.genererCode();
                    code += "\tpop ebx\n";       // ebx = gauche
                    code += "\tsub ebx, eax\n";  // ebx = gauche - droite
                    code += "\tmov eax, ebx\n";
                }
                break;

            case "*":
                code += fg.genererCode();
                code += "\tpush eax\n";
                code += fd.genererCode();
                code += "\tpop ebx\n";
                code += "\tmul eax, ebx\n";
                break;

            case "/":
                code += fg.genererCode();
                code += "\tpush eax\n";
                code += fd.genererCode();        // eax = droite
                code += "\tpop ebx\n";           // ebx = gauche
                code += "\tdiv ebx, eax\n";      // ebx = gauche/droite
                code += "\tmov eax, ebx\n";
                break;

            case "MOD":
                code += fg.genererCode();        // eax = a
                code += "\tpush eax\n";
                code += fd.genererCode();        // eax = b
                code += "\tpop ebx\n";           // ebx = a, eax = b
                code += "\tmov ecx, eax\n";      // ecx = b
                code += "\tmov eax, ebx\n";      // eax = a
                code += "\tdiv ebx, ecx\n";      // ebx = a/b
                code += "\tmul ebx, ecx\n";      // ebx = (a/b)*b
                code += "\tsub eax, ebx\n";      // eax = a - (a/b)*b
                break;

            case ";":
                code = fg.genererCode() + fd.genererCode();
                break;

            case "OUTPUT":
                code += fg.genererCode();
                code += "\tout eax\n";
                break;

            case "<": {
                int idComp = Arbre.compteurLabel++;
                code += fg.genererCode();
                code += "\tpush eax\n";
                code += fd.genererCode();        
                code += "\tpop ebx\n";           
                code += "\tsub ebx, eax\n";      
                code += "\tjl vrai_jl_" + idComp + "\n";
                code += "\tmov eax, 0\n";
                code += "\tjmp fin_jl_" + idComp + "\n";
                code += "vrai_jl_" + idComp + ":\n";
                code += "\tmov eax, 1\n";
                code += "fin_jl_" + idComp + ":\n";
                break;
            }

            case "<=": {
                int idComp = Arbre.compteurLabel++;
                code += fd.genererCode();
                code += "\tpush eax\n";
                code += fg.genererCode();        
                code += "\tpop ebx\n";           
                code += "\tsub ebx, eax\n";      
                code += "\tjl faux_jle_" + idComp + "\n";
                code += "\tmov eax, 1\n";
                code += "\tjmp fin_jle_" + idComp + "\n";
                code += "faux_jle_" + idComp + ":\n";
                code += "\tmov eax, 0\n";
                code += "fin_jle_" + idComp + ":\n";
                break;
            }

            case "=": {
                int idComp = Arbre.compteurLabel++;
                code += fg.genererCode();
                code += "\tpush eax\n";
                code += fd.genererCode();
                code += "\tpop ebx\n";
                code += "\tsub ebx, eax\n";
                code += "\tjz vrai_eq_" + idComp + "\n";
                code += "\tmov eax, 0\n";
                code += "\tjmp fin_eq_" + idComp + "\n";
                code += "vrai_eq_" + idComp + ":\n";
                code += "\tmov eax, 1\n";
                code += "fin_eq_" + idComp + ":\n";
                break;
            }

            case "NOT":
                {
                    int idNot = Arbre.compteurLabel++;
                    code = fg.genererCode();
                    code += "\tjz vrai_not_" + idNot + "\n";
                    code += "\tmov eax, 0\n";
                    code += "\tjmp fin_not_" + idNot + "\n";
                    code += "vrai_not_" + idNot + ":\n";
                    code += "\tmov eax, 1\n";
                    code += "fin_not_" + idNot + ":\n";
                }
                break;

            case "AND": {
                int idAnd = Arbre.compteurLabel++;
                code += fg.genererCode();
                code += "\tjz faux_and_" + idAnd + "\n";
                code += fd.genererCode();
                code += "\tjz faux_and_" + idAnd + "\n";
                code += "\tmov eax, 1\n";
                code += "\tjmp fin_and_" + idAnd + "\n";
                code += "faux_and_" + idAnd + ":\n";
                code += "\tmov eax, 0\n";
                code += "fin_and_" + idAnd + ":\n";
                break;
            }

            case "OR": {
                int idOr = Arbre.compteurLabel++;
                code += fg.genererCode();
                code += "\tjnz vrai_or_" + idOr + "\n";
                code += fd.genererCode();
                code += "\tjnz vrai_or_" + idOr + "\n";
                code += "\tmov eax, 0\n";
                code += "\tjmp fin_or_" + idOr + "\n";
                code += "vrai_or_" + idOr + ":\n";
                code += "\tmov eax, 1\n";
                code += "fin_or_" + idOr + ":\n";
                break;
            }

            case ",":
                code += fg.genererCode();
                code += "\tpush eax\n";
                code += fd.genererCode();
                break;

            case "THEN":
                return fg.genererCode();

            case "FCALL": {
                int nbArgs = compterArgs(fd);
                code += fg.genererCode();        
                code += "\tpush eax\n";
                code += genererArgs(fd);
                code += "\tmov eax, " + (nbArgs * 4) + "[esp]\n";
                code += "\tcall eax\n";
                code += "\tadd esp, " + (nbArgs * 4) + "\n";
                code += "\tpop eax\n";
                break;
            }

            default:
                System.err.println("NoeudBinaire: opérateur inconnu : " + op);
        }
        return code;
    }

    private int compterArgs(Arbre args) {
        if (args == null) return 0;
        if (args instanceof Feuille) {
            String v = ((Feuille) args).valeur;
            if (v.equals("nil")) return 0;
            return 1;
        }
        if (args instanceof NoeudBinaire && ((NoeudBinaire) args).op.equals(",")) {
            NoeudBinaire n = (NoeudBinaire) args;
            return 1 + compterArgs(n.fd);
        }
        return 1; 
    }

    private String genererArgs(Arbre args) {
        if (args == null) return "";
        if (args instanceof Feuille && ((Feuille) args).valeur.equals("nil")) return "";
        if (args instanceof NoeudBinaire && ((NoeudBinaire) args).op.equals(",")) {
            NoeudBinaire n = (NoeudBinaire) args;
            String code = n.fg.genererCode();
            code += "\tpush eax\n";
            code += genererArgs(n.fd);
            return code;
        }
        return args.genererCode() + "\tpush eax\n";
    }

    @Override
    public void afficherArbre(String prefix, boolean estDernier) {
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + op);
        String nouveauPrefix = prefix + (estDernier ? "    " : "│   ");
        if (fg != null) fg.afficherArbre(nouveauPrefix, fd == null);
        if (fd != null) fd.afficherArbre(nouveauPrefix, true);
    }

    @Override
    public void collecterVariables(Set<String> vars) {
        if (fg != null) fg.collecterVariables(vars);
        if (fd != null) fd.collecterVariables(vars);
    }

    @Override
    public void calculerTypes() {
        if (fg != null) fg.calculerTypes();
        if (fd != null) fd.calculerTypes();

        switch (op) {
            case "<": typeNoeud = "BOOL"; break;
            case ",": typeNoeud = "INTxINT"; break;
            case "FCALL": typeNoeud = "INT"; break;
            case "THEN": typeNoeud = "INT"; break;
            case "OUTPUT": typeNoeud = "INT"; break;
            case "MOD": case "+": case "-": case "*": case "/": typeNoeud = "INT"; break;
            case ";": typeNoeud = "INT"; break;
            default: typeNoeud = ""; break;
        }
    }

    @Override
    public void afficherArbre2(String prefix, boolean estDernier) {
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + op + " " + typeNoeud);
        String nouveauPrefix = prefix + (estDernier ? "    " : "│   ");
        if (fg != null) fg.afficherArbre2(nouveauPrefix, fd == null);
        if (fd != null) fd.afficherArbre2(nouveauPrefix, true);
    }

    @Override
    public String toString() {
        return "(" + op + " " + fg + (fd == null ? "" : " " + fd) + ")";
    }
}