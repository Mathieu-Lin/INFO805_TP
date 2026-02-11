package TP2.src.main.java.fr.usmb.m1isc.compilation.tp;

public abstract class Arbre {
    public abstract String toString();
}

// Nœud pour les opérations binaires (+, -, *, /, ;, etc.)
class NoeudBinaire extends Arbre {
    String op;
    Arbre fg, fd;
    public NoeudBinaire(String op, Arbre fg, Arbre fd) {
        this.op = op; this.fg = fg; this.fd = fd;
    }
    public String toString() {
        return "(" + op + " " + fg + (fd == null ? "" : " " + fd) + ")";
    }
}

// Nœud pour les feuilles (Entiers, Identifiants)
class Feuille extends Arbre {
    String valeur;
    public Feuille(Object v) { this.valeur = String.valueOf(v); }
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
    public String toString() {
        String res = "(" + type + " " + cond + " " + alors;
        if (sinon != null) res += " " + sinon;
        return res + ")";
    }
}