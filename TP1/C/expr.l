%{
#include "expr.tab.h"
#include <string.h>

int column = 1;

/* Update column counter for each token */
void update_loc() {
    yylloc.first_line = yylineno;
    yylloc.first_column = column;
    column += yyleng;
}

#define YY_USER_ACTION update_loc();
%}

%option yylineno

%%
"+"         { return PLUS; }
"-"         { return MOINS; }
"*"         { return MUL; }
"/"         { return DIV; }
"mod"       { return MOD; }
";"         { return SEMI; }
"("         { return PG; }
")"         { return PD; }
"="         { return EQUAL; }
"let"       { return LET; }

[0-9]+      { yylval.val = atoi(yytext); return ENTIER; }
[a-zA-Z][a-zA-Z0-9]* { yylval.name = strdup(yytext); return IDENT; }

"/*"([^*]|[*]+[^*/])*[*]+"/" { /* ignore */ }
"//".* { /* ignore */ }

[ \t]       { /* skip */ }
\n          { column = 1; }
.           { return ERROR; }
%%

int yywrap() { return 1; }
