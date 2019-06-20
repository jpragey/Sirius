package org.sirius.frontend.ast;

public interface AstVisitor {

	public default void startCompilationUnit (StandardCompilationUnit compilationUnit) {}
	public default void endCompilationUnit (StandardCompilationUnit compilationUnit) {}

//	public default void startScriptCompilationUnit (ScriptCompilationUnit compilationUnit) {}
//	public default void endScriptCompilationUnit (ScriptCompilationUnit compilationUnit) {}

	public default void startModuleDescriptorCompilationUnit (ModuleDescriptor compilationUnit) {}
	public default void endModuleDescriptorCompilationUnit (ModuleDescriptor compilationUnit) {}

	public default void startPackageDescriptorCompilationUnit (PackageDescriptorCompilationUnit compilationUnit) {}
	public default void endPackageDescriptorCompilationUnit(PackageDescriptorCompilationUnit compilationUnit) {}

	public default void startImportDeclaration	(ImportDeclaration importDeclaration) {}
	public default void endImportDeclaration	(ImportDeclaration importDeclaration) {}


	
	
	
	public default void startShebangDeclaration (ShebangDeclaration declaration) {}
	public default void endShebangDeclaration (ShebangDeclaration declaration) {}

	public default void startPackageDeclaration (PackageDeclaration declaration) {}
	public default void endPackageDeclaration (PackageDeclaration declaration) {}

	
	public default void startClassDeclaration (ClassDeclaration classDeclaration) {}
	public default void endClassDeclaration (ClassDeclaration classDeclaration) {}
	
	public default void startFunctionDeclaration (FunctionDeclaration functionDeclaration) {}
	public default void endFunctionDeclaration (FunctionDeclaration functionDeclaration) {}
	
	public default void startValueDeclaration (ValueDeclaration valueDeclaration) {}
	public default void endValueDeclaration (ValueDeclaration valueDeclaration) {}
	
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
