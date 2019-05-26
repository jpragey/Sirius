/**
 * Define a grammar called Hello
 */
grammar Sirius;

@lexer::header {
  package org.sirius.frontend.parser;
  import org.sirius.frontend.ast.AstFactory;
  import org.sirius.frontend.ast.ModuleDeclaration;
}
@parser::header {
  package org.sirius.frontend.parser;
  import org.sirius.frontend.ast.*;
  import org.sirius.frontend.ast.AstFactory;
  import org.sirius.frontend.ast.AstFactory;
    
  import java.util.Optional;
}

@members {
	//public SiriusLangPackage languagePackage; 
	public AstFactory factory;
	public ModuleDeclaration currentModule;
}


// -------------------- COMPILATION UNITS

/** Usual compilation unit */
standardCompilationUnit returns [StandardCompilationUnit stdUnit]
locals[
	PackageDeclaration currentPackage
]
@init {     
	$currentPackage = factory.createPackageDeclaration();
	$stdUnit = factory.createStandardCompilationUnit();
}
    : 						
    ( shebangDeclaration 		{ $stdUnit.setShebang($shebangDeclaration.declaration); } )?
    ( importDeclaration 		{ $stdUnit.addImport($importDeclaration.declaration);  })*
    (
    	  moduleDeclaration 	{ $stdUnit.addModuleDeclaration($moduleDeclaration.declaration);    } 
    	| packageDeclaration	{ $currentPackage = $packageDeclaration.declaration; }
    	| functionDeclaration	{ $stdUnit.addFunctionDeclaration($functionDeclaration.declaration); }
    	| classDeclaration 		{ $stdUnit.addClassDeclaration($classDeclaration.declaration); }
    )*
	EOF
	;

/** CompilationUnit from script */
scriptCompilationUnit returns [ScriptCompilationUnit unit]
@init {     
	$unit = factory.createScriptCompilationUnit();
}
	: shebangDeclaration			{$unit.setShebang($shebangDeclaration.declaration); }
	(
		  moduleDeclaration 			{$unit.addModuleDeclaration($moduleDeclaration.declaration);    } 
		| packageDeclaration 			{$unit.addPackageDeclaration($packageDeclaration.declaration);	} 
		| functionDeclaration 		{$unit.addFunctionDeclaration($functionDeclaration.declaration);	}
		| classDeclaration 			{$unit.addClassDeclaration($classDeclaration.declaration);	}
	)*
	
	;

/** CompilationUnit from module descriptor */
moduleDescriptorCompilationUnit returns [ModuleDescriptor unit]
@init {
}
	: moduleDeclaration		{ $unit = factory.createModuleDescriptorCompilationUnit($moduleDeclaration.declaration);} 
	;

/** CompilationUnit from package descriptor */
packageDescriptorCompilationUnit returns [PackageDescriptorCompilationUnit unit]
	: packageDeclaration 	{ $unit = factory.createPackageDescriptorCompilationUnit($packageDeclaration.declaration);}
	;



// -------------------- MODULE DECLARATION

moduleDeclaration returns [ModuleDeclaration declaration]
	: 'module'			{ $declaration = factory.createModuleDeclaration(); }
	  LOWER_ID			{ $declaration.addQNameElement($LOWER_ID); }
	  ( 
	  	'.' LOWER_ID 	{ $declaration.addQNameElement($LOWER_ID); }
	  )*
	  STRING			{ $declaration.setVersion($STRING); }
	  '{'
	  		(	
	  			  'value' name=LOWER_ID '=' value=STRING ';' 	{ $declaration.addValueEquivalent($name, $value); }
	  			|
	  									{ boolean shared = false; } 
	  				( 'shared' 			{ shared = true;})? 
	  				'import'    		{ ModuleDeclaration.ModuleImport mi = $declaration.addImport(shared);} 
	  				
	  							// source: package artefact version
	  				(STRING ':'			{	mi.setOrigin($STRING); })?
	  				
	  				( 	  qname 	{ mi.setGroupId($qname.content);} 
	  					| STRING 	{ mi.setGroupId($STRING); }
	  				)
	  				( 	  LOWER_ID 	{ mi.setVersionRef($LOWER_ID);} 
	  					| STRING 	{ mi.setVersion($STRING); }
	  				)
	  				';'
	  				 
	  		)*
	  '}'
	;

qname returns [QName content]
@init {
	$content = new QName();
}	
	: LOWER_ID		{ $content.add($LOWER_ID); }
	('.' LOWER_ID	{ $content.add($LOWER_ID); } )*
	;

// -------------------- SHEBANG

shebangDeclaration returns [ShebangDeclaration declaration]  
	: SHEBANG { $declaration = new ShebangDeclaration($SHEBANG); }
	;

// -------------------- IMPORTS
importDeclaration returns [ImportDeclaration declaration]
	: 'import'
	   //package
	  qname								{ $declaration = factory.createImportDeclaration($qname.content); }
	  '{'
	  	e=importDeclarationElement		{ $declaration.add($e.declaration); }
	  	(',' e=importDeclarationElement	{ $declaration.add($e.declaration); })*
	  '}'
	;


importDeclarationElement returns [ImportDeclarationElement declaration]
	:									{ Optional<Token> alias = Optional.empty();}		
	  		(al=TYPE_ID '=' 			{ alias = Optional.of($al);} )? 
	  		t=TYPE_ID 					{ $declaration = factory.createImportDeclarationElement($t, alias); }
	  		( '{'
	  			
	  		'}')?
	;

// -------------------- (TOP-LEVEL ?) FUNCION

functionDeclaration returns [FunctionDeclaration declaration]
	: type
	  LOWER_ID		{ $declaration = factory. createFunctionDeclaration($LOWER_ID, $type.declaration); }
	  (
	    '<'
	  		  	(
	  		d=typeFormalParameterDeclaration 		{$declaration.addTypeParameterDeclaration($d.declaration);}
	  		(
	  			','
		  		d=typeFormalParameterDeclaration 	{$declaration.addTypeParameterDeclaration($d.declaration);}
	  		)*
	  	)?
	  	'>'
	  )?
	  
	    
	  '('
	  	(  functionFormalArgument		{ $declaration.addFormalArgument($functionFormalArgument.argument); }
	  	  (  ',' functionFormalArgument	{ $declaration.addFormalArgument($functionFormalArgument.argument); } )*
	    )?
	  ')' 
	  '{' 
	  		(
	  			statement	{ $declaration.addStatement($statement.stmt); }
	  		)*
	   '}'
	;

functionFormalArgument returns [FunctionFormalArgument argument]
:
	type LOWER_ID	{$argument = new FunctionFormalArgument($type.declaration, $LOWER_ID);}
;


// -------------------- STATEMENT

statement returns [Statement stmt]
	: returnStatement	{ $stmt = $returnStatement.stmt; }
	;

returnStatement returns [ReturnStatement stmt]
	: 'return' expression { $stmt = new ReturnStatement($expression.express); }
	  ';'
	; 

// -------------------- EXPRESSION

expression returns [Expression express]
	: constantExpression { $express = $constantExpression.express ;}
	
	| left=expression op=('+'|'-') right=expression 	{ $express = new BinaryOpExpression($left.express, $right.express, $op); }
	| left=expression op=('*'|'/') right=expression 	{ $express = new BinaryOpExpression($left.express, $right.express, $op); }
	// Function call
	| LOWER_ID '('				{ FunctionCallExpression call = new FunctionCallExpression($LOWER_ID); $express = call;}
		(expression 			{ call.addActualArgument($expression.express); }
			( ',' expression	{ call.addActualArgument($expression.express); } )*
		)?
	  ')'
	
	;

constantExpression returns [Expression express]
	: STRING	{ $express = factory.stringConstant($STRING); }
	| INTEGER	{ $express = factory.integerConstant($INTEGER); }
	| FLOAT		{ $express = factory.floatConstant($FLOAT); }
	| BOOLEAN	{ $express = factory.booleanConstant($BOOLEAN); }
	;


//
//// -------------------- ANNOTATION
//
//annotation returns [Annotation anno]
//	: LOWER_ID	{ $anno = new Annotation($LOWER_ID); }
//	(
//		'(' ')'
//	)?
//	;
//
//
// -------------------- PACKAGE 

packageDeclaration returns [PackageDeclaration declaration]
@init {
	$declaration = factory.createPackageDeclaration();
}
	: 'package'
		LOWER_ID			{ $declaration.addNamePart($LOWER_ID);}
		('.' LOWER_ID	{ $declaration.addNamePart($LOWER_ID);})*
	  ';'
	;
	

//// -------------------- TYPES
//type0 returns [TypeDeclaration typeval]
//	: TYPE_ID //{$typeval = new SimpleType($TYPE_ID);} 
//	;

// -------------------- CLASS 
//
classDeclaration /*[PackageDeclaration currentPackage]*/ returns [ClassDeclaration declaration]
@init {
//	List<Annotation> annos = new ArrayList<Annotation> ();
}
	: 
//	  (annotation {annos.add($annotation.anno); } )*
	  'class'
	  TYPE_ID		{ $declaration = factory.createClassDeclaration($TYPE_ID /* , currentPackage*/); }
	  '('
	  	(  functionFormalArgument		{ $declaration.addAnonConstructorArgument($functionFormalArgument.argument); }
	  	  (  ',' functionFormalArgument	{ $declaration.addAnonConstructorArgument($functionFormalArgument.argument); } )*
	    )?
	  ')'
	  (
	  	'<'
	  	(
	  		d=typeFormalParameterDeclaration 		{$declaration.addTypeParameterDeclaration($d.declaration);}
	  		(
	  			','
		  		d=typeFormalParameterDeclaration 	{$declaration.addTypeParameterDeclaration($d.declaration);}
	  		)*
	  	)?
	  	'>'
	  )?
	  
	  '{'
	  (
	  	functionDeclaration { $declaration.addFunctionDeclaration($functionDeclaration.declaration);}
	  )*
	  '}'
	;

typeFormalParameterDeclaration returns [TypeFormalParameterDeclaration declaration]
locals [
	Variance variance = Variance.INVARIANT;
]
	:
	 ( 
		 'in'   	{$variance = Variance.IN;} 
		|'out'		{$variance = Variance.OUT;}
	  )?
	  TYPE_ID		{$declaration = factory.createTypeFormalParameter($variance, $TYPE_ID); }
	  (
	  	'=' type
	  )?
	;


// -------------------- TYPES
	
	
type returns [Type declaration]
locals [
	SimpleType simpleType = null; 
]
	:
	  TYPE_ID						{ $simpleType = factory.createSimpleType($TYPE_ID); }
	  (
	  	'<'
	  		type					{ $simpleType.appliedParameter($type.declaration);}
	  		( ',' type				{ $simpleType.appliedParameter($type.declaration);} )*
	  	'>'
	  )?							{ $declaration = $simpleType;}
	  
	| first=type '|' second=type	{ $declaration = factory.createUnionType($first.declaration, $second.declaration); }
	| first=type '&' second=type	{ $declaration = factory.createIntersectionType($first.declaration, $second.declaration); }
	| '<' type '>'					{ $declaration = $type.declaration; }
////	| '{' type '*' '}'				{ $declaration = factory.createIterable($type.declaration); }
	;
	
	
	
	





TYPE_ID : [A-Z][a-zA-Z0-9_]* ;	// start by uppercase

LOWER_ID : [a-z][a-zA-Z0-9_]* ;	// start by lowercase

//ANNOTATION_ID : '@'[a-zA-Z][a-zA-Z0-9_]* ;

ID : [a-zA-Z][a-zA-Z0-9_]* ;

WS : [ \t\r\n]+ -> skip ;

SHEBANG : '#''!'~('\r' | '\n')*;

STRING : '"' ~('"')* '"' ;

FLOAT	: [0-9]+ '.' [0-9]+ ;
INTEGER : [0-9]+ ;
BOOLEAN : 'true' | 'false' ;

