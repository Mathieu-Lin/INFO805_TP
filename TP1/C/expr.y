%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void yyerror(const char *s);
extern int yylex();
extern int yylineno;

/* Symbol Table Structure */
struct symbol {
    char *name;
    int value;
    struct symbol *next;
};
struct symbol *sym_table = NULL;

int get_var(char *name, int *val);
void set_var(char *name, int val);

int erreur_detectee = 0;
%}

%union {
    int val;
    char *name;
}

/* Enable location tracking for column reporting */
%locations

%token PLUS MOINS MUL DIV MOD PG PD SEMI EQUAL LET ERROR
%token <val> ENTIER
%token <name> IDENT

%type <val> expr

%left PLUS MOINS
%left MUL DIV MOD
%nonassoc MOINS_UNAIRE

%%

listeexpr : listeexpr ligne
          | ligne
          ;

ligne : expr SEMI
        {
            if (!erreur_detectee) {
                // $1 refers to the value of 'expr'
                // @1 refers to the location (line/column) of 'expr'
                printf("Ligne %d, Colonne %d : Eval = %d\n", @1.first_line, @1.first_column, $1);
            }
            erreur_detectee = 0;
        }
      | LET IDENT EQUAL expr SEMI
        {
            if (!erreur_detectee) {
                set_var($2, $4); // $2 is IDENT, $4 is expr
                printf("Ligne %d, Colonne %d : Eval = %d\n", @4.first_line, @4.first_column, $4);
            }
            free($2);
            erreur_detectee = 0;
        }
      | error SEMI
        {
            erreur_detectee = 0;
        }
      ;

expr : expr PLUS expr  { $$ = $1 + $3; }
     | expr MOINS expr { $$ = $1 - $3; }
     | expr MUL expr   { $$ = $1 * $3; }
     | expr DIV expr
        {
            if (!erreur_detectee) {
                if ($3 == 0) {
                    fprintf(stderr, "Ligne %d, Colonne %d : Erreur division par zero\n", @3.first_line, @3.first_column);
                    erreur_detectee = 1;
                    $$ = 0;
                } else {
                    $$ = $1 / $3;
                }
            }
        }
     | expr MOD expr   { $$ = $1 % $3; }
     | MOINS expr      { $$ = -$2; } %prec MOINS_UNAIRE
     | IDENT
        {
            int val;
            if (!erreur_detectee) {
                if (get_var($1, &val)) {
                    $$ = val;
                } else {
                    fprintf(stderr, "Ligne %d, Colonne %d : Erreur variable indefinie\n", @1.first_line, @1.first_column);
                    erreur_detectee = 1;
                    $$ = 0;
                }
            }
            free($1);
        }
     | ENTIER          { $$ = $1; }
     | PG expr PD      { $$ = $2; }
     ;

%%

/* Helper functions remain the same as previous response */
void set_var(char *name, int val) {
    struct symbol *s;
    for (s = sym_table; s != NULL; s = s->next) {
        if (strcmp(s->name, name) == 0) {
            s->value = val;
            return;
        }
    }
    s = malloc(sizeof(struct symbol));
    s->name = strdup(name);
    s->value = val;
    s->next = sym_table;
    sym_table = s;
}

int get_var(char *name, int *val) {
    struct symbol *s;
    for (s = sym_table; s != NULL; s = s->next) {
        if (strcmp(s->name, name) == 0) {
            *val = s->value;
            return 1;
        }
    }
    return 0;
}

void yyerror(const char *s) {
    fprintf(stderr, "Ligne %d, Colonne %d : Syntax error\n", yylineno, yylloc.first_column);
}

int main() {
    return yyparse();
}
