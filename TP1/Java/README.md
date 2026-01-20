## Évaluateur d'expressions arithmétiques (`+` , `*`) infixées sur les nombres entiers.

### Analyse lexicale
Source pour l'analyseur lexical (JFlex) : *[src/main/jflex/expr.jflex](src/main/jflex/expr.jflex)*

On reconnait les différents mots du langage (lexème) : 
- opérateurs arithmétiques : +, *
- les parenthèses ouvrantes et fermante : (, )
- les entiers : `[1-9][0-9]*`
- les espaces (espaces, tabulation, passage à la ligne) : \s
- le lexème indiquant la fin d'une expression : ;


### Analyse syntaxique
Source de l'analyseur syntaxique : *[src/main/cup/expr.cup](src/main/cup/expr.cup)*

Il s'agit de reconnaître la syntaxe (grammaire du langage).
Il travaille à partir des lexèmes (token) qui remontent de l'analyseur lexical. 
Tous les lexèmes doivent être déclarés comme des symboles terminaux.

```JFLEX
terminal PLUS, MUL, ENTIER; 
```

Les règles de la grammaire décrivent le langage :
- on a une suite d'expressions (au moins une)
- chaque expression est terminée par un point-virgule (lexème SEMI)
- une expression est soit :
    - un entier (lexème ENTIER)
    - une expression arithmétique simple : expression_gauche opérateur expression_droite
    - une expression parenthèsée : ( expression )
    
```
// on a une liste d'expressions (avec au moins une expression)
// chaque expression arithmetique est terminee par un point virgule (SEMI)
listeexpr ::= listeexpr expr SEMI
            | expr SEMI
            | error SEMI
            ;
expr  ::= ENTIER 
        | expr PLUS expr  
        | expr MUL expr 
        | PAR_G expr PAR_D   
        ;
```

Les règles du genre : `expression ::= expression PLUS expression` introduisent des ambiguïtés dans la grammaire 
(ce que n'aime pas l'analyseur). 
Il faut dont lever ces ambiguïtés, soit en réécrivant les règles (cf. exemple donné en cours pour les expressions arithmétiques), 
soit en précisant (comme le permet l'analyseur syntaxique) l'associativité et la priorité des opérateurs :

```
precedence left PLUS;
precedence left MUL;
```

On liste les opérateurs du moins prioritaire au plus prioritaire.

### Évaluation des expressions
Pour l'évaluation des expressions, il faut aussi disposer des valeurs des entiers reconnus par l'analyseur lexical. 
Avec JFlex et CUP cela peut se faire en passant un paramètre supplémentaire lors de la remontée du lexème par l'analyseur lexical :

```JFLEX
{uint}      { return new Symbol(ParserSym.ENTIER, new Integer(yytext())); }
```

Au niveau de CUP (analyseur syntaxique), on peut avoir une valeur associée à chacun des symboles (terminaux ou non terminaux). 
Le type de cette valeur doit être spécifié lors de la déclation du symbole : 

```
/* symboles terminaux */
terminal PLUS, MUL; 
terminal Integer ENTIER;
/* non terminaux */
non terminal listeexpr;
non terminal Integer expr ;
```

Dans notre cas, on aura un entier associé 
- au symbole terminal (lexème) `ENTIER` - il est défini par l'analyzeur lexical - 
- au symbole non terminal `expression`.

Pour calculer les valeurs associées aux symboles non terminaux, on ajoute des actions sémantique dans les règles (valeur associée au non terminal en partie gauche : `RESULT`) :  
`expr  ::= expr:e1  PLUS expr:e2   {: RESULT = e1 + e2; :}`

On ajoute aussi une action sémantique pour afficher la valeur finale de l'expression :  
`listeexpr   ::= expr:e SEMI {: System.out.println("valeur = "+e); :}`

```
/* grammaire */
// on a une liste d'expressions (avec au moins une expression)
listeexpr   ::= listeexpr expr:e {: System.out.println("valeur = "+e); :} SEMI
              | expr:e {: System.out.println("valeur = "+e); :} SEMI
              | error SEMI
              ;
expr  ::= expr:e1  PLUS expr:e2   {: RESULT = e1 + e2; :}
        | expr:e1 MUL expr:e2     {: RESULT = e1 * e2 ;:}
        | PG expr:e PD      {: RESULT = e; :}
        | ENTIER:e          {: RESULT = e; :}
        ;
```

La règle `listeexpr ::= error SEMI` permet de gérér les erreurs syntaxiques en définissant
un point de reprise d'erreur après l'obtention d'un point virgule.

### Utilisation du numéro de ligne et de colonne et réécriture des messages d'erreur.
Il est possible de demander à l'analyseur lexical (JFlex) de passer à l'analyseur syntaxique (CUP) 
le numéro de ligne et de colonne dans le lexème.
CUP les propage alors au niveau des règles de la grammaire, 
ce qui permet des les utiliser dans les actions sémantiques et les messages d'erreur.

__Passage numero de ligne et de colonne dans JFLEX :__

```JFLEX
/* ------------------------Section des Regles Lexicales----------------------*/
\+          { return new Symbol(ParserSym.PLUS, yyline, yycolumn); }
\*          { return new Symbol(ParserSym.MUL, yyline, yycolumn); }
\;          { return new Symbol(ParserSym.SEMI, yyline, yycolumn); }
\(          { return new Symbol(ParserSym.PG, yyline, yycolumn); }
\)          { return new Symbol(ParserSym.PD, yyline, yycolumn); }
...
```

__Réécriture du message d'erreur par défaut :__

On peut adapter la méthode utilisée pour rapporter les erreurs en redefinissant `report_error` :

```
parser code {:
  // pour le parser (redefinition de la methode reportant les erreurs d'analyse)
  public void report_error(String message, Object info) {
    String m = "";
    if (info instanceof java_cup.runtime.Symbol) {
        Symbol s = ((Symbol) info);     
        if (s != null && s.left >= 0 ) {
            /* Ajoute le numero de ligne  et de colonne*/
            m =  "Ligne " + (s.left+1) + ", Colonne " + (s.right+1) + " : ";
            }
        }
    m = m + message;
    System.err.println(m);
    }
:};
```
