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
	
	public default void startFunctionDeclaration (AstFunctionDeclaration functionDeclaration) {}
	public default void endFunctionDeclaration (AstFunctionDeclaration functionDeclaration) {}
	
	public default void startValueDeclaration (AstValueDeclaration valueDeclaration) {}
	public default void endValueDeclaration (AstValueDeclaration valueDeclaration) {}
	
	// -- Expressions
	public default void startExpression (Expression expression) {}
	public default void endExpression (Expression expression) {}
	
	public default void startBinaryOpExpression (BinaryOpExpression expression) {}
	public default void endBinaryOpExpression (BinaryOpExpression expression) {}
	
	public default void startFunctionCallExpression (FunctionCallExpression expression) {}
	public default void endFunctionCallExpression (FunctionCallExpression expression) {}
	
	
	
	
//	public default void startConstantExpression (ConstantExpression expression) {}
//	public default void endConstantExpression (ConstantExpression expression) {}
	
	public default void startStringConstant(StringConstantExpression expression) {}
	public default void endStringConstant (StringConstantExpression expression) {}
	
	public default void startIntegerConstant(IntegerConstantExpression expression) {}
	public default void endIntegerConstant (IntegerConstantExpression expression) {}
	
	public default void startFloatConstant(FloatConstantExpression expression) {}
	public default void endFloatConstant (FloatConstantExpression expression) {}
	
	public default void startBooleanConstant(BooleanConstantExpression expression) {}
	public default void endBooleanConstant (BooleanConstantExpression expression) {}
	
	
	// -- Statements
	public default void startReturnStatement (ReturnStatement statement) {}
	public default void endReturnStatement (ReturnStatement statement) {}

	
	
}
