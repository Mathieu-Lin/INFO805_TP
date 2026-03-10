package TP34.exercice1.main.java.fr.usmb.m1isc.compilation.tp;

import java_cup.runtime.Symbol; // <--- AJOUTER CETTE LIGNE
import java.io.FileReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        LexicalAnalyzer yy;
        if (args.length > 0)
            yy = new LexicalAnalyzer(new FileReader(args[0]));
        else
            yy = new LexicalAnalyzer(new InputStreamReader(System.in));
            
        parser p = new parser(yy);
        
        // On récupère le résultat de l'analyse
        Symbol s = p.parse(); 
        Arbre ast = (Arbre) s.value;

        if (ast != null) {
            // 1. Collecte des variables pour le segment DATA
            System.out.println("DATA SEGMENT");
            for (String var : ast.collecterVariables()) {
                System.out.println("\t" + var + " DD");
            }
            System.out.println("DATA ENDS");

            // 2. Génération du segment CODE
            System.out.println("CODE SEGMENT");
            System.out.print(ast.genererCode());
            System.out.println("CODE ENDS");

			// 3. Affichage de l'arbre
			System.out.println("\nSTRUCTURE DE L'ARBRE :");
    		ast.afficherArbre("", true);
        }
    }
}