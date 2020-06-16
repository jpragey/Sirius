package org.sirius.frontend.symbols;

import java.util.List;
import java.util.Stack;

import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstFunctionDeclarationBuilder;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstIntegerConstantExpression;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberAccessExpression;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStringConstantExpression;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ConstructorCallExpression;
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

	private Stack<DefaultSymbolTable> symbolTableStack = new Stack<>();
	

	public SymbolTableFillingVisitor(DefaultSymbolTable globalSymbolTable) {
		super();
		this.symbolTableStack.push(globalSymbolTable);
	}

	private void processImports(DefaultSymbolTable st, List<ImportDeclaration> imports) {
		symbolTableStack.push(st);
		
		for(ImportDeclaration importDecl: imports) {
			for(ImportDeclarationElement element: importDecl.getElements()) {
				st.addImportSymbol(importDecl.getPack(), element);
			}
		}
	}

	@Override 
	public void startScriptCompilationUnit(ScriptCompilationUnit compilationUnit) {
		processImports(compilationUnit.getSymbolTable(), compilationUnit.getImportDeclarations());
	}
	
	@Override
	public void startCompilationUnit(StandardCompilationUnit compilationUnit) {
		processImports(compilationUnit.getSymbolTable(), compilationUnit.getImportDeclarations());
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
		DefaultSymbolTable parentSymbolTable = symbolTableStack.lastElement();
		
		DefaultSymbolTable symbolTable = new DefaultSymbolTable(parentSymbolTable, classDeclaration.getName().getText());
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
		DefaultSymbolTable parentSymbolTable = symbolTableStack.lastElement();
		
		DefaultSymbolTable symbolTable = new DefaultSymbolTable(parentSymbolTable, interfaceDeclaration.getName().getText());
		symbolTableStack.push(symbolTable);
		interfaceDeclaration.setSymbolTable(symbolTable);

		for(TypeParameter formalParameter: interfaceDeclaration.getTypeParameters()) {
			interfaceDeclaration.getSymbolTable().addFormalParameter(interfaceDeclaration.getQName(), formalParameter);
		}
		
		interfaceDeclaration.getSymbolTable().addInterface(interfaceDeclaration);
		
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
	public void startFunctionDeclaration(AstFunctionDeclarationBuilder functionDeclaration) {
		/*
		DefaultSymbolTable parentSymbolTable = symbolTableStack.lastElement();
		
		DefaultSymbolTable functionSymbolTable = new DefaultSymbolTable(parentSymbolTable, functionDeclaration.getName().getText());
		functionDeclaration.assignSymbolTable(functionSymbolTable);
		symbolTableStack.push(functionSymbolTable);
		
		parentSymbolTable.addFunction(functionDeclaration);
		*/
	}

	@Override
	public void endFunctionDeclaration(AstFunctionDeclarationBuilder functionDeclaration) {
		symbolTableStack.pop();
	}
	@Override
	public void startPartial (Partial partial) {
		DefaultSymbolTable parentSymbolTable = symbolTableStack.lastElement();
		
		String stName = "Partial " + partial.getName() + 
//				"[" + partial.getCaptures().size() + "]" +
				"(" + partial.getArgs().size() + ")";
				
		DefaultSymbolTable functionSymbolTable = new DefaultSymbolTable(parentSymbolTable, stName);
		
		partial.assignSymbolTable(functionSymbolTable);
		symbolTableStack.push(functionSymbolTable);
		
//		parentSymbolTable.addFunction(functionDeclaration);
		
	}
	@Override
	public void endPartial   (Partial partialFunctionDeclaration) {
		symbolTableStack.pop();
	}

	
	@Override
	public void startFunctionFormalArgument(AstFunctionParameter formalArgument) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		formalArgument.setSymbolTable(symbolTable);
	}
	
	@Override
	public void startFunctionCallExpression(AstFunctionCallExpression expression) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		assert(symbolTable != null);
		
		expression.setSymbolTable(symbolTable);
	}
	@Override
	public void startConstructorCallExpression (ConstructorCallExpression expression) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		assert(symbolTable != null);
		
		expression.setSymbolTable(symbolTable);
	}

	@Override
	public void startSimpleReferenceExpression(SimpleReferenceExpression expression) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		assert(symbolTable != null);
		
		expression.setSymbolTable(symbolTable);
	}

	public void startFieldAccess (AstMemberAccessExpression expression) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		assert(symbolTable != null);
		expression.setSymbolTable(symbolTable);
	}

	@Override
	public void start(SimpleType simpleType) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		simpleType.setSymbolTable(symbolTable);
	}
	
	@Override
	public void startIntegerConstant(AstIntegerConstantExpression expression) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		expression.setSymbolTable(symbolTable);
	}
	
	@Override
	public void startStringConstant(AstStringConstantExpression expression) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		expression.setSymbolTable(symbolTable);
	}
	
	
	@Override
	public void start (AstLocalVariableStatement statement) {
		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
		statement.setSymbolTable(symbolTable);
		symbolTable.addLocalVariable(statement);
	}

//	@Override
//	public void startReturnStatement(AstReturnStatement statement) {
//		DefaultSymbolTable symbolTable = symbolTableStack.lastElement();
//		statement.getExpression()
//		AstVisitor.super.startReturnStatement(statement);
//	}
}
