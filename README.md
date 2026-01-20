# INFO805_TP

- Mathieu LIN
- Master 1 informatique semestre 8 
- 2026

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

## TP2

à suivre

## TP3

à suivre

