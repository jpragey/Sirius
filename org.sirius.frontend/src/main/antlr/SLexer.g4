/**
 * Define a grammar called Sirius
 */
lexer grammar SLexer;

channels { CommentChannel, COMMENTS_CHANNEL }


//@members {
//	public static final int COMMENTS_CHANNEL = 1;
//}
@lexer::members {
//	public static final int COMMENTS_CHANNEL = 1;
}

@header {
	package org.sirius.frontend.parser;
//	package org.sirius.frontend.grammar;
}
//@lexer::header {
//	package org.sirius.frontend.parser;
////	package org.sirius.frontend.grammar;
//}

//COMMENT : '/*' .*? '*/' -> channel(CommentChannel);
COMMENT : '/*' .*? '*/' -> channel(COMMENTS_CHANNEL);
//COMMENT : '/*' .*? '*/' ;

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


//channels { CCOMMENT }
//WS : ' ' -> channel(CHAN);
//Char : .;




//
//
//
//// These are all supported lexer sections:
//
//// Lexer file header. Appears at the top of h + cpp files. Use e.g. for copyrights.
////@lexer::header {/* lexer header section */}
//
//// Appears before any #include in h + cpp files.
//@lexer::preinclude {/* lexer precinclude section */}
//
//// Follows directly after the standard #includes in h + cpp files.
//@lexer::postinclude {
///* lexer postinclude section */
//#ifndef _WIN32
//#pragma GCC diagnostic ignored "-Wunused-parameter"
//#endif
//}
//
//// Directly preceds the lexer class declaration in the h file (e.g. for additional types etc.).
//@lexer::context {/* lexer context section */}
//
//// Appears in the public part of the lexer in the h file.
//@lexer::members {/* public lexer declarations section */
//bool canTestFoo() { return true; }
//bool isItFoo() { return true; }
//bool isItBar() { return true; }
//
//void myFooLexerAction() { /* do something*/ };
//void myBarLexerAction() { /* do something*/ };
//}
//
//// Appears in the private part of the lexer in the h file.
//@lexer::declarations {/* private lexer declarations/members section */}
//
//// Appears in line with the other class member definitions in the cpp file.
//@lexer::definitions {/* lexer definitions section */}
//
//channels { CommentsChannel, DirectiveChannel }
////
////tokens {
////	DUMMY
////}




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

