package org.sirius.frontend.api;

public interface Visitor {

	public default void startClassType(ClassType declaration) {}
	public default void endClassType(ClassType declaration) {}

//	public default void startClassDeclaration(ClassDeclaration declaration) {}
//	public default void endClassDeclaration(ClassDeclaration declaration) {}
//
//	public default void startInterfaceDeclaration(InterfaceDeclaration declaration) {}
//	public default void endInterfaceDeclaration(InterfaceDeclaration declaration) {}
	
	public default void start(ConstructorDeclaration declaration) {}
	public default void end(ConstructorDeclaration declaration) {}
	
//	public default void start(MemberFunction declaration) {}
//	public default void end(MemberFunction declaration) {}
//	public default void start(TopLevelFunction declaration) {}
//	public default void end(TopLevelFunction declaration) {}
	
	public default void startAbstractFunction(AbstractFunction declaration) {}
	public default void endAbstractFunction(AbstractFunction declaration) {}
	
	public default void startFunctionActualArgument(FunctionActualArgument declaration) {}
	public default void endFunctionActualArgument(FunctionActualArgument declaration) {}
	
	
	
	
	public default void start(Statement statement) {}
	public default void end(Statement statement) {}
	
	public default void start(BlockStatement statement) {}
	public default void end(BlockStatement statement) {}
	
	public default void start(LocalVariableStatement statement) {}
	public default void end(LocalVariableStatement statement) {}
	
	public default void start(ExpressionStatement statement) {}
	public default void end(ExpressionStatement statement) {}
	
	// -- Expressions
	public default void start(IntegerConstantExpression expression) {}
	public default void end(IntegerConstantExpression expression) {}
	
	public default void start(FloatConstantExpression expression) {}
	public default void end(FloatConstantExpression expression) {}
	
	public default void start(BooleanConstantExpression expression) {}
	public default void end(BooleanConstantExpression expression) {}
	
	public default void start(StringConstantExpression expression) {}
	public default void end(StringConstantExpression expression) {}
	
	public default void start(BinaryOpExpression expression) {}
	public default void end(BinaryOpExpression expression) {}
	
	public default void start(FunctionCall expression) {}
	public default void end(FunctionCall expression) {}
	
	public default void start(TypeCastExpression expression) {}
	public default void end(TypeCastExpression expression) {}
	
	public default void start(MemberValueAccessExpression expression) {}
	public default void end(MemberValueAccessExpression expression) {}
	
	public default void start(LocalVariableReference expression) {}
	public default void end(LocalVariableReference expression) {}
	
	
	
	
	public default void start(ConstructorCall expression) {}
	public default void end(ConstructorCall expression) {}
	
	
	public default void start(ReturnStatement statement) {}
	public default void end(ReturnStatement statement) {}
	
	
	public default void start(MemberValue declaration) {}
	public default void end(MemberValue declaration) {}
	
	public default void start(ModuleDeclaration declaration) {}
	public default void end(ModuleDeclaration declaration) {}
	
	public default void start(PackageDeclaration declaration) {}
	public default void end(PackageDeclaration declaration) {}
	
	
	public default void start(FunctionParameter declaration) {}
	public default void end(FunctionParameter declaration) {}
	
	
	
//	public default void start(TopLevelValue declaration) {}
//	public default void end(TopLevelValue declaration) {}
	
}
