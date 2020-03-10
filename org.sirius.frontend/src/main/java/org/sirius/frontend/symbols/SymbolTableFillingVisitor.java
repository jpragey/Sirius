package org.sirius.frontend.symbols;

import java.util.Stack;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstFunctionFormalArgument;
import org.sirius.frontend.ast.AstIntegerConstantExpression;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstStringConstantExpression;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.ast.Scoped;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.ast.TypeFormalParameterDeclaration;

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

	@Override
	public void startCompilationUnit(StandardCompilationUnit compilationUnit) {
		DefaultSymbolTable st = compilationUnit.getSymbolTable();
		symbolTableStack.push(st);
		
//		DefaultSymbolTable alst = compilationUnit.getSymbolTable();
		
		for(ImportDeclaration importDecl: compilationUnit.getImportDeclarations()) {
			for(ImportDeclarationElement element: importDecl.getElements()) {
				
//				st.addImportSymbol(importDecl.getPack(), element.getImportedTypeName(), element.getAlias());
				st.addImportSymbol(importDecl.getPack(), element);
			}
		}
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
		
//		DefaultSymbolTable symbolTable = classDeclaration.getSymbolTable();
		DefaultSymbolTable symbolTable = new DefaultSymbolTable(parentSymbolTable);
		symbolTableStack.push(symbolTable);
		classDeclaration.setSymbolTable(symbolTable);

//		String className = classDeclaration.getName().getText();
//		QName classQName = qnameStack.lastElement().child(className);
//		qnameStack.push(classQName);
//		classDeclaration.setqName(classQName);
		
		for(TypeFormalParameterDeclaration formalParameter: classDeclaration.getTypeParameters()) {
			classDeclaration.getSymbolTable().addFormalParameter(classDeclaration.getQName(), formalParameter);
		}
		
//		AstToken className = classDeclaration.getName();
		classDeclaration.getSymbolTable().addClass(classDeclaration);
		
		parentSymbolTable.addClass(classDeclaration);
	}

	@Override
	public void endClassDeclaration(AstClassDeclaration classDeclaration) {
		symbolTableStack.pop();
	}

	@Override
	public void startFunctionDeclaration(AstFunctionDeclaration functionDeclaration) {
		DefaultSymbolTable parentSymbolTable = symbolTableStack.lastElement();
		
//		functionDeclaration.setContainerQName(qnameStack.lastElement());
//		String funcName = functionDeclaration.getName().getText();
//		QName funcQName = qnameStack.lastElement().child(funcName);
//		qnameStack.push(funcQName);

		
		DefaultSymbolTable functionSymbolTable = new DefaultSymbolTable(parentSymbolTable);
		functionDeclaration.assignSymbolTable(functionSymbolTable);
//		DefaultSymbolTable symbolTable = functionDeclaration.getSymbolTable();
//		SymbolTable symbolTable = startScope0(functionDeclaration);
		symbolTableStack.push(functionSymbolTable);
		
		parentSymbolTable.addFunction(functionDeclaration);
	}

	@Override
	public void endFunctionDeclaration(AstFunctionDeclaration functionDeclaration) {
		symbolTableStack.pop();
//		qnameStack.pop();
	}
	
	@Override
	public void startFunctionFormalArgument(AstFunctionFormalArgument formalArgument) {
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
	
	
}
