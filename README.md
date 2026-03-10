# INFO805_TP

- Mathieu LIN
- Master 1 informatique semestre 8 
- 2026

## Consignes d'exécution

Restez à l'origine du dossier INFO805_TP/

## TP1 (20/01/2026) 

Compilation « l'évaluateur »

L'objectif du TP est de se familiariser avec les outils JFlex et CUP.

### Java

#### Installation

Vous aurez besoin d'installer Java dans le terminal.

#### Exécution
```bash
java -jar jflex-full-1.9.1.jar -d TP1/Java/src/main/java TP1/Java/src/main/jflex/expr.jflex
java -jar java-cup-11b.jar -destdir TP1/Java/src/main/java TP1/Java/src/main/cup/expr.cup
javac -cp ".;java-cup-11b-runtime.jar" TP1/Java/src/main/java/*.java
java -cp ".;java-cup-11b-runtime.jar" TP1.Java.src.main.java.Main TP1/Java/result/tpEvaluateurSource.txt
```

### C

#### Installation

Si vous êtes sous Windows, le plus simple est d'utiliser WinFlexBison ou de passer par WSL (Linux) ou MSYS2.

- Sur Linux/WSL : sudo apt install flex bison build-essential
- Sur Mac : brew install flex bison

#### Exécution 

```bash
bison -d TP1/C/expr.y -o TP1/C/expr.tab.c
flex -o TP1/C/lex.yy.c TP1/C/expr.l
gcc TP1/C/expr.tab.c TP1/C/lex.yy.c -o TP1/C/parser
./TP1/C/parser < TP1/C/result/tpEvaluateurSource.txt
```

## TP2 (11/02/2026) 

Génération d'arbres abstraits

L'objectif du TP est d'utiliser les outils JFlex et CUP pour générer des arbres abstraits correspondant à un sous ensemble du langage λ-ada.

### Java

#### Installation

Vous aurez besoin d'installer Java dans le terminal.

#### Exécution
```bash
java -jar jflex-full-1.9.1.jar -d TP2/src/main/java/fr/usmb/m1isc/compilation/tp/ TP2/src/main/jflex/AnalyseurLexical.jflex
java -jar java-cup-11b.jar -destdir TP2/src/main/java/fr/usmb/m1isc/compilation/tp/ TP2/src/main/cup/AnalyseurSyntaxique.cup
javac -cp ".;java-cup-11b-runtime.jar" TP2/src/main/java/fr/usmb/m1isc/compilation/tp/*.java
java -cp ".;java-cup-11b-runtime.jar" TP2.src.main.java.fr.usmb.m1isc.compilation.tp.Main TP2/result/tpEvaluateurSource.txt
```

## TP34 (26/02/2026, 10/03/2026)

Génération d'arbres abstraits

L'objectif du TP est d'utiliser les outils JFlex et CUP pour générer des arbres abstraits correspondant à un sous ensemble du langage λ-ada.

### Java

#### Installation

Vous aurez besoin d'installer Java dans le terminal.

#### Exécution (Exercice 1)
```bash
java -jar jflex-full-1.9.1.jar -d TP34/exercice1/main/java/fr/usmb/m1isc/compilation/tp/ TP34/exercice1/main/jflex/AnalyseurLexical.jflex
java -jar java-cup-11b.jar -destdir TP34/exercice1/main/java/fr/usmb/m1isc/compilation/tp/ TP34/exercice1/main/cup/AnalyseurSyntaxique.cup
javac -cp ".;java-cup-11b-runtime.jar" TP34/exercice1/main/java/fr/usmb/m1isc/compilation/tp/*.java
java -cp ".;java-cup-11b-runtime.jar" TP34.exercice1.main.java.fr.usmb.m1isc.compilation.tp.Main TP34/result/tpEvaluateurSource.txt
```

#### Exécution (Exercice 2)
```bash
java -jar jflex-full-1.9.1.jar -d TP34/src/main/java/fr/usmb/m1isc/compilation/tp/ TP34/src/main/jflex/AnalyseurLexical.jflex
java -jar java-cup-11b.jar -destdir TP34/src/main/java/fr/usmb/m1isc/compilation/tp/ TP34/src/main/cup/AnalyseurSyntaxique.cup
javac -cp ".;java-cup-11b-runtime.jar" TP34/src/main/java/fr/usmb/m1isc/compilation/tp/*.java

# Pour l'exercice 2
java -cp ".;java-cup-11b-runtime.jar" TP34.src.main.java.fr.usmb.m1isc.compilation.tp.Main TP34/result/pgcd.txt
java -cp ".;java-cup-11b-runtime.jar" TP34.src.main.java.fr.usmb.m1isc.compilation.tp.Main TP34/result/pgcd_rec.txt

# Pour la partie fibonacci
java -cp ".;java-cup-11b-runtime.jar" TP34.src.main.java.fr.usmb.m1isc.compilation.tp.Main TP34/result/fibonacci.txt
java -cp ".;java-cup-11b-runtime.jar" TP34.src.main.java.fr.usmb.m1isc.compilation.tp.Main TP34/result/fibonacci_rec.txt

# Pour la partie machine à pile
java -jar vm-0.9.jar pgcd.asm
java -jar vm-0.9.jar pgcd.asm --debug
java -jar vm-0.9.jar pgcd_rec.asm
```



