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
	import org.sirius.frontend.symbols.DefaultSymbolTable;
	
	import java.util.Optional;
	import java.util.HashMap;
	
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
    	| functionDeclaration 	[QName.empty]	{ $stdUnit.addFunctionDeclaration($functionDeclaration.partialList ); }
    	| classDeclaration 		[currentModule.getCurrentPackage().getQname()] { $stdUnit.addClassDeclaration($classDeclaration.declaration);	}
    	| interfaceDeclaration	[currentModule.getCurrentPackage().getQname()] { $stdUnit.addInterfaceDeclaration($interfaceDeclaration.declaration);	}
    	
    	
    	
    )*
	EOF
	;

/** CompilationUnit from script */
scriptCompilationUnit returns [ScriptCompilationUnit unit]
@init {     
	$unit = factory.createScriptCompilationUnit(currentModule);
}
	: (shebangDeclaration			{$unit.setShebang($shebangDeclaration.declaration); })?
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
		| functionDeclaration 		[currentModule.getCurrentPackage().getQname()] 
										{currentModule.addFunctionDeclaration($functionDeclaration.partialList);	}
		| classDeclaration 			[currentModule.getCurrentPackage().getQname()] {currentModule.addClassDeclaration($classDeclaration.declaration);	}
    	| interfaceDeclaration		[currentModule.getCurrentPackage().getQname()] {currentModule.addInterfaceDeclaration($interfaceDeclaration.declaration);	}
	)*
	EOF
	;

scriptCompilationUnit2 returns [ScriptCompilationUnit unit]
@init {     
}
	: (shebangDeclaration )?
    ( importDeclaration )*
	(
		  moduleDeclaration 		 
		| packageDeclaration 		
		| functionDeclaration 		[currentModule.getCurrentPackage().getQname()] 
										{currentModule.addFunctionDeclaration($functionDeclaration.partialList);	}
		| classDeclaration 			[currentModule.getCurrentPackage().getQname()] {currentModule.addClassDeclaration($classDeclaration.declaration);	}
    	| interfaceDeclaration		[currentModule.getCurrentPackage().getQname()] {currentModule.addInterfaceDeclaration($interfaceDeclaration.declaration);	}
	)*
	EOF
	;

/** CompilationUnit from module descriptor */
/*
moduleDescriptorCompilationUnit returns [ModuleDescriptor unit]
@init {
}
	: moduleDeclaration		{ $unit = factory.createModuleDescriptorCompilationUnit($moduleDeclaration.declaration);} 
	;
*/
/** CompilationUnit from package descriptor */

packageDescriptorCompilationUnit returns [PackageDescriptorCompilationUnit unit]
	: packageDeclaration 	{ $unit = factory.createPackageDescriptorCompilationUnit($packageDeclaration.declaration);}
	;



// -------------------- MODULE DECLARATION

moduleDeclaration returns [AstModuleDeclaration declaration]
@init {
	ModuleImportEquivalents importEquiv = new ModuleImportEquivalents();
	List<ModuleImport> moduleImports= new ArrayList<>();
}
	: 'module' qname version=STRING				{ /*$declaration = factory.createModuleDeclaration($qname.content, $version); */}
	  '{'
	  		( 
	  			  moduleVersionEquivalent
	  			| moduleImport 				{ moduleImports.add($moduleImport.modImport);}
	  		)*
	  '}'
	  { $declaration = factory.createModuleDeclaration($qname.content, $version, importEquiv, moduleImports); }
	;

moduleVersionEquivalent returns [AstToken key, AstToken value]
	:	
		equivKey=LOWER_ID '=' equivValue=STRING ';'	{ 
														$key = new AstToken($equivKey); 
														$value = new AstToken($equivValue);
													}
;

moduleImport returns [ModuleImport modImport]
@init {
	boolean shared = false;
	Optional<AstToken> originOpt = Optional.empty();
	Optional<QualifiedName> qnameAsQN = Optional.empty();
	Optional<String> qnameString = Optional.empty();
}
	: 
		( shared='shared' 			{ shared = true;})? 
		'import'    		 
		
					// format:  (origin:)? qname version
		(origin=STRING ':' { originOpt = Optional.of(new AstToken($origin));  })?
		
		( 	  nameQName=qname 		{ qnameAsQN = Optional.of($qname.content); } 
			| nameString=STRING 	{ qnameString = Optional.of($nameString.getText()); }
		)
		( 	  version=LOWER_ID  
			| versionString=STRING 
		)
		';'	
		{ 
			$modImport = new ModuleImport(shared, originOpt, 
				qnameAsQN.map(qn->qn.toQName()), qnameString, 
				$version, $versionString
			);
//			if($origin!=null)
//				$modImport.setOrigin($origin);
		}
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
	  (('{'
	  	  ( e=importDeclarationElement		{ $declaration.add($e.declaration); }
	  	    (',' e=importDeclarationElement	{ $declaration.add($e.declaration); })*
	  	  )?
	  '}')
	  | ';'
	  )
	;


importDeclarationElement returns [ImportDeclarationElement declaration]
	: 
	//	al=ID '=' t=ID		{ $declaration = factory.createImportDeclarationElement($t, $al); }
		importName=(LOWER_ID | TYPE_ID)				{ $declaration = factory.createImportDeclarationElement($importName); }
	|	alias=(LOWER_ID | TYPE_ID) '=' importName=(LOWER_ID | TYPE_ID)				{ $declaration = factory.createImportDeclarationElement($importName, $alias); }

	( '{'
		
	'}')?
	;

// -------------------- VALUE
memberValueDeclaration returns [AstMemberValueDeclaration declaration]
	: /*Annotations*/
		annotationList
		type
		LOWER_ID		{$declaration = factory.valueDeclaration( $annotationList.annotations, $type.declaration, $LOWER_ID);}
//		LOWER_ID		{$declaration = factory.valueDeclaration( $type.declaration, $LOWER_ID);}
		('=' expression	{$declaration.setInitialValue($expression.express); }
			
		)?
		';'
	;

// -------------------- (TOP-LEVEL ?) FUNCION
// Also maps to annotation declaration.

functionDeclaration [QName containerQName] returns [PartialList partialList]
@init {
	AstType retType;
	ArrayList<AstFunctionParameter> arguments = new ArrayList<>();
	AstFunctionDeclarationBuilder fdBuilder;
	//AstFunctionDeclaration.Builder builder;
}
	: annotationList
	  (	  returnType=type	{retType = $returnType.declaration; } 
	  	| 'void' 	{retType = new AstVoidType();}
	  )
	  name=LOWER_ID		{ 
	  	/* builder = factory. createFunctionDeclaration($annotationList.annotations, $LOWER_ID, retType, containerQName);*/
	  	fdBuilder = factory. createFunctionDeclaration($annotationList.annotations, $name, retType, containerQName);
	  }
	  (
	    '<'
	  		  	(
	  		d=typeParameterDeclaration 		{ fdBuilder = fdBuilder.withFormalParameter($d.declaration); }
	  		(
	  			','
		  		d=typeParameterDeclaration 	{ fdBuilder = fdBuilder.withFormalParameter($d.declaration); }
	  		)*
	  	)?
	  	'>'
	  )?
	  
	    
	  '('
	  	(  functionFormalArgument		{ arguments.add($functionFormalArgument.argument);}
	  	  (  ',' functionFormalArgument	{ arguments.add($functionFormalArgument.argument);} )*
	    )?								{  }
	  ')' 
	  ('{' 					{ fdBuilder.setConcrete(true); }
	  		(
	  			statement	{ fdBuilder.addStatement($statement.stmt); }
	  		)*
	   '}')?
	   { $partialList = fdBuilder.withFunctionArguments(arguments); }
	   
	;

functionFormalArgument returns [AstFunctionParameter argument]
:
	type LOWER_ID	{$argument = new AstFunctionParameter($type.declaration, new AstToken($LOWER_ID));}
;


// -------------------- STATEMENT

statement returns [AstStatement stmt]
	: returnStatement	{ $stmt = $returnStatement.stmt; }								# isReturnStatement
	| expression ';'	{ $stmt = new AstExpressionStatement($expression.express); }	# isExpressionStatement
	| localVariableStatement	{ $stmt = $localVariableStatement.lvStatement; }		# isLocalVaribleStatement
	| ifElseStatement	{ $stmt = $ifElseStatement.stmt; }								# isIfElseStatement
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
	: constantExpression { $express = $constantExpression.express ;}	# isConstantExpression
	
	| <assoc=right> left=expression op='^' right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); } # isBinaryExpression 
	
	
	| left=expression op=('*'|'/') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); } # isBinaryExpression
	| left=expression op=('+'|'-') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); } # isBinaryExpression
	
	| left=expression op=('<'|'>'|'<='|'>=') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); } # isBinaryExpression

	| left=expression op=('=='|'!=') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); } # isBinaryExpression
	| left=expression op='&&' right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); } # isBinaryExpression
	| left=expression op='||' right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); } # isBinaryExpression
	| left=expression op=('='|'+='|'-='|'*='|'/=') right=expression 	{ $express = new AstBinaryOpExpression($left.express, $right.express, $op); } # isBinaryExpression
	
	// -- Function call
	| 
		functionCallExpression 		{$express = $functionCallExpression.call; } # isFunctionCallExpression
	| 
	    thisExpr=expression '.' functionCallExpression 		{  
				$functionCallExpression.call.setThisExpression($thisExpr.express); 
				$express = $functionCallExpression.call;
		} # isMethodCallExpression
	
	
	|  // -- Constructor call
	  name=TYPE_ID '('		{ ConstructorCallExpression call = factory.createConstructorCall($name); 
	  							$express = call;
	  						}
		
		(arg0=expression 			{ call.addArgument($arg0.express); }
			( ',' arg1=expression	{ call.addArgument($arg1.express); } )*
		)?
	  ')' # isConstructorCallExpression
	| 
	  // -- Field access
	  lhs = expression '.' LOWER_ID		{
	  	AstMemberAccessExpression expr = factory.valueAccess($lhs.express, $LOWER_ID); 
	  	$express = expr;
	  }# isFieldAccessExpression
	| 
		// -- Local/member/global variable, function parameter
 		ref = LOWER_ID                                                { $express = factory.simpleReference($ref); } # isVariableRefExpression
	;

functionCallExpression returns [AstFunctionCallExpression call]
	: 
		LOWER_ID '('				{ $call = factory.functionCall($LOWER_ID); }
		(arg0=expression 			{ $call.addActualArgument($arg0.express); }
			( ',' arg1=expression	{ $call.addActualArgument($arg1.express); } )*
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
classDeclaration [QName containerQName] returns [AstClassDeclaration declaration]
@init {
//	List<Annotation> annos = new ArrayList<Annotation> ();
	boolean isInterface;
}
	: 
	  (   'class' 		{isInterface = false;}
	  	/*| 'interface'	{isInterface = true;}*/
	  )
	  TYPE_ID		{ $declaration = factory.createClassOrInterface($TYPE_ID /* , currentPackage*/, isInterface, containerQName); }
	  '('
	  	(  functionFormalArgument		{ $declaration.addAnonConstructorArgument($functionFormalArgument.argument); }
	  	  (  ',' functionFormalArgument	{ $declaration.addAnonConstructorArgument($functionFormalArgument.argument); } )*
	    )?
	  ')'
	  (
	  	'<'
	  	(
	  		d=typeParameterDeclaration 		{$declaration = $declaration.withFormalParameter($d.declaration);}
	  		(
	  			','
		  		d=typeParameterDeclaration 	{$declaration = $declaration.withFormalParameter($d.declaration);}
	  		)*
	  	)?
	  	'>'
	  )? 
	  ( 'implements' implementedInterface=TYPE_ID { $declaration.addAncestor($implementedInterface);} 
	  	
	  )?
			  
	  '{'
	  (
	  	  functionDeclaration		[$declaration.getQName()] { $declaration = $declaration.withFunctionDeclaration($functionDeclaration.partialList);}
	  	| memberValueDeclaration	{ $declaration.addValueDeclaration($memberValueDeclaration.declaration);}
	  )*
	  '}'
	;
interfaceDeclaration [QName containerQName] returns [AstInterfaceDeclaration declaration]
@init {
//	List<Annotation> annos = new ArrayList<Annotation> ();
	boolean isInterface;
}
	: 
	  ('interface'	{isInterface = true;} // TODO: no need of isInterface
	  )
	  TYPE_ID		{ $declaration = factory.createInterface($TYPE_ID, containerQName); }
	  (
	  	'<'
	  	(
	  		d=typeParameterDeclaration 		{$declaration = $declaration.withFormalParameter($d.declaration);}
	  		(
	  			','
		  		d=typeParameterDeclaration 	{$declaration = $declaration.withFormalParameter($d.declaration);}
	  		)*
	  	)?
	  	'>'
	  )? 
	  ( 'implements' TYPE_ID { $declaration.addAncestor($TYPE_ID);} 
	  	
	  )?
			  
	  '{'
	  (
	  	  functionDeclaration		[$declaration.getQName()] { $declaration = $declaration.withFunctionDeclaration($functionDeclaration.partialList);}
	  	| memberValueDeclaration	{ $declaration.addValueDeclaration($memberValueDeclaration.declaration);}
	  )*
	  '}'
	;

typeParameterDeclaration returns [TypeParameter declaration]
locals [
	Variance variance = Variance.INVARIANT;
]
	:
	 ( 
		  IN 	{$variance = Variance.IN;} 
		| OUT		{$variance = Variance.OUT;}
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
	  )?							{ $declaration = $simpleType;}								# simpleType0	
	  
	| first=type '|' second=type	{ $declaration = factory.createUnionType($first.declaration, $second.declaration); } 		# unionType
	| first=type '&' second=type	{ $declaration = factory.createIntersectionType($first.declaration, $second.declaration); }	# intersectionType
	| '<' type '>'					{ $declaration = $type.declaration; }						# bracketedType
	| el=type '[' ']'				{ $declaration = factory.createArray($el.declaration); }	# arrayType
////	| '{' type '*' '}'				{ $declaration = factory.createIterable($type.declaration); }
	;
	
	
	
	
//topDeclaration : 
//	  classDeclaration2 
//	| interfaceDeclaration2
//	;
//
//classDeclaration2 : 'class' className '{' (method)* '}';
//interfaceDeclaration2 : 'interface' className '{' (method)* '}';
//className : ID ;
//method : methodName '{' (instruction)+ '}' ;
//methodName : ID ;
//instruction : ID ;


BOOLEAN : 'true' | 'false' ;
IN : 'in';
OUT : 'out';



TYPE_ID : [A-Z][a-zA-Z0-9_]* ;	// start by uppercase

LOWER_ID : [a-z][a-zA-Z0-9_]* ;	// start by lowercase

//ALIAS_ID : [a-zA-Z][a-zA-Z0-9_]* ;

//ANNOTATION_ID : '@'[a-zA-Z][a-zA-Z0-9_]* ;

ID : [a-zA-Z][a-zA-Z0-9_]* ;

WS : [ \t\r\n]+ -> skip ;

SHEBANG : '#''!'~('\r' | '\n')*;

STRING : '"' ~('"')* '"' ;

FLOAT	: [0-9]+ '.' [0-9]+ ;
INTEGER : ('-'|'+')?[0-9]+ ;

