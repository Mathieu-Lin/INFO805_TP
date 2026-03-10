package TP34.exercice1.main.java.fr.usmb.m1isc.compilation.tp;
import java.util.Set;
import java.util.TreeSet;

public abstract class Arbre {
    public abstract String toString();
    // Chaque type de nœud saura comment générer son propre code
    public abstract String genererCode();
    // Pour collecter les variables (DATA SEGMENT)
    public abstract void collecterVariables(Set<String> vars);

    public Set<String> collecterVariables() {
        Set<String> vars = new TreeSet<>();
        collecterVariables(vars);
        return vars;
    }

    public abstract void afficherArbre(String prefix, boolean estDernier);

}

// Nœud pour les opérations binaires (+, -, *, /, ;)
class NoeudBinaire extends Arbre {
    String op;
    Arbre fg, fd;
    public NoeudBinaire(String op, Arbre fg, Arbre fd) {
        this.op = op; this.fg = fg; this.fd = fd;
    }
    
    @Override
    public String genererCode() {
        String code = "";
        switch (this.op) {
            case "+":
                code += fg.genererCode() + "\tpush eax\n" + fd.genererCode() + "\tpop ebx\n\tadd eax, ebx\n";
                break;
            case "-":
                // Pour la soustraction : gauche - droite
                code += fd.genererCode() + "\tpush eax\n" + fg.genererCode() + "\tpop ebx\n\tsub eax, ebx\n";
                break;
            case "*":
                code += fg.genererCode() + "\tpush eax\n" + fd.genererCode() + "\tpop ebx\n\tmul eax, ebx\n";
                break;
            case "/":
                code += fg.genererCode() + "\tpush eax\n" + fd.genererCode() + "\tpop ebx\n\tdiv ebx, eax\n\tmov eax, ebx\n";
                break;
            case ";":
                code += fg.genererCode() + fd.genererCode();
                break;
            case "OUTPUT":
                code += fg.genererCode() + "\tout eax\n";
                break;
        }
        return code;
    }

    @Override
    public void afficherArbre(String prefix, boolean estDernier) {
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + op);
        String nouveauPrefix = prefix + (estDernier ? "    " : "│   ");
        
        // On affiche d'abord le fils gauche, puis le droit
        if (fg != null) {
            fg.afficherArbre(nouveauPrefix, fd == null);
        }
        if (fd != null) {
            fd.afficherArbre(nouveauPrefix, true);
        }
    }

    @Override
    public void collecterVariables(Set<String> vars) {
        if (fg != null) fg.collecterVariables(vars);
        if (fd != null) fd.collecterVariables(vars);
    }

    public String toString() { return "(" + op + " " + fg + (fd == null ? "" : " " + fd) + ")"; }
}

// Nœud pour les feuilles (Entiers, Identifiants, INPUT)
class Feuille extends Arbre {
    String valeur;
    public Feuille(Object v) { this.valeur = String.valueOf(v); }
    
    @Override
    public String genererCode() {
        if (valeur.equals("INPUT")) return "\tin eax\n";
        if (valeur.matches("-?\\d+")) return "\tmov eax, " + valeur + "\n"; // Si c'est un nombre
        return "\tmov eax, " + valeur + "\n"; // Si c'est une variable
    }

    @Override
    public void collecterVariables(Set<String> vars) {
        // Si la valeur n'est pas un nombre, ni un mot clé, c'est une variable
        if (!valeur.matches("-?\\d+") && !valeur.equals("INPUT") && !valeur.equals("nil")) {
            vars.add(valeur);
        }
    }

    @Override
    public void afficherArbre(String prefix, boolean estDernier) {
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + valeur);
    }

    public String toString() { return valeur; }
}

// Nœud pour les structures de contrôle (IF, WHILE, LET)
class NoeudControle extends Arbre {
    String type;
    Arbre cond, alors, sinon;
    public NoeudControle(String type, Arbre c, Arbre a, Arbre s) {
        this.type = type; this.cond = c; this.alors = a; this.sinon = s;
    }

    @Override
    public String genererCode() {
        if (type.equals("LET")) {
            // cond est l'ID (Feuille), alors est l'expression
            return alors.genererCode() + "\tmov " + cond.toString() + ", eax\n";
        }
        // TODO: Implémenter WHILE et IF pour l'Ex 2
        return "";
    }

    @Override
    public void collecterVariables(Set<String> vars) {
        if (type.equals("LET")) vars.add(cond.toString());
        if (cond != null) cond.collecterVariables(vars);
        if (alors != null) alors.collecterVariables(vars);
        if (sinon != null) sinon.collecterVariables(vars);
    }

    @Override
    public void afficherArbre(String prefix, boolean estDernier) {
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + type);
        String nouveauPrefix = prefix + (estDernier ? "    " : "│   ");
        
        if (cond != null) cond.afficherArbre(nouveauPrefix, (alors == null && sinon == null));
        if (alors != null) alors.afficherArbre(nouveauPrefix, (sinon == null));
        if (sinon != null) sinon.afficherArbre(nouveauPrefix, true);
    }
    
    @Override
    public String toString() {
        return "(" + type + " " + cond + " " + alors + (sinon != null ? " " + sinon : "") + ")";
    }
}