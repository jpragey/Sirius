package org.sirius.frontend.api;

public interface Visitor {

	public default void start(ClassDeclaration declaration) {}
	public default void end(ClassDeclaration declaration) {}

	public default void start(InterfaceDeclaration declaration) {}
	public default void end(InterfaceDeclaration declaration) {}
	
	public default void start(ConstructorDeclaration declaration) {}
	public default void end(ConstructorDeclaration declaration) {}
	
	public default void start(MemberFunction declaration) {}
	public default void end(MemberFunction declaration) {}
	
	public default void start(Statement statement) {}
	public default void end(Statement statement) {}
	
	public default void start(LocalVariableStatement statement) {}
	public default void end(LocalVariableStatement statement) {}
	
	public default void start(ExpressionStatement statement) {}
	public default void end(ExpressionStatement statement) {}
	
	// -- Expressions
	public default void start(IntegerConstantExpression expression) {}
	public default void end(IntegerConstantExpression expression) {}
	
	public default void start(StringConstantExpression expression) {}
	public default void end(StringConstantExpression expression) {}
	
	public default void start(BinaryOpExpression expression) {}
	public default void end(BinaryOpExpression expression) {}
	
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
	
	public default void start(TopLevelFunction declaration) {}
	public default void end(TopLevelFunction declaration) {}
	
	public default void start(FunctionFormalArgument declaration) {}
	public default void end(FunctionFormalArgument declaration) {}
	
	
	
	public default void start(TopLevelValue declaration) {}
	public default void end(TopLevelValue declaration) {}
	
}
