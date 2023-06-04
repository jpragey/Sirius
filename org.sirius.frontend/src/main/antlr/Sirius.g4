/**
 * Define a grammar called Hello
 */
parser grammar Sirius;


options {
	tokenVocab = SLexer;
}

@header {
package org.sirius.frontend.parser;
}
//@parser::header {
////package org.sirius.frontend.parser;
//package org.sirius.frontend.grammar;
//
//// 	import org.sirius.frontend.ast.*;
//}

@members {
}


// -------------------- COMPILATION UNITS

/** New  */
newCompilationUnit :
	shebangDeclaration ?
	
	// unnamed module content
//// /*TODO*/	packageTopLevelDeclarations
	
	// explicit module declarations
	newModuleDeclaration *
;

newModuleDeclaration :
	moduleHeader 
	( 
	  ';' packageTopLevelDeclarations
	| '{' packageTopLevelDeclarations '}'
	)
	;
	

moduleHeader :
	'module' qname '{' '}'
	;

packageTopLevelDeclarations : 
	importDeclaration *
	packageTopLevelDeclaration *
	;
	
packageTopLevelDeclaration : 
    	  functionDeclaration
    	| functionDefinition 	
    	| classDeclaration 		
	;

/////////////////
packageContent : 
    	  functionDeclaration
    	| functionDefinition 	
    	| classDeclaration 		
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
//	| interfaceDeclaration		
	; 

packageDescriptorCompilationUnit 
	: packageDeclaration 	
	;

moduleDescriptorCompilationUnit 
	: moduleDeclaration 	
	;

// -------------------- MODULE DECLARATION

moduleDeclaration 
	: MODULE /*'module'*/ qname version=STRING				{}
	  '{'
	  		( 
	  			  moduleVersionEquivalent
	  			| moduleImport 				
	  		)*
	  '}'
	;

moduleVersionEquivalent 
	: key=LOWER_ID EQUAL value=STRING ';'	
	;

moduleImport
	: 
		( shared= SHARED /*  'shared'*/ 			)? 
		IMPORT    		 
		
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
	(DOT LOWER_ID	)*
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
	  	    (COMA e=importDeclarationElement	)*
	  	  )?
	  '}')
	  | ';'
	  )
	;


importDeclarationElement 
	: 	importName=(LOWER_ID | TYPE_ID)
	|	alias=(LOWER_ID | TYPE_ID) EQUAL importName=(LOWER_ID | TYPE_ID)
	    ( '{' '}')?
	;

// -------------------- VALUE
memberValueDeclaration 
	:
		annotationList
		type
		name=LOWER_ID		
		(EQUAL expression	
			
		)?
		';'
	;

// -------------------- FUNCION (top-level or member)

functionDeclaration 
	: annotationList
	  (	  returnType=type	 
	  	| VOID 	
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
	  	| VOID
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
	  	  (  COMA functionDefinitionParameter	)*
	    )?
	;

functionDeclarationParameterList
	:   ( functionDeclarationParameter		
	  	  (  COMA functionDeclarationParameter	)*
	    )?
	;

typeParameterDeclarationList
	:
	LT	
	 ( 		typeParameterDeclaration 		
	  		( COMA typeParameterDeclaration )*
	 )?
	 GT
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
	 ARROW returnType=type 
	 
	 | // Temp., to be replaced by template class
	 	FUNCTION LT returnType=type COMA LBRACK functionDeclarationParameterList RBRACK GT
	; 
	

lambdaDefinition 
	: 
		// annotationList // ???
//	  (	  returnType=type	 
//	  	| 'void' 	
//	  )
	  // ( LT ( d=typeParameterDeclaration ( COMA d=typeParameterDeclaration )* )? GT)? // ???
	  '(' functionDefinitionParameterList ')'
	  
	  (':' (	  returnType=type	 
	  	| VOID
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
	: RETURN expression ';' 
	; 

localVariableStatement 
	: 
		annotationList
		type
		LOWER_ID		
		(EQUAL expression	
			
		)?
		';'
	;

ifElseStatement  
	: IF '(' ifExpression = expression ')'
		ifBlock = statement 
		(
			ELSE elseBlock = statement 
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
	
	| <assoc=right> left=expression op=XOR right=expression # isBinaryExpression 
	
	| left=expression op=( STAR | SLASH ) right=expression 	# isBinaryExpression
	| left=expression op=( PLUS | MINUS ) right=expression 	# isBinaryExpression
	
	| left=expression op=(LT | GT | LOWER_EQUAL | GREATER_EQUAL ) right=expression 	# isBinaryExpression

	| left=expression op=( EQUAL_EQUAL | NOT_EQUAL ) right=expression 	# isBinaryExpression
	| left=expression op=AND_AND right=expression 	# isBinaryExpression
	| left=expression op=OR_OR right=expression 	# isBinaryExpression
	| left=expression op=( EQUAL | PLUS_EQUAL | MINUS_EQUAL | MUL_EQUAL | DIV_EQUAL ) right=expression 	# isBinaryExpression
	
	// -- Function call
	| 
	  functionCallExpression 		# isFunctionCallExpression
	| lambdaDefinition 		# isLambdaDefinition
	| 
	    thisExpr=expression DOT functionCallExpression 		# isMethodCallExpression
	
	|  // -- Constructor call
	   classInstanciationExpression		# isConstructorCallExpression
	| 
	  // -- Field access
	  lhs = expression DOT LOWER_ID		# isFieldAccessExpression
	| 
		// -- Local/member/global variable, function parameter
 		ref = LOWER_ID                  # isVariableRefExpression
	;

classInstanciationExpression : 
	name=TYPE_ID '('		
		
		(arg0=expression 	
			( COMA arg1=expression)*
		)?
	  ')'	;

functionCallExpression 
	: 
		LOWER_ID '('	
		(arg0=expression
			( COMA arg1=expression )*
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
	: PACKAGE /*'package'*/ qname ';'
	packageElement *
	;
	
// -------------------- CLASS 
//
classDeclaration 
	: CLASS
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
	  IMPLEMENTS TYPE_ID
	  	(COMA TYPE_ID)*
;

	/*
interfaceDeclaration 
	: 
	  INTERFACE	
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
*/

typeParameterDeclaration 
	:
	 ( 
		  IN 	 
		| OUT	
	  )?
	  TYPE_ID	
	  ( EQUAL type  )?
	;


// -------------------- TYPES
	
type
	:
	  TYPE_ID						
	  (
	  	LT
	  		type					
	  		( COMA type				)*
	  	GT
	  )?							# simpleType0	
	| first=type  OR  second=type	# unionType
	| first=type AMPERSAND second=type	# intersectionType
	| LT type GT					# bracketedType
	| el=type LBRACK RBRACK				# arrayType
	| lambdaDeclaration				# lambdaType
////	| '{' type '*' '}'				{ $declaration = factory.createIterable($type.declaration); }
	;
