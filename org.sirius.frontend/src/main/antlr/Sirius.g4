/**
 * Define a grammar called Hello
 */
grammar Sirius;

@lexer::header {
	package org.sirius.frontend.parser;
	import org.sirius.frontend.ast.AstFactory;
	import org.sirius.frontend.ast.AstModuleDeclaration;
}
@parser::header {
	package org.sirius.frontend.parser;
	import org.sirius.frontend.ast.*;
	import org.sirius.common.core.QName;
	
	import java.util.Optional;
}

@members {
	public AstFactory factory;
	public AstModuleDeclaration currentModule;
}


// -------------------- COMPILATION UNITS

/** Usual compilation unit */
standardCompilationUnit returns [StandardCompilationUnit stdUnit]
locals[
]
@init {     
/*	currentPackage = factory.createPackageDeclaration();*/
	$stdUnit = factory.createStandardCompilationUnit();
}
    : 						
    ( importDeclaration 		{ $stdUnit.addImport($importDeclaration.declaration);  })*
    (
    	  moduleDeclaration 	{ $stdUnit.addModuleDeclaration($moduleDeclaration.declaration);    } 
    	| packageDeclaration	{ currentModule.addPackageDeclaration($packageDeclaration.declaration);}
    	| functionDeclaration	{ $stdUnit.addFunctionDeclaration($functionDeclaration.declaration); }
    	| classDeclaration 		{ $stdUnit.addClassDeclaration($classDeclaration.declaration);	}
    	| interfaceDeclaration	{ $stdUnit.addInterfaceDeclaration($interfaceDeclaration.declaration);	}
    	
    	
    	
    )*
	EOF
	;

/** CompilationUnit from script */
scriptCompilationUnit returns [ScriptCompilationUnit unit]
@init {     
	$unit = factory.createScriptCompilationUnit(currentModule);
}
	: shebangDeclaration			{$unit.setShebang($shebangDeclaration.declaration); }
    ( importDeclaration 			{$unit.addImport($importDeclaration.declaration);  })*
	(
		  moduleDeclaration 		{
		  								$unit.addModuleDeclaration($moduleDeclaration.declaration);
		  								currentModule = $moduleDeclaration.declaration;
		  							} 
		| packageDeclaration 		{
										//$unit.addPackageDeclaration($packageDeclaration.declaration);
										currentModule.addPackageDeclaration($packageDeclaration.declaration);
										//scriptCurrentState.getCurrentModule().addPackageDeclaration($packageDeclaration.declaration);

									} 
		| functionDeclaration 		{currentModule.addFunctionDeclaration($functionDeclaration.declaration);	}
		| classDeclaration 			{currentModule.addClassDeclaration($classDeclaration.declaration);	}
    	| interfaceDeclaration		{currentModule.addInterfaceDeclaration($interfaceDeclaration.declaration);	}
	)*
	EOF
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

moduleDeclaration returns [AstModuleDeclaration declaration]
	: 'module' qname STRING				{ $declaration = factory.createModuleDeclaration($qname.content, $STRING); }
	  '{'
	  		(	
	  			  'value' name=LOWER_ID '=' value=STRING ';' 	{ $declaration.addValueEquivalent($name, $value); }
	  			|
	  									{ boolean shared = false; } 
	  				( 'shared' 			{ shared = true;})? 
	  				'import'    		{ AstModuleDeclaration.ModuleImport mi = $declaration.addImport(shared);} 
	  				
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

qname returns [QualifiedName content]
@init {
//	$content = new QualifiedName();
	List<AstToken> elements = new ArrayList<>();
}	
	: LOWER_ID		{ /*$content.add($LOWER_ID); */elements.add(new AstToken($LOWER_ID));}
	('.' LOWER_ID	{ /*$content.add($LOWER_ID); */elements.add(new AstToken($LOWER_ID));} )*
	{$content = new QualifiedName(elements);}
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
	  	  ( e=importDeclarationElement		{ $declaration.add($e.declaration); }
	  	    (',' e=importDeclarationElement	{ $declaration.add($e.declaration); })*
	  	  )?
	  '}'
	;


importDeclarationElement returns [ImportDeclarationElement declaration]
	: 
	//	al=ID '=' t=ID		{ $declaration = factory.createImportDeclarationElement($t, $al); }
		t=(LOWER_ID | TYPE_ID)				{ $declaration = factory.createImportDeclarationElement($t); }
	|	al=(LOWER_ID | TYPE_ID) '=' t=(LOWER_ID | TYPE_ID)				{ $declaration = factory.createImportDeclarationElement($t, $al); }

	( '{'
		
	'}')?
	;

// -------------------- VALUE
memberValueDeclaration returns [AstMemberValueDeclaration declaration]
	: /*Annotations*/
		annotationList
		type
		LOWER_ID		{$declaration = factory.valueDeclaration($annotationList.annotations, $type.declaration, $LOWER_ID);}
		('=' expression	{$declaration.setInitialValue($expression.express); }
			
		)?
		';'
	;

// -------------------- (TOP-LEVEL ?) FUNCION
// Also maps to annotation declaration.

functionDeclaration returns [AstFunctionDeclaration declaration]
@init {
	AstType retType;
}
	: annotationList
	  (	  rt=type	{retType = $rt.declaration; } 
	  	| 'void' 	{retType = new AstVoidType();}
	  )
	  LOWER_ID		{ $declaration = factory. createFunctionDeclaration($annotationList.annotations, $LOWER_ID, retType, false /*concrete*/, false /*member*/); }
	  (
	    '<'
	  		  	(
	  		d=typeFormalParameterDeclaration 		{ $declaration = $declaration.withFormalParameter($d.declaration); }
	  		(
	  			','
		  		d=typeFormalParameterDeclaration 	{ $declaration = $declaration.withFormalParameter($d.declaration); }
	  		)*
	  	)?
	  	'>'
	  )?
	  
	    
	  '('
	  	(  functionFormalArgument		{ $declaration = $declaration.withFunctionArgument($functionFormalArgument.argument); }
	  	  (  ',' functionFormalArgument	{ $declaration = $declaration.withFunctionArgument($functionFormalArgument.argument); } )*
	    )?
	  ')' 
	  '{' 					{ $declaration.setConcrete(true); }
	  		(
	  			statement	{ $declaration.addStatement($statement.stmt); }
	  		)*
	   '}'
	;

functionFormalArgument returns [AstFunctionFormalArgument argument]
:
	type LOWER_ID	{$argument = new AstFunctionFormalArgument($type.declaration, new AstToken($LOWER_ID));}
;


// -------------------- STATEMENT

statement returns [AstStatement stmt]
	: returnStatement	{ $stmt = $returnStatement.stmt; }
	| expression ';'	{ $stmt = new AstExpressionStatement($expression.express); }
	| localVariableStatement	{ $stmt = $localVariableStatement.lvStatement; }
	| ifElseStatement	{ $stmt = $ifElseStatement.stmt; }
	;

returnStatement returns [AstReturnStatement stmt]
	: 'return' expression ';' { $stmt = new AstReturnStatement($expression.express); }
	; 

localVariableStatement returns [AstLocalVariableStatement lvStatement]
	: /*Annotations*/
		annotationList
		type
		LOWER_ID		{$lvStatement = factory.localVariableStatement($annotationList.annotations, $type.declaration, $LOWER_ID);}
		('=' expression	{$lvStatement.setInitialValue($expression.express); }
			
		)?
		';'
	;

ifElseStatement returns [AstIfElseStatement stmt] 
@init {
	AstExpression ifExpression;
	//, AstBlock ifBlock, Optional<AstBlock> elseBlock
}
	: 'if' '(' ifExpression = expression ')'
		ifBlock = statement {$stmt = factory.ifElseStatement($ifExpression.express, $ifBlock.stmt);}
		(
			'else' elseBlock = statement {$stmt = $stmt.withElse($elseBlock.stmt); }
		)?
	;

// -------------------- EXPRESSION

expression returns [AstExpression express]
	: constantExpression { $express = $constantExpression.express ;}
	
	| <assoc=right> left=expression op='^' right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); }// TODO
	
	
	| left=expression op=('*'|'/') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); }
	| left=expression op=('+'|'-') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); }
	
	/* TODO: && || ! == != < > >= <=
	*/
	| left=expression op=('<'|'>'|'<='|'>=') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); }

	| left=expression op=('=='|'!=') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); }
	| left=expression op='&&' right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); }
	| left=expression op='||' right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); }
	| left=expression op=('='|'+='|'-='|'*='|'/=') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); }
	// TODO - end
	
	// -- Function call
	| 
		functionCallExpression 		{$express = $functionCallExpression.call; }
	| expression '.' functionCallExpression 		{  $functionCallExpression.call.setThisExpression($expression.express); $express = $functionCallExpression.call; }
	
	
	|	// -- Constructor call
	  TYPE_ID '('					{ ConstructorCallExpression call = factory.createConstructorCall($TYPE_ID); $express = call; }
		
		(arg=expression 			{ call.addArgument($arg.express); }
			( ',' arg=expression	{ call.addArgument($arg.express); } )*
		)?
	  ')'
	| // -- Field access
	  lhs = expression '.' LOWER_ID		{
	  	AstMemberAccessExpression expr = factory.valueAccess($lhs.express, $LOWER_ID); 
	  	$express = expr;
	  }
	| // -- Local/member/global variable, function parameter
	  ref = LOWER_ID						{ $express = factory.simpleReference($ref); }
	;

functionCallExpression returns [AstFunctionCallExpression call]
	: 
		LOWER_ID '('				{ $call = factory.functionCall($LOWER_ID); }
		(arg=expression 			{ $call.addActualArgument($arg.express); }
			( ',' arg=expression	{ $call.addActualArgument($arg.express); } )*
		)?
	  ')'
	;






constantExpression returns [AstExpression express]
	: STRING	{ $express = factory.stringConstant($STRING); }
	| INTEGER	{ $express = factory.integerConstant($INTEGER); }
	| FLOAT		{ $express = factory.floatConstant($FLOAT); }
	| BOOLEAN	{ $express = factory.booleanConstant($BOOLEAN); }
	;



// -------------------- ANNOTATION

annotation returns [Annotation anno]
	: LOWER_ID	{ $anno = factory.annotation($LOWER_ID); }
	(
		'(' ')'
	)?
	;

annotationList returns [AnnotationList annotations]
@init {
	$annotations = new AnnotationList();
}
	:
	( annotation 		{$annotations.addAnnotation($annotation.anno ); })*
	;


// -------------------- PACKAGE 

packageDeclaration returns [AstPackageDeclaration declaration]
@init {
//	$declaration = factory.createPackageDeclaration();
}
	: 'package' qname ';'	  {$declaration = factory.createPackageDeclaration($qname.content);}
	;
	

//// -------------------- TYPES
//type0 returns [TypeDeclaration typeval]
//	: TYPE_ID //{$typeval = new SimpleType($TYPE_ID);} 
//	;

// -------------------- CLASS 
//
classDeclaration returns [AstClassDeclaration declaration]
@init {
//	List<Annotation> annos = new ArrayList<Annotation> ();
	boolean isInterface;
}
	: 
	  (   'class' 		{isInterface = false;}
	  	/*| 'interface'	{isInterface = true;}*/
	  )
	  TYPE_ID		{ $declaration = factory.createClassOrInterface($TYPE_ID /* , currentPackage*/, isInterface); }
	  '('
	  	(  functionFormalArgument		{ $declaration.addAnonConstructorArgument($functionFormalArgument.argument); }
	  	  (  ',' functionFormalArgument	{ $declaration.addAnonConstructorArgument($functionFormalArgument.argument); } )*
	    )?
	  ')'
	  (
	  	'<'
	  	(
	  		d=typeFormalParameterDeclaration 		{$declaration = $declaration.withFormalParameter($d.declaration);}
	  		(
	  			','
		  		d=typeFormalParameterDeclaration 	{$declaration = $declaration.withFormalParameter($d.declaration);}
	  		)*
	  	)?
	  	'>'
	  )? 
	  ( 'implements' TYPE_ID { $declaration.addAncestor($TYPE_ID);} 
	  	
	  )?
			  
	  '{'
	  (
	  	  functionDeclaration		{ $declaration = $declaration.withFunctionDeclaration($functionDeclaration.declaration);}
	  	| memberValueDeclaration	{ $declaration.addValueDeclaration($memberValueDeclaration.declaration);}
	  )*
	  '}'
	;
interfaceDeclaration returns [AstInterfaceDeclaration declaration]
@init {
//	List<Annotation> annos = new ArrayList<Annotation> ();
	boolean isInterface;
}
	: 
	  ('interface'	{isInterface = true;} // TODO: no need of isInterface
	  )
	  TYPE_ID		{ $declaration = factory.createInterface($TYPE_ID); }
	  (
	  	'<'
	  	(
	  		d=typeFormalParameterDeclaration 		{$declaration = $declaration.withFormalParameter($d.declaration);}
	  		(
	  			','
		  		d=typeFormalParameterDeclaration 	{$declaration = $declaration.withFormalParameter($d.declaration);}
	  		)*
	  	)?
	  	'>'
	  )? 
	  ( 'implements' TYPE_ID { $declaration.addAncestor($TYPE_ID);} 
	  	
	  )?
			  
	  '{'
	  (
	  	  functionDeclaration		{ $declaration = $declaration.withFunctionDeclaration($functionDeclaration.declaration);}
	  	| memberValueDeclaration	{ $declaration.addValueDeclaration($memberValueDeclaration.declaration);}
	  )*
	  '}'
	;

typeFormalParameterDeclaration returns [TypeParameter declaration]
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
	
	
type returns [AstType declaration]
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
	| el=type '[' ']'					{ $declaration = factory.createArray($el.declaration); }
////	| '{' type '*' '}'				{ $declaration = factory.createIterable($type.declaration); }
	;
	
	
	




BOOLEAN : 'true' | 'false' ;

TYPE_ID : [A-Z][a-zA-Z0-9_]* ;	// start by uppercase

LOWER_ID : [a-z][a-zA-Z0-9_]* ;	// start by lowercase

//ALIAS_ID : [a-zA-Z][a-zA-Z0-9_]* ;

//ANNOTATION_ID : '@'[a-zA-Z][a-zA-Z0-9_]* ;

ID : [a-zA-Z][a-zA-Z0-9_]* ;

WS : [ \t\r\n]+ -> skip ;

SHEBANG : '#''!'~('\r' | '\n')*;

STRING : '"' ~('"')* '"' ;

FLOAT	: [0-9]+ '.' [0-9]+ ;
INTEGER : [0-9]+ ;

