package org.sirius.frontend.symbols;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Stack;

import org.sirius.frontend.ast.AstBlock;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ConstructorCallExpression;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.SimpleReferenceExpression;

public class ScopeSetterVisitor implements AstVisitor {

	private Stack<Scope> scopeStack = new Stack<>();
	private LinkedList<Integer> stmtIndexStack = new LinkedList<Integer>();
	
	private static class STableNameElement {
		
		private String prefix;
		private String suffix;
		private String content;
		
		public STableNameElement(String prefix, String suffix, String content) {
			super();
			this.prefix = prefix;
			this.suffix = suffix;
			this.content = content;
		}
		public STableNameElement(AstModuleDeclaration declaration) {
			this("$m{", "}", declaration.getqName().dotSeparated());
		}
		public STableNameElement(AstPackageDeclaration declaration) {
			this("$p{", "}", declaration.getQname().dotSeparated());
		}
		public STableNameElement(AstClassDeclaration declaration) {
			this("$c{", "}", declaration.getQName().dotSeparated());
		}
		public STableNameElement(AstBlock block, int index) {
			this("${", "}", Integer.toString(index));
		}
		public void appendT(StringBuilder sb) {
			sb.append(prefix);
			sb.append(content);
			sb.append(suffix);
		}
	}
	private LinkedList<STableNameElement> nameElementsStack = new LinkedList<>();
	
	public ScopeSetterVisitor(Scope globalScope ) {
		super();
		scopeStack.push(globalScope);
		stmtIndexStack.push(0);
	}

	private Scope pushNewScope(String newScopeName) {
		Optional<Scope> currentScope = scopeStack.empty() ? Optional.empty() : Optional.of(scopeStack.peek());
		Scope newScope = new Scope(currentScope, newScopeName);
		scopeStack.push(newScope);
		return newScope;
	}

	private void popScope() {
		scopeStack.pop();
	}

	private String currentFullName() {
		StringBuilder sb = new StringBuilder();
		for(STableNameElement e: nameElementsStack) {
			e.appendT(sb);
		}
		String fullName = sb.toString();
		return fullName;
	}

	
	@Override
	public void startScriptCompilationUnit(ScriptCompilationUnit compilationUnit) {
		Scope scope = pushNewScope("ScriptCU root");
	}
	@Override
	public void endScriptCompilationUnit(ScriptCompilationUnit compilationUnit) {
		popScope();
	}
	
	
	@Override
	public void startModuleDeclaration(AstModuleDeclaration declaration) {
		Scope scope = pushNewScope("Module " + declaration.getQnameString().toString());
		STableNameElement el = new STableNameElement(declaration);
		nameElementsStack.add(el);
	}
	@Override
	public void endModuleDeclaration(AstModuleDeclaration declaration) {
		popScope();
		nameElementsStack.removeLast();
	}
	
	@Override
	public void startPackageDeclaration(AstPackageDeclaration declaration) {
		Scope newScope = pushNewScope("Package " + declaration.getQnameString());
		declaration.setScope2(newScope);
		
		declaration.setScopeName(declaration.getQnameString());
		STableNameElement el = new STableNameElement(declaration);
		nameElementsStack.add(el);
	}
	@Override
	public void endPackageDeclaration(AstPackageDeclaration declaration) {
		popScope();
		nameElementsStack.removeLast();
	}
	
	
	@Override
	public void startClassDeclaration(AstClassDeclaration classDeclaration) {
		Scope scope = pushNewScope("Class " + classDeclaration.getName().getText());
		classDeclaration.assignScope(scope);
		STableNameElement el = new STableNameElement(classDeclaration);
		nameElementsStack.add(el);
	}
	
	@Override
	public void endClassDeclaration(AstClassDeclaration classDeclaration) {
		popScope();
		nameElementsStack.removeLast();
	}
	
	@Override
	public void startInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		Scope scope = pushNewScope("Interface " + interfaceDeclaration.getNameString());
		interfaceDeclaration.setScope2(scope);
	}
	@Override
	public void endInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		popScope();
	}
	
	
	@Override
	public void startSimpleReferenceExpression(SimpleReferenceExpression expression) {
		Scope scope = scopeStack.peek();
		expression.setScope(scope);
	}
	
	@Override
	public void startFunctionCallExpression(AstFunctionCallExpression expression) {
		Scope scope = scopeStack.peek();
		expression.setScope(scope);
	}
	
	@Override
	public void startPartial(Partial partialFunctionDeclaration) {
		Scope scope = pushNewScope("Partial " + partialFunctionDeclaration.getName().getText());
		partialFunctionDeclaration.assignScope(scope);
	}
	@Override
	public void endPartial(Partial partialFunctionDeclaration) {
		popScope();
	}

	@Override
	public void startBlock(AstBlock block) {

		Scope scope = pushNewScope("Block (TODO: name)");
		block.setScope2(scope);

		String name = currentFullName();
		int index = stmtIndexStack.removeLast();
		
		block.getSymbolTable().setName(name);
		index++;
		stmtIndexStack.addLast(index);
		stmtIndexStack.addLast(0);
		STableNameElement el = new STableNameElement(block, index);
		nameElementsStack.add(el);
	}

	@Override
	public void endBlock(AstBlock blockStmt) {
		stmtIndexStack.removeLast();
		nameElementsStack.removeLast();

		popScope();
	}
	
	@Override
	public void startLambdaDefinition(LambdaDefinition definition) {
		Scope parentScope = scopeStack.peek();
		Scope lambdaScope = definition.createScope(parentScope);
		scopeStack.push(lambdaScope);
	}
	
	@Override
	public void endLambdaDefinition(LambdaDefinition definition) {
		popScope();
	}

	@Override
	public void startFunctionDefinition(FunctionDefinition functionDefinition) {
		Scope fctScope = pushNewScope(functionDefinition.getNameString());
		functionDefinition.setScope2(fctScope);
	}
	@Override
	public void endFunctionDefinition(FunctionDefinition functionDefinition) {
		popScope();
	}
	
	@Override
	public void startConstructorCallExpression(ConstructorCallExpression expression) {
		Scope scope = scopeStack.peek();
		expression.setScope(scope);
	}
	
}
