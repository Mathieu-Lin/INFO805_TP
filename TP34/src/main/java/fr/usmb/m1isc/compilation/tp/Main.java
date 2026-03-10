package TP34.src.main.java.fr.usmb.m1isc.compilation.tp;

import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        LexicalAnalyzer yy;
        String outputFilename = "out.asm"; 

        if (args.length > 0) {
            yy = new LexicalAnalyzer(new FileReader(args[0]));
            outputFilename = new File(args[0]).getName().replaceFirst("[.][^.]+$", "") + ".asm";
        } else {
            yy = new LexicalAnalyzer(new InputStreamReader(System.in));
        }

        parser p = new parser(yy);
        Symbol s = p.parse();
        Arbre ast = (Arbre) s.value;

        if (ast != null) {
            // 1. Arbre SANS type
            System.out.println("Arbre abstrait généré (sans le calcul de type) :");
            ast.afficherArbre("", true);
            
            // 2. Passe Sémantique
            ast.calculerTypes();

            // 3. Arbre AVEC types
            System.out.println("\nArbre abstrait généré (avec le calcul de type) :");
            ast.afficherArbre2("", true);

            // ----------------------------------------------------------------
            // 4. Création du code Assembleur (En mémoire)
            // ----------------------------------------------------------------
            StringBuilder codeAsm = new StringBuilder();
            
            codeAsm.append("DATA SEGMENT\n");
            for (String var : ast.collecterVariables()) {
                codeAsm.append("\t").append(var).append(" DD\n");
            }
            codeAsm.append("DATA ENDS\n");

            codeAsm.append("CODE SEGMENT\n");
            codeAsm.append(ast.genererCode()); 

            if (!Arbre.lambdaBodies.isEmpty()) {
                Arbre.compteurLabel++; 
                int progEnd = Arbre.compteurLabel++;
                codeAsm.append("\tjmp end_pg_").append(progEnd).append("\n");

                for (String body : Arbre.lambdaBodies) {
                    codeAsm.append(body);
                }
                codeAsm.append("end_pg_").append(progEnd).append(":\n");
            }

            codeAsm.append("CODE ENDS\n");

            // ----------------------------------------------------------------
            // 5. AFFICHAGE DANS LA CONSOLE (Comme demandé par le sujet !)
            // ----------------------------------------------------------------
            System.out.println("\nCode généré :");
            System.out.print(codeAsm.toString());

            // ----------------------------------------------------------------
            // 6. Écriture dans le fichier .asm
            // ----------------------------------------------------------------
            try (PrintWriter out = new PrintWriter(outputFilename)) {
                out.print(codeAsm.toString());
            }

            System.out.println("\n[SUCCÈS] Le code a été généré et sauvegardé dans le fichier : " + outputFilename);
        }
    }
}