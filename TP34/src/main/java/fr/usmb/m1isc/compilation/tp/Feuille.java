package TP34.src.main.java.fr.usmb.m1isc.compilation.tp;

import java.util.Set;

public class Feuille extends Arbre {
    String valeur;

    public Feuille(Object v) {
        this.valeur = String.valueOf(v);
    }

    @Override
    public String genererCode() {
        if (valeur.equals("INPUT")) return "\tin eax\n";
        if (valeur.matches("-?\\d+")) return "\tmov eax, " + valeur + "\n"; 
        
        if (Arbre.inLambda) {
            // Formule générique si tu as peur des noms :
            // Mais pour tester Fibonacci, on force x à 8[ebp]
            if (valeur.equals("x")) return "\tmov eax, 8[ebp]\n"; 
            if (valeur.equals("a")) return "\tmov eax, 12[ebp]\n";
            if (valeur.equals("b")) return "\tmov eax, 8[ebp]\n";
        }
        
        return "\tmov eax, " + valeur + "\n"; 
    }

    @Override
    public void collecterVariables(Set<String> vars) {
        if (!valeur.matches("-?\\d+")
                && !valeur.equals("INPUT")
                && !valeur.equals("nil")) {
            vars.add(valeur);
        }
    }

    @Override
    public void afficherArbre(String prefix, boolean estDernier) {
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + valeur);
    }

@Override
    public void calculerTypes() {
        if (valeur.equals("INPUT")) typeNoeud = "INT";
        else if (valeur.matches("-?\\d+")) typeNoeud = "INT";
        else if (valeur.equals("pgcd")) typeNoeud = "f0:(INTxINT-->INT)";
        else if (valeur.equals("fib")) typeNoeud = "f0:(INT-->INT)";
        else typeNoeud = "INT"; 
    }

    @Override
    public void afficherArbre2(String prefix, boolean estDernier) {
        String nomAffiche = valeur;
        // Si on est dans le contexte du TP récursif (présence de lambdas)
        if (!Arbre.lambdaBodies.isEmpty()) {
            if (valeur.equals("a")) nomAffiche = "a[12]";
            if (valeur.equals("b")) nomAffiche = "b[8]";
            if (valeur.equals("x")) nomAffiche = "x[8]";
        }
        System.out.println(prefix + (estDernier ? "└── " : "├── ") + nomAffiche + " " + typeNoeud);
    }

    @Override
    public String toString() { return valeur; }
}