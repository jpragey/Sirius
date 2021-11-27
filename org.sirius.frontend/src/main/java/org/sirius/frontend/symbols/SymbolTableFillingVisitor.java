package org.sirius.frontend.symbols;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstIntegerConstantExpression;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberAccessExpression;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstStringConstantExpression;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ConstructorCallExpression;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.SimpleReferenceExpression;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.ast.TypeParameter;

/** Visitor that sets the 'parent' symbol table field throughout the AST.
 * 
 * @author jpragey
 *
 */
public class SymbolTableFillingVisitor implements AstVisitor {

	private Stack<SymbolTableImpl> symbolTableStack = new Stack<>();
	

	public SymbolTableFillingVisitor(Scope scope) {
		super();
		this.symbolTableStack.push(scope.getSymbolTable());
	}

	private void processImports(SymbolTableImpl st, List<ImportDeclaration> imports) {
		assert(st != null);
		symbolTableStack.push(st);
		
		for(ImportDeclaration importDecl: imports) {
			for(ImportDeclarationElement element: importDecl.getElements()) {
				st.addImportSymbol(importDecl.getPack(), element);
			}
		}
	}

	@Override 
	public void startScriptCompilationUnit(ScriptCompilationUnit compilationUnit) {
		processImports(compilationUnit.getScope().getSymbolTable(), compilationUnit.getImportDeclarations());
	}
	
	@Override
	public void startCompilationUnit(StandardCompilationUnit compilationUnit) {
		processImports(compilationUnit.getScope().getSymbolTable() /* compilationUnit.getSymbolTable()*/, compilationUnit.getImportDeclarations());
	}

	@Override
	public void startPackageDeclaration(AstPackageDeclaration declaration) {
	}
	
	@Override
	public void endCompilationUnit(StandardCompilationUnit compilationUnit) {
		symbolTableStack.pop();
	}
	
	@Override
	public void startClassDeclaration(AstClassDeclaration classDeclaration) {
		SymbolTableImpl parentSymbolTable = symbolTableStack.lastElement();
		
		SymbolTableImpl symbolTable = new SymbolTableImpl(Optional.of(parentSymbolTable), classDeclaration.getName().getText());
		symbolTableStack.push(symbolTable);
		classDeclaration.setSymbolTable(symbolTable);

		for(TypeParameter formalParameter: classDeclaration.getTypeParameters()) {
			classDeclaration.getSymbolTable().addFormalParameter(classDeclaration.getQName(), formalParameter);
		}
		
		classDeclaration.getSymbolTable().addClass(classDeclaration);
		
		parentSymbolTable.addClass(classDeclaration);
	}

	@Override
	public void startInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		SymbolTableImpl parentSymbolTable = symbolTableStack.lastElement();
		
		SymbolTableImpl symbolTable = new SymbolTableImpl(Optional.of(parentSymbolTable), interfaceDeclaration.getName().getText());
		symbolTableStack.push(symbolTable);
		
		for(TypeParameter formalParameter: interfaceDeclaration.getTypeParameters()) {
			interfaceDeclaration.getSymbolTable().addFormalParameter(interfaceDeclaration.getQName(), formalParameter);
		}
		
		interfaceDeclaration.getScope().addInterface(interfaceDeclaration);
		
		parentSymbolTable.addInterface(interfaceDeclaration);
	}
	
	
	
	@Override
	public void endClassDeclaration(AstClassDeclaration classDeclaration) {
		symbolTableStack.pop();
	}

	@Override
	public void endInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		symbolTableStack.pop();
	}	

	@Override
	public void startPartial (Partial partial) {
		SymbolTableImpl parentSymbolTable = symbolTableStack.lastElement();
		
		String stName = "Partial " + partial.getName() + 
				"(" + partial.getArgs().size() + ")";
				
		SymbolTableImpl functionSymbolTable = new SymbolTableImpl(Optional.of(parentSymbolTable), stName);
		
		partial.assignSymbolTable(functionSymbolTable);
		symbolTableStack.push(functionSymbolTable);
	}
	@Override
	public void endPartial   (Partial partialFunctionDeclaration) {
		symbolTableStack.pop();
	}

	@Override
	public void startFunctionDefinition(FunctionDefinition functionDefinition) {
		SymbolTableImpl parentSymbolTable = symbolTableStack.lastElement();
		parentSymbolTable.addFunction(functionDefinition);
	}
	
	@Override
	public void startFunctionParameter(AstFunctionParameter formalArgument) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		formalArgument.setSymbolTable(symbolTable);
	}
	
	@Override
	public void startFunctionCallExpression(AstFunctionCallExpression expression) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		assert(symbolTable != null);
		assert(expression.getSymbolTable() != null);
	}
	@Override
	public void startConstructorCallExpression (ConstructorCallExpression expression) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		assert(symbolTable != null);
	}

	@Override
	public void startSimpleReferenceExpression(SimpleReferenceExpression expression) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		assert(symbolTable != null);
		
		expression.setSymbolTable(symbolTable);
	}

	public void startFieldAccess (AstMemberAccessExpression expression) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		assert(symbolTable != null);
	}

	@Override
	public void start(SimpleType simpleType) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		simpleType.setSymbolTable(symbolTable);
	}
	
	@Override
	public void startIntegerConstant(AstIntegerConstantExpression expression) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		expression.setSymbolTable(symbolTable);
	}
	
	@Override
	public void startStringConstant(AstStringConstantExpression expression) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		expression.setSymbolTable(symbolTable);
	}
	
	
	@Override
	public void start (AstLocalVariableStatement statement) {
		SymbolTableImpl symbolTable = symbolTableStack.lastElement();
		statement.setSymbolTable(symbolTable);
		symbolTable.addLocalVariable(statement);
	}
}
