/**
 * Define a grammar called Sirius
 */
lexer grammar SLexer;

channels { CommentChannel }

@lexer::header {
	package org.sirius.frontend.parser;
}

COMMENT : '/*' .*? '*/' -> channel(CommentChannel);

MODULE : 'module';

EQUAL : '=' ;
DIV_EQUAL : '/=' ;
MUL_EQUAL : '*=' ;
PLUS_EQUAL : '+=' ;
MINUS_EQUAL : '-=' ;
OR_OR : '||';
AND_AND : '&&';
EQUAL_EQUAL : '==';
NOT_EQUAL : '!=';
LOWER_EQUAL : '<=';
GREATER_EQUAL : '>=';
ARROW : '->' ;

PLUS : '+';
MINUS : '-';
STAR : '*';
SLASH : '/';
XOR : '^';

SHARED : 'shared';
IMPLEMENTS : 'implements' ;
INTERFACE : 'interface' ;
CLASS : 'class';
PACKAGE : 'package';
VOID : 'void';
IMPORT : 'import';
RETURN : 'return';
IF : 'if';
ELSE : 'else';
FUNCTION : 'function';


Colon: ':';
Semicolon: ';';

COMA: ',';
DOT : '.';

LBRACK : '[';
RBRACK : ']';

AMPERSAND : '&';
OR : '|';

GT : '>';
LT : '<';

OpenPar: '(';
ClosePar: ')';
OpenCurly: '{';
CloseCurly: '}';
QuestionMark: '?';





////////////////////////////////////////////////////
//channels {COMMENT}


	
BOOLEAN : 'true' | 'false' ;
IN : 'in';
OUT : 'out';



TYPE_ID : [A-Z][a-zA-Z0-9_]* ;	// start by uppercase

LOWER_ID : [a-z][a-zA-Z0-9_]* ;	// start by lowercase

//ALIAS_ID : [a-zA-Z][a-zA-Z0-9_]* ;

ID : [a-zA-Z][a-zA-Z0-9_]* ;

WS : [ \t\r\n]+ -> skip ;

SHEBANG : '#''!'~('\r' | '\n')*;

STRING : '"' ~('"')* '"' ;

FLOAT	: [0-9]+ '.' [0-9]+ ;
INTEGER : ('-'|'+')?[0-9]+ ;

