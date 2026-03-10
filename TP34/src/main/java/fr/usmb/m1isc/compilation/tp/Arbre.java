package TP34.src.main.java.fr.usmb.m1isc.compilation.tp;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;

public abstract class Arbre {
    // Compteur partagé par tous les types de nœuds pour créer des labels uniques
    protected static int compteurLabel = 0;
    
    // NOUVEAU : Buffer pour stocker le code des lambdas et l'afficher à la fin
    public static List<String> lambdaBodies = new ArrayList<>();
    // NOUVEAU : Offset dynamique pour le retour
    public static int currentRetOffset = 16;
    public abstract String toString();
    public abstract String genererCode();
    public abstract void collecterVariables(Set<String> vars);

    public Set<String> collecterVariables() {
        Set<String> vars = new LinkedHashSet<>(); // LinkedHashSet pour garder l'ordre (a, b, aux)
        collecterVariables(vars);
        return vars;
    }

    public abstract void afficherArbre(String prefix, boolean estDernier);
    public static boolean inLambda = false; // Indicateur de contexte

    protected String typeNoeud = ""; 
    public abstract void calculerTypes();
    public abstract void afficherArbre2(String prefix, boolean estDernier);
}