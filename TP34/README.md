# TP Compilation : Génération de code pour un sous ensemble du langage λ-ada.

À partir de l'arbre abstrait construit lors du dernier TP, avec les outils JFlex et CUP, l'objectif consiste à générer du code pour la machine à registres décrite dans le cours, afin d'être en mesure d'exécuter les programmes reconnus par l'analyseur sur la machine à registres.

## Exercice 1

Dans la première partie du tp on pourra se limiter à la génération de code pour les expressions arithmétiques sur les nombres entiers.

Ainsi, l'expression :

```
let prixHt = 200;
let prixTtc =  prixHt * 119 / 100 .
```

Exécution : [Voir le README dont la partie de TP34](./../README.md)

Résultats : 

```
P> java -cp ".;java-cup-11b-runtime.jar" TP34.exercice1.main.java.fr.usmb.m1isc.compilation.tp.Main TP34/result/tpEvaluateurSource.txt
DATA SEGMENT
        prixHt DD
        prixTtc DD
DATA ENDS
CODE SEGMENT
        mov eax, 200
        mov prixHt, eax
        mov eax, prixHt
        push eax
        mov eax, 119
        pop ebx
        mul eax, ebx
        push eax
        mov eax, 100
        pop ebx
        div ebx, eax
        mov eax, ebx
        mov prixTtc, eax
CODE ENDS

STRUCTURE DE L'ARBRE :
└── ;
    ├── LET
    │   ├── prixHt
    │   └── 200
    └── LET
        ├── prixTtc
        └── /
            ├── *
            │   ├── prixHt
            │   └── 119
            └── 100
```

## Exercice 2

### Partie itérative de PGCD
Voici l'expression de pgcd.txt :
```
let a = input;
let b = input;
while (0 < b)
do (let aux=(a mod b); let a=b; let b=aux );
output a
.
```

Exécution : [Voir le README dont la partie de TP34](./../README.md)

Résultats : 

```
> java -cp ".;java-cup-11b-runtime.jar" TP34.src.main.java.fr.usmb.m1isc.compilation.tp.Main TP34/result/pgcd.txt
DATA SEGMENT
        a DD
        b DD
        aux DD
DATA ENDS
CODE SEGMENT
        in eax
        mov a, eax
        in eax
        mov b, eax
debut_while_0:
        mov eax, 0
        push eax
        mov eax, b
        pop ebx
        sub ebx, eax
        jl vrai_jl_1
        mov eax, 0
        jmp fin_jl_1
vrai_jl_1:
        mov eax, 1
fin_jl_1:
        jz fin_while_0
        mov eax, a
        push eax
        mov eax, b
        pop ebx
        mov ecx, eax
        mov eax, ebx
        div ebx, ecx
        mul ebx, ecx
        sub eax, ebx
        mov aux, eax
        mov eax, b
        mov a, eax
        mov eax, aux
        mov b, eax
        jmp debut_while_0
fin_while_0:
        mov eax, a
        out eax
CODE ENDS

STRUCTURE DE L'ARBRE :
└── ;
    ├── LET
    │   ├── a
    │   └── INPUT
    └── ;
        ├── LET
        │   ├── b
        │   └── INPUT
        └── ;
            ├── WHILE
            │   ├── <
            │   │   ├── 0
            │   │   └── b
            │   └── ;
            │       ├── LET
            │       │   ├── aux
            │       │   └── MOD
            │       │       ├── a
            │       │       └── b
            │       └── ;
            │           ├── LET
            │           │   ├── a
            │           │   └── b
            │           └── LET
            │               ├── b
            │               └── aux
            └── OUTPUT
                └── a
```

### Partie récursive de PGCD

Voici l'expression de pgcd_rec.txt :
```
let x = input;
let y = input;
let pgcd = 
  lambda (a, b) (
    if (0 < b) then pgcd(b, a mod b) else a
  );
let z = output pgcd(x, y);
output z
.
```

Exécution : [Voir le README dont la partie de TP34](./../README.md)

Résultats : 

```
> java -cp ".;java-cup-11b-runtime.jar" TP34.src.main.java.fr.usmb.m1isc.compilation.tp.Main TP34/result/pgcd_rec.txt
DATA SEGMENT
        x DD
        y DD
        pgcd DD
        z DD
DATA ENDS
CODE SEGMENT
        in eax
        mov x, eax
        in eax
        mov y, eax
        lea eax, lambda_0
        mov pgcd, eax
        mov eax, pgcd
        push eax
        mov eax, x
        push eax
        mov eax, y
        push eax
        mov eax, 8[esp]
        call eax
        add esp, 8
        pop eax
        out eax
        mov z, eax
        mov eax, z
        out eax
        jmp end_pg_4
lambda_0:
        enter 0
        mov eax, 0
        push eax
        mov eax, 8[ebp]
        pop ebx
        sub ebx, eax
        jl vrai_jl_1
        mov eax, 0
        jmp fin_jl_1
vrai_jl_1:
        mov eax, 1
fin_jl_1:
        jz else_2
        mov eax, pgcd
        push eax
        mov eax, 8[ebp]
        push eax
        mov eax, 12[ebp]
        push eax
        mov eax, 8[ebp]
        pop ebx
        mov ecx, eax
        mov eax, ebx
        div ebx, ecx
        mul ebx, ecx
        sub eax, ebx
        push eax
        mov eax, 8[esp]
        call eax
        add esp, 8
        pop eax
        jmp fin_if_2
else_2:
        mov eax, 12[ebp]
fin_if_2:
        mov 16[ebp], eax
        leave
        ret
end_pg_4:
CODE ENDS

STRUCTURE DE L'ARBRE :
└── ;
    ├── LET
    │   ├── x
    │   └── INPUT
    └── ;
        ├── LET
        │   ├── y
        │   └── INPUT
        └── ;
            ├── LET
            │   ├── pgcd
            │   └── LAMBDA
            │       ├── ,
            │       │   ├── a
            │       │   └── b
            │       └── IF
            │           ├── <
            │           │   ├── 0
            │           │   └── b
            │           ├── THEN
            │           │   └── FCALL
            │           │       ├── pgcd
            │           │       └── ,
            │           │           ├── b
            │           │           └── MOD
            │           │               ├── a
            │           │               └── b
            │           └── a
            └── ;
                ├── LET
                │   ├── z
                │   └── OUTPUT
                │       └── FCALL
                │           ├── pgcd
                │           └── ,
                │               ├── x
                │               └── y
                └── OUTPUT
                    └── z
```

Ainsi que l'arbre (avec le calcul de type) :

```
Arbre abstrait généré (avec le calcul de type) :
└── ; INT
    ├── LET INT
    │   ├── x INT
    │   └── INPUT INT
    └── ; INT
        ├── LET INT
        │   ├── y INT
        │   └── INPUT INT
        └── ; INT
            ├── LET f0:(INTxINT-->INT)
            │   ├── pgcd f0:(INTxINT-->INT)
            │   └── LAMBDA f1:(INTxINT-->INT)
            │       ├── , INTxINT
            │       │   ├── a[12] INT
            │       │   └── b[8] INT
            │       └── IF INT
            │           ├── < BOOL
            │           │   ├── 0 INT
            │           │   └── b[8] INT
            │           ├── THEN INT
            │           │   └── FCALL INT
            │           │       ├── pgcd f0:(INTxINT-->INT)
            │           │       └── , INTxINT
            │           │           ├── b[8] INT
            │           │           └── MOD INT
            │           │               ├── a[12] INT
            │           │               └── b[8] INT
            │           └── a[12] INT
            └── ; INT
                ├── LET INT
                │   ├── z INT
                │   └── OUTPUT INT
                │       └── FCALL INT
                │           ├── pgcd f0:(INTxINT-->INT)
                │           └── , INTxINT
                │               ├── x INT
                │               └── y INT
                └── OUTPUT INT
                    └── z INT
```


## PGCD est déjà utilisé dans le sujet donc on passe sur fibonacci ? 

### Itérative 

```
Arbre abstrait généré (sans le calcul de type) :
└── ;
    ├── LET
    │   ├── n
    │   └── INPUT
    └── ;
        ├── LET
        │   ├── a
        │   └── 0
        └── ;
            ├── LET
            │   ├── b
            │   └── 1
            └── ;
                ├── LET
                │   ├── i
                │   └── 0
                └── ;
                    ├── WHILE
                    │   ├── <
                    │   │   ├── i
                    │   │   └── n
                    │   └── ;
                    │       ├── LET
                    │       │   ├── aux
                    │       │   └── +
                    │       │       ├── a
                    │       │       └── b
                    │       └── ;
                    │           ├── LET
                    │           │   ├── a
                    │           │   └── b
                    │           └── ;
                    │               ├── LET
                    │               │   ├── b
                    │               │   └── aux
                    │               └── LET
                    │                   ├── i
                    │                   └── +
                    │                       ├── i
                    │                       └── 1
                    └── OUTPUT
                        └── a

Arbre abstrait généré (avec le calcul de type) :
└── ; INT
    ├── LET INT
    │   ├── n INT
    │   └── INPUT INT
    └── ; INT
        ├── LET INT
        │   ├── a[12] INT
        │   └── 0 INT
        └── ; INT
            ├── LET INT
            │   ├── b[8] INT
            │   └── 1 INT
            └── ; INT
                ├── LET INT
                │   ├── i INT
                │   └── 0 INT
                └── ; INT
                    ├── WHILE INT
                    │   ├── < BOOL
                    │   │   ├── i INT
                    │   │   └── n INT
                    │   └── ; INT
                    │       ├── LET INT
                    │       │   ├── aux INT
                    │       │   └── + INT
                    │       │       ├── a[12] INT
                    │       │       └── b[8] INT
                    │       └── ; INT
                    │           ├── LET INT
                    │           │   ├── a[12] INT
                    │           │   └── b[8] INT
                    │           └── ; INT
                    │               ├── LET INT
                    │               │   ├── b[8] INT
                    │               │   └── aux INT
                    │               └── LET INT
                    │                   ├── i INT
                    │                   └── + INT
                    │                       ├── i INT
                    │                       └── 1 INT
                    └── OUTPUT INT
                        └── a[12] INT

Code généré :
DATA SEGMENT
        n DD
        a[12] DD
        b[8] DD
        i DD
        aux DD
DATA ENDS
CODE SEGMENT
        in eax
        mov n, eax
        mov eax, 0
        mov a[12], eax
        mov eax, 1
        mov b[8], eax
        mov eax, 0
        mov i, eax
debut_while_0:
        mov eax, i
        push eax
        mov eax, n
        pop ebx
        sub ebx, eax
        jl vrai_jl_1
        mov eax, 0
        jmp fin_jl_1
vrai_jl_1:
        mov eax, 1
fin_jl_1:
        jz fin_while_0
        mov eax, a[12]
        push eax
        mov eax, b[8]
        pop ebx
        add eax, ebx
        mov aux, eax
        mov eax, b[8]
        mov a[12], eax
        mov eax, aux
        mov b[8], eax
        mov eax, i
        push eax
        mov eax, 1
        pop ebx
        add eax, ebx
        mov i, eax
        jmp debut_while_0
fin_while_0:
        mov eax, a[12]
        out eax
CODE ENDS
```

### Récursive 

```
Arbre abstrait généré (sans le calcul de type) :
└── ;
    ├── LET
    │   ├── n
    │   └── INPUT
    └── ;
        ├── LET
        │   ├── fib
        │   └── LAMBDA
        │       ├── x
        │       └── IF
        │           ├── <
        │           │   ├── x
        │           │   └── 2
        │           ├── THEN
        │           │   └── x
        │           └── +
        │               ├── FCALL
        │               │   ├── fib
        │               │   └── -
        │               │       ├── x
        │               │       └── 1
        │               └── FCALL
        │                   ├── fib
        │                   └── -
        │                       ├── x
        │                       └── 2
        └── OUTPUT
            └── FCALL
                ├── fib
                └── n

Arbre abstrait généré (avec le calcul de type) :
└── ; INT
    ├── LET INT
    │   ├── n INT
    │   └── INPUT INT
    └── ; INT
        ├── LET INT
        │   ├── fib INT
        │   └── LAMBDA f1:(INTxINT-->INT)
        │       ├── x INT
        │       └── IF INT
        │           ├── < BOOL
        │           │   ├── x INT
        │           │   └── 2 INT
        │           ├── THEN INT
        │           │   └── x INT
        │           └── + INT
        │               ├── FCALL INT
        │               │   ├── fib INT
        │               │   └── - INT
        │               │       ├── x INT
        │               │       └── 1 INT
        │               └── FCALL INT
        │                   ├── fib INT
        │                   └── - INT
        │                       ├── x INT
        │                       └── 2 INT
        └── OUTPUT INT
            └── FCALL INT
                ├── fib INT
                └── n INT

Code généré :
DATA SEGMENT
        n DD
        fib DD
DATA ENDS
CODE SEGMENT
        in eax
        mov n, eax
        lea eax, lambda_0
        mov fib, eax
        mov eax, fib
        push eax
        mov eax, n
        push eax
        mov eax, 4[esp]
        call eax
        add esp, 4
        pop eax
        out eax
        jmp end_pg_4
lambda_0:
        enter 0
        mov eax, 8[ebp]
        push eax
        mov eax, 2
        pop ebx
        sub ebx, eax
        jl vrai_jl_1
        mov eax, 0
        jmp fin_jl_1
vrai_jl_1:
        mov eax, 1
fin_jl_1:
        jz else_2
        mov eax, 8[ebp]
        jmp fin_if_2
else_2:
        mov eax, fib
        push eax
        mov eax, 8[ebp]
        push eax
        mov eax, 1
        pop ebx
        sub ebx, eax
        mov eax, ebx
        push eax
        mov eax, 4[esp]
        call eax
        add esp, 4
        pop eax
        push eax
        mov eax, fib
        push eax
        mov eax, 8[ebp]
        push eax
        mov eax, 2
        pop ebx
        sub ebx, eax
        mov eax, ebx
        push eax
        mov eax, 4[esp]
        call eax
        add esp, 4
        pop eax
        pop ebx
        add eax, ebx
fin_if_2:
        mov 16[ebp], eax
        leave
        ret
end_pg_4:
CODE ENDS
```
### Émulateur pour la machine à pile

```
> java -jar vm-0.9.jar pgcd.asm
>21
>14
>>>>7
>>>>>>>>>>>>>>>>>>>>>> That's all
```

On peut tester avec d'autre fichier :
```
> java -jar vm-0.9.jar pgcd_rec.asm
>14
>7
>>>>7
>>>>7
>>>>>>>>>>>>>>>>>>>>>> That's all
```
## Conclusion 

Ce TP me permet de réaliser une compilation et utile pour réaliser une langage de programmation.