package org.sirius.frontend.ast;

public interface AstVisitor {

	public default void startModuleDeclaration (AstModuleDeclaration declaration) {}
	public default void endModuleDeclaration (AstModuleDeclaration declaration) {}

	public default void startCompilationUnit (StandardCompilationUnit compilationUnit) {}
	public default void endCompilationUnit (StandardCompilationUnit compilationUnit) {}

	public default void startScriptCompilationUnit (ScriptCompilationUnit compilationUnit) {}
	public default void endScriptCompilationUnit (ScriptCompilationUnit compilationUnit) {}

	public default void startModuleDescriptorCompilationUnit (ModuleDescriptor compilationUnit) {}
	public default void endModuleDescriptorCompilationUnit (ModuleDescriptor compilationUnit) {}

	public default void startPackageDescriptorCompilationUnit (PackageDescriptorCompilationUnit compilationUnit) {}
	public default void endPackageDescriptorCompilationUnit(PackageDescriptorCompilationUnit compilationUnit) {}

	public default void startImportDeclaration	(ImportDeclaration importDeclaration) {}
	public default void endImportDeclaration	(ImportDeclaration importDeclaration) {}


	
	
	
	public default void startShebangDeclaration (ShebangDeclaration declaration) {}
	public default void endShebangDeclaration (ShebangDeclaration declaration) {}

	public default void startPackageDeclaration (AstPackageDeclaration declaration) {}
	public default void endPackageDeclaration (AstPackageDeclaration declaration) {}

	
	public default void startClassDeclaration (AstClassDeclaration classDeclaration) {}
	public default void endClassDeclaration (AstClassDeclaration classDeclaration) {}
	
	public default void startInterfaceDeclaration (AstInterfaceDeclaration interfaceDeclaration) {}
	public default void endInterfaceDeclaration (AstInterfaceDeclaration interfaceDeclaration) {}
	
	
	
	
	
	public default void startFunctionDeclaration (AstFunctionDeclaration functionDeclaration) {}
	public default void endFunctionDeclaration (AstFunctionDeclaration functionDeclaration) {}
	
	public default void startFunctionFormalArgument (AstFunctionFormalArgument formalArgument) {}
	public default void endFunctionFormalArgument   (AstFunctionFormalArgument formalArgument) {}
	
	
	
	public default void startValueDeclaration (AstMemberValueDeclaration valueDeclaration) {}
	public default void endValueDeclaration (AstMemberValueDeclaration valueDeclaration) {}
	
//	public default void startSimpleType (SimpleType simpleType) {}
//	public default void endSimpleType (SimpleType simpleType) {}
	
	// -- Expressions
	public default void startExpression (AstExpression expression) {}
	public default void endExpression (AstExpression expression) {}
	
	public default void startBinaryOpExpression (AstBinaryOpExpression expression) {}
	public default void endBinaryOpExpression (AstBinaryOpExpression expression) {}
	
	public default void startFunctionCallExpression (AstFunctionCallExpression expression) {}
	public default void endFunctionCallExpression (AstFunctionCallExpression expression) {}
	
	public default void startConstructorCallExpression (ConstructorCallExpression expression) {}
	public default void endConstructorCallExpression (ConstructorCallExpression expression) {}
	
	public default void startFieldAccess (AstMemberAccessExpression expression) {}
	public default void endFieldAccess (AstMemberAccessExpression expression) {}
	
	public default void startSimpleReferenceExpression(SimpleReferenceExpression expression) {}
	public default void endSimpleReferenceExpression(SimpleReferenceExpression expression) {}
	
	
	
//	public default void startConstantExpression (ConstantExpression expression) {}
//	public default void endConstantExpression (ConstantExpression expression) {}
	
	public default void startStringConstant(AstStringConstantExpression expression) {}
	public default void endStringConstant (AstStringConstantExpression expression) {}
	
	public default void startIntegerConstant(AstIntegerConstantExpression expression) {}
	public default void endIntegerConstant (AstIntegerConstantExpression expression) {}
	
	public default void startFloatConstant(AstFloatConstantExpression expression) {}
	public default void endFloatConstant (AstFloatConstantExpression expression) {}
	
	public default void startBooleanConstant(AstBooleanConstantExpression expression) {}
	public default void endBooleanConstant (AstBooleanConstantExpression expression) {}
	
	
	// -- Statements
	public default void startReturnStatement (AstReturnStatement statement) {}
	public default void endReturnStatement (AstReturnStatement statement) {}

	public default void startExpressionStatement (AstExpressionStatement statement) {}
	public default void endExpressionStatement (AstExpressionStatement statement) {}
	
	public default void start (AstLocalVariableStatement statement) {}
	public default void end   (AstLocalVariableStatement statement) {}
	
	public default void startBlock (AstBlock statement) {}
	public default void endBlock   (AstBlock statement) {}
	
	public default void startIfElseStatement(AstIfElseStatement statement) {}
	public default void endIfElseStatement	(AstIfElseStatement statement) {}
	
	// -- Types
	public default void start(AstArrayType type) {}
	public default void end(AstArrayType type) {}
	
	public default void start(AstVoidType type) {}
	public default void end(AstVoidType type) {}
	
	public default void start(IntersectionType type) {}
	public default void end(IntersectionType type) {}
	
	public default void start(QNameRefType type) {}
	public default void end(QNameRefType type) {}
	
	public default void start(SimpleType type) {}
	public default void end(SimpleType type) {}
	
	public default void start(TypeParameter type) {}
	public default void end(TypeParameter type) {}
	
	public default void start(UnionType type) {}
	public default void end(UnionType type) {}
	
}
