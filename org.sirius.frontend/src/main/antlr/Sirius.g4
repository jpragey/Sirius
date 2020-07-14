/**
 * Define a grammar called Hello
 */
grammar Sirius;

@lexer::header {
	package org.sirius.frontend.parser;
	import org.sirius.frontend.ast.AstModuleDeclaration;
}
@parser::header {
	package org.sirius.frontend.parser;
	import org.sirius.frontend.ast.*;
	import org.sirius.common.core.QName;
	import org.sirius.frontend.symbols.DefaultSymbolTable;
	
	import java.util.Optional;
	import java.util.HashMap;
	
}

@members {
}


// -------------------- COMPILATION UNITS

/** Usual compilation unit */
standardCompilationUnit 
locals[
]
@init {     
}
    : 						
    ( importDeclaration )*
    (
    	  moduleDeclaration 	 
    	| packageDeclaration	
    	| functionDeclaration 	
    	| classDeclaration 		
    	| interfaceDeclaration	
    )*
	EOF
	;

/** CompilationUnit from script */
scriptCompilationUnit
	: shebangDeclaration ?
      importDeclaration *
	  concreteModule *
	  EOF
	;
	
concreteModule
	: moduleDeclaration
	  (packageElement		)*
	|
	  (packageElement 		)+
	;
	
packageElement
	: packageDeclaration 		 
	| functionDeclaration 		
	| classDeclaration 			
	| interfaceDeclaration		
	; 

packageDescriptorCompilationUnit 
	: packageDeclaration 	
	;

moduleDescriptorCompilationUnit 
	: moduleDeclaration 	
	;

// -------------------- MODULE DECLARATION

moduleDeclaration 
@init {
}
	: 'module' qname version=STRING				{}
	  '{'
	  		( 
	  			  moduleVersionEquivalent
	  			| moduleImport 				
	  		)*
	  '}'
	;

moduleVersionEquivalent 
	: key=LOWER_ID '=' value=STRING ';'	
	;

moduleImport
	: 
		( shared='shared' 			)? 
		'import'    		 
		
		(origin=STRING ':' )?
		
		(	  nameQName=qname 		 
			| nameString=STRING 	
		)
		( 	  version=LOWER_ID  
			| versionString=STRING 
		)
		';'	
;



qname 
	: LOWER_ID		
	('.' LOWER_ID	)*
	;

// -------------------- SHEBANG
shebangDeclaration  
	: SHEBANG 
	;

// -------------------- IMPORTS
importDeclaration
	: 'import'
	  qname		
	  (('{'
	  	  ( e=importDeclarationElement		
	  	    (',' e=importDeclarationElement	)*
	  	  )?
	  '}')
	  | ';'
	  )
	;


importDeclarationElement 
	: 	importName=(LOWER_ID | TYPE_ID)
	|	alias=(LOWER_ID | TYPE_ID) '=' importName=(LOWER_ID | TYPE_ID)
	    ( '{' '}')?
	;

// -------------------- VALUE
memberValueDeclaration 
	:
		annotationList
		type
		LOWER_ID		
		('=' expression	
			
		)?
		';'
	;

// -------------------- FUNCION (top-level or member)

functionDeclaration 
@init {
}
	: annotationList
	  (	  returnType=type	 
	  	| 'void' 	
	  )
	  name=LOWER_ID		
	  (
	    '<'
	  		  	(
	  		d=typeParameterDeclaration 		
	  		(
	  			','
		  		d=typeParameterDeclaration 	
	  		)*
	  	)?
	  	'>'
	  )?
	  
	    
	  '('
	  	(  functionFormalArgument		
	  	  (  ',' functionFormalArgument	)*
	    )?								{  }
	  ')' 
	  ( functionBody )?
	;
	
functionBody
	:'{' 					
	  		( statement )*
	 '}'
	;

functionFormalArgument 
:
	type LOWER_ID
;


// -------------------- STATEMENT

statement 
	: returnStatement			# isReturnStatement
	| expression ';'			# isExpressionStatement
	| localVariableStatement	# isLocalVaribleStatement
	| ifElseStatement			# isIfElseStatement
	;

returnStatement 
	: 'return' expression ';' 
	; 

localVariableStatement 
	: 
		annotationList
		type
		LOWER_ID		
		('=' expression	
			
		)?
		';'
	;

ifElseStatement  
@init {
}
	: 'if' '(' ifExpression = expression ')'
		ifBlock = statement 
		(
			'else' elseBlock = statement 
		)?
	;

// -------------------- EXPRESSION

expression 
	: constantExpression 									# isConstantExpression
	
	| <assoc=right> left=expression op='^' right=expression # isBinaryExpression 
	
	| left=expression op=('*'|'/') right=expression 	# isBinaryExpression
	| left=expression op=('+'|'-') right=expression 	# isBinaryExpression
	
	| left=expression op=('<'|'>'|'<='|'>=') right=expression 	# isBinaryExpression

	| left=expression op=('=='|'!=') right=expression 	# isBinaryExpression
	| left=expression op='&&' right=expression 	# isBinaryExpression
	| left=expression op='||' right=expression 	# isBinaryExpression
	| left=expression op=('='|'+='|'-='|'*='|'/=') right=expression 	# isBinaryExpression
	
	// -- Function call
	| 
		functionCallExpression 		# isFunctionCallExpression
	| 
	    thisExpr=expression '.' functionCallExpression 		# isMethodCallExpression
	
	|  // -- Constructor call
	  name=TYPE_ID '('		
		
		(arg0=expression 	
			( ',' arg1=expression)*
		)?
	  ')' # isConstructorCallExpression
	| 
	  // -- Field access
	  lhs = expression '.' LOWER_ID		# isFieldAccessExpression
	| 
		// -- Local/member/global variable, function parameter
 		ref = LOWER_ID                  # isVariableRefExpression
	;

functionCallExpression 
	: 
		LOWER_ID '('	
		(arg0=expression
			( ',' arg1=expression )*
		)?
	  ')'
	;


constantExpression returns [AstExpression express]
	: STRING	
	| INTEGER	
	| FLOAT		
	| BOOLEAN	
	;


// -------------------- ANNOTATION

annotation 
	: LOWER_ID	
	( '(' ')' )?
	;

annotationList 
	:
	( annotation )*
	;


// -------------------- PACKAGE 

packageDeclaration 
	: 'package' qname ';'
	packageElement *
	;
	
// -------------------- CLASS 
//
classDeclaration 
	: 'class' 		
	  TYPE_ID		
	  '('
	  	(  functionFormalArgument		
	  	  (  ',' functionFormalArgument	)*
	    )?
	  ')'
	  (
	  	'<'
	  	(
	  		d=typeParameterDeclaration 	
	  		(
	  			','
		  		d=typeParameterDeclaration 	
	  		)*
	  	)?
	  	'>'
	  )? 
	  ( 'implements' implementedInterface=TYPE_ID  
	  	
	  )?
			  
	  '{'
		  (
		  	  functionDeclaration		
		  	| memberValueDeclaration	
		  )*
	  '}'
	;
interfaceDeclaration 
@init {
}
	: 
	  'interface'	
	  TYPE_ID		
	  (
	  	'<'
	  	(
	  		d=typeParameterDeclaration 		
	  		( ',' d=typeParameterDeclaration )*
	  	)?
	  	'>'
	  )? 
	  ( 'implements' TYPE_ID  )?
	  '{'
	  (
	  	  functionDeclaration		
	  	| memberValueDeclaration	
	  )*
	  '}'
	;

typeParameterDeclaration 
	:
	 ( 
		  IN 	 
		| OUT	
	  )?
	  TYPE_ID	
	  ( '=' type  )?
	;


// -------------------- TYPES
	
type
	:
	  TYPE_ID						
	  (
	  	'<'
	  		type					
	  		( ',' type				)*
	  	'>'
	  )?							# simpleType0	
	| first=type '|' second=type	# unionType
	| first=type '&' second=type	# intersectionType
	| '<' type '>'					# bracketedType
	| el=type '[' ']'				# arrayType
////	| '{' type '*' '}'				{ $declaration = factory.createIterable($type.declaration); }
	;
	
	
	
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

