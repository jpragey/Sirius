/**
 * Define a grammar called Hello
 */
grammar Sirius;


import SLexer;

//@lexer::header {
////package org.sirius.frontend.parser;
//// 	import org.sirius.frontend.ast.AstModuleDeclaration;
//}
@parser::header {
package org.sirius.frontend.parser;
// 	import org.sirius.frontend.ast.*;
}

@members {
}


// -------------------- COMPILATION UNITS

/** New  */
newCompilationUnit :
//	( importDeclaration )*
	packageContent ?	// unnamed module / unnamed package content
	(
		moduleDeclaration
		packageContent ?	// named module / unnamed package content
	)*
		;

packageContent : 
    	  functionDeclaration
    	| functionDefinition 	
    	| classDeclaration 		
    	| interfaceDeclaration	
	;
	
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
	| functionDefinition 		
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
		name=LOWER_ID		
		('=' expression	
			
		)?
		';'
	;

// -------------------- FUNCION (top-level or member)

functionDeclaration 
	: annotationList
	  (	  returnType=type	 
	  	| 'void' 	
	  )
	  name=LOWER_ID		
	  (
	  	typeParameterDeclarationList
	  )?
	  '('
		  functionDefinitionParameterList
	  ')' 
	;
// Function definition (with body and named arguments)	
functionDefinition 
	: annotationList
	  (	  returnType=type	 
	  	| 'void' 	
	  )
	  name=LOWER_ID		
	  (
	  	typeParameterDeclarationList
	  )?
	  '('
		  functionDefinitionParameterList
	  ')' 
	  ( functionBody )
//	  ( functionBody )	// TODO: ??? should be optional
	;
	
functionDefinitionParameterList
	:   ( functionDefinitionParameter		
	  	  (  ',' functionDefinitionParameter	)*
	    )?
	;

functionDeclarationParameterList
	:   ( functionDeclarationParameter		
	  	  (  ',' functionDeclarationParameter	)*
	    )?
	;

typeParameterDeclarationList
	:
	'<'	
	 ( 		typeParameterDeclaration 		
	  		( ',' typeParameterDeclaration )*
	 )?
	 '>'
	;
	
functionBody
	:'{' 					
	  		( statement )*
	 '}'
	;

functionDefinitionParameter :
	type LOWER_ID
;

functionDeclarationParameter :
	type ( LOWER_ID ?)
;

// -------------------- LAMBDAS

// lamda declaration: parameter names are optional, return type is mandatory, no body
lambdaDeclaration :
	// []
	'('  functionDeclarationParameterList ')'
	 '->' returnType=type 
	 
	 | // Temp., to be replaced by template class
	 	'Function' '<' returnType=type ',' '[' functionDeclarationParameterList ']' '>'
	; 
	

lambdaDefinition 
	: 
		// annotationList // ???
//	  (	  returnType=type	 
//	  	| 'void' 	
//	  )
	  // ( '<' ( d=typeParameterDeclaration ( ',' d=typeParameterDeclaration )* )? '>')? // ???
	  '(' functionDefinitionParameterList ')'
	  
	  (':' (	  returnType=type	 
	  	| 'void' 	
	  ) )?
	  
	   
	  functionBody
	;

lambdaFormalArgument 
:
	type LOWER_ID
;

// -------------------- STATEMENT

statement 
	: returnStatement			# isReturnStatement
	| expression ';'			# isExpressionStatement
	| localVariableStatement	# isLocalVaribleStatement
	| ifElseStatement			# isIfElseStatement
	| blockStatement			# isBlockStatement
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
	: 'if' '(' ifExpression = expression ')'
		ifBlock = statement 
		(
			'else' elseBlock = statement 
		)?
	;

blockStatement
	: '{'
	(statement *)
	 '}'
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
	| lambdaDefinition 		# isLambdaDefinition
	| 
	    thisExpr=expression '.' functionCallExpression 		# isMethodCallExpression
	
	|  // -- Constructor call
	   classInstanciationExpression		# isConstructorCallExpression
	| 
	  // -- Field access
	  lhs = expression '.' LOWER_ID		# isFieldAccessExpression
	| 
		// -- Local/member/global variable, function parameter
 		ref = LOWER_ID                  # isVariableRefExpression
	;

classInstanciationExpression : 
	name=TYPE_ID '('		
		
		(arg0=expression 	
			( ',' arg1=expression)*
		)?
	  ')'	;

functionCallExpression 
	: 
		LOWER_ID '('	
		(arg0=expression
			( ',' arg1=expression )*
		)?
	  ')'
	;


constantExpression //returns [AstExpression express]
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
	  className=TYPE_ID		
	  '('
			functionDefinitionParameterList
	  ')'
	  ( typeParameterDeclarationList )?
	  implementedInterfaces ? 
			  
	  '{'
		  (
		  	  functionDeclaration		
		  	| functionDefinition		
		  	| memberValueDeclaration	
		  )*
	  '}'
	;
	
 	
implementedInterfaces :	 // 'implements' clause, for classes and interfaces
	  'implements' TYPE_ID
	  	(',' TYPE_ID)*
;
	
interfaceDeclaration 
	: 
	  'interface'	
	  interfaceName=TYPE_ID		
	  (
	  	typeParameterDeclarationList
	  )? 
	  implementedInterfaces ? 
	  '{'
	  (
	  	  functionDeclaration		
	  	| functionDefinition		
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
	| lambdaDeclaration				# lambdaType
////	| '{' type '*' '}'				{ $declaration = factory.createIterable($type.declaration); }
	;
