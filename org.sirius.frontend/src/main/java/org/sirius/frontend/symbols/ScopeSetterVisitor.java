package org.sirius.frontend.symbols;

import java.util.Optional;
import java.util.Stack;

import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.SimpleReferenceExpression;

public class ScopeSetterVisitor implements AstVisitor {

	private Stack<Scope> scopeStack = new Stack<>();
	
	private Scope pushNewScope() {
		Scope newScope = new Scope(scopeStack.empty() ? Optional.empty() : Optional.of(scopeStack.peek()));
		scopeStack.push(newScope);
		return newScope;
	}

	private void popScope() {
		scopeStack.pop();
	}
	
	@Override
	public void startScriptCompilationUnit(ScriptCompilationUnit compilationUnit) {
		Scope scope = pushNewScope();
	}
	@Override
	public void endScriptCompilationUnit(ScriptCompilationUnit compilationUnit) {
		popScope();
	}
	
	
	@Override
	public void startModuleDeclaration(AstModuleDeclaration declaration) {
		Scope scope = pushNewScope();
	}
	@Override
	public void endModuleDeclaration(AstModuleDeclaration declaration) {
		popScope();
	}
	
	@Override
	public void startPackageDeclaration(AstPackageDeclaration declaration) {
		// TODO Auto-generated method stub
		AstVisitor.super.startPackageDeclaration(declaration);
	}
	@Override
	public void endPackageDeclaration(AstPackageDeclaration declaration) {
		// TODO Auto-generated method stub
		AstVisitor.super.endPackageDeclaration(declaration);
	}
	
	
	@Override
	public void startClassDeclaration(AstClassDeclaration classDeclaration) {
		Scope scope = pushNewScope();
		classDeclaration.assignScope(scope);
	}
	
	@Override
	public void endClassDeclaration(AstClassDeclaration classDeclaration) {
		popScope();
	}
	
	@Override
	public void startSimpleReferenceExpression(SimpleReferenceExpression expression) {
		Scope scope = scopeStack.peek();
		expression.setScope(scope);
	}
	
	@Override
	public void startPartial(Partial partialFunctionDeclaration) {
		Scope scope = pushNewScope();
		partialFunctionDeclaration.assignScope(scope);
	}
	@Override
	public void endPartial(Partial partialFunctionDeclaration) {
		popScope();
	}
	
}
