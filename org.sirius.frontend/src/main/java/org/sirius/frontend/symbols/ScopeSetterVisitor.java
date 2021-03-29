package org.sirius.frontend.symbols;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.sirius.frontend.ast.AstBlock;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.LambdaDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.SimpleReferenceExpression;

public class ScopeSetterVisitor implements AstVisitor {

	private Stack<Scope> scopeStack = new Stack<>();
	private LinkedList<Integer> stmtIndexStack = new LinkedList<Integer>();
	
	private static class STableNameElement {
		
		// Module package class function block
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

	private Scope pushNewScope() {
		Scope newScope = new Scope(scopeStack.empty() ? Optional.empty() : Optional.of(scopeStack.peek()));
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
		Scope scope = pushNewScope();
	}
	@Override
	public void endScriptCompilationUnit(ScriptCompilationUnit compilationUnit) {
		popScope();
	}
	
	
	@Override
	public void startModuleDeclaration(AstModuleDeclaration declaration) {
		Scope scope = pushNewScope();
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
		declaration.setScopeName(declaration.getQnameString());
		STableNameElement el = new STableNameElement(declaration);
		nameElementsStack.add(el);
	}
	@Override
	public void endPackageDeclaration(AstPackageDeclaration declaration) {
		// TODO Auto-generated method stub
		AstVisitor.super.endPackageDeclaration(declaration);
		nameElementsStack.removeLast();
	}
	
	
	@Override
	public void startClassDeclaration(AstClassDeclaration classDeclaration) {
		Scope scope = pushNewScope();
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
	public void startSimpleReferenceExpression(SimpleReferenceExpression expression) {
		Scope scope = scopeStack.peek();
		expression.setScope(scope);
	}
	
	@Override
	public void startPartial(Partial partialFunctionDeclaration) {
		Scope scope = pushNewScope();
		partialFunctionDeclaration.assignScope(scope);
//		partialFunctionDeclaration.setScopeName(partialFunctionDeclaration.getqName().dotSeparated());
	}
	@Override
	public void endPartial(Partial partialFunctionDeclaration) {
		popScope();
	}

//	private int currentBlockIndex = 0;

	@Override
	public void startBlock(AstBlock block) {

		String name = currentFullName();
//		String name = stmtIndexStack.stream().map(i -> i.toString()).collect(Collectors.joining(".", "$b:", "$"));
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
	}
	
	@Override
	public void startLambdaDefinition(LambdaDefinition definition) {
		Scope scope = pushNewScope();
		definition.setSymbolTable(scope.getSymbolTable()); // TODO: ugly
	}
	
	@Override
	public void endLambdaDefinition(LambdaDefinition definition) {
		popScope();
	}
}
