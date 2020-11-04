grammar impl;

/* A small imperative language */

start   :  cs+=command* EOF ;

program : c=command                      # SingleCommand
	| '{' cs+=command* '}'           # MultipleCommands
	;
	
command : l=lhs '=' e=expr ';'	         # Assignment
	| 'output' e=expr ';'            # Output
        | 'while' '('c=condition')' p=program  # WhileLoop
	| 'for' '(' x=ID '=' e1=expr '..' e2=expr ')' p=program # ForLoop
	| 'if' '(' c=condition ')' p=program # If
	;

lhs     : x=ID                # Variable
	|   a=ID '[' e=expr ']' # Array
	;

expr	: e1=expr o=OP1 e2=expr # Addition
	| e1=expr o=OP2 e2=expr # Multiplication
	| c=FLOAT     	      # Constant
	| l=lhs		      # Lefthandside
	| '(' e=expr ')'      # Parenthesis
	;

condition : e1=expr '!=' e2=expr # Unequal
	  | e1=expr '==' e2=expr # Equal
	  | e1=expr '<' e2=expr # Smaller
	  | c1=condition '||' c2=condition # Disjunction
	  | c1=condition '&&' c2=condition # Conjunction
	  | '!' c=condition     # Negation
	  | '(' c=condition ')' # ParenthesisCondition
	  ;  

ID    : ALPHA (ALPHA|NUM)* ;
FLOAT : NUM+ ('.' NUM+)? ;

ALPHA : [a-zA-Z_ÆØÅæøå] ;
NUM   : [0-9] ;

OP1   : '+' | '-' ;
OP2   : '*' | '/' ;

WHITESPACE : [ \n\t\r]+ -> skip;
COMMENT    : '//'~[\n]*  -> skip;
COMMENT2   : '/*' (~[*] | '*'~[/]  )*   '*/'  -> skip;
