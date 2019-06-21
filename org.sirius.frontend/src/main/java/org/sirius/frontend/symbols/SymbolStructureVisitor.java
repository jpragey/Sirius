package org.sirius.frontend.symbols;

import java.util.Stack;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.ast.Scoped;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.ast.TypeFormalParameterDeclaration;

/** Visitor that sets the 'parent' symbol table field throughout the AST.
 * 
 * @author jpragey
 *
 */
public class SymbolStructureVisitor implements AstVisitor {


//	private List<SymbolTable> stack = new ArrayList<>();
	private Stack<SymbolTable> stack = new Stack<>();
	
//	private List<String> packageQName;
	private QName packageQName;
	private GlobalSymbolTable globalSymbolTable;

	public SymbolStructureVisitor(/*SymbolTable rootSymbolTable, */GlobalSymbolTable globalSymbolTable, QName packageQName) {
		super();
//		this.stack.add(rootSymbolTable);
//		this.stack.add(globalSymbolTable);
		this.packageQName = packageQName;
		this.globalSymbolTable = globalSymbolTable;
	}

	private SymbolTable startScope0(Scoped scoped) {
		SymbolTable st = scoped.getSymbolTable();
//		st.setParentSymbolTable(stack.peek());
//		stack.push(st);
		return st;
	}
//	private void endScope(Scoped scoped) {
//		stack.pop();
//	}
	
	@Override
	public void startCompilationUnit(StandardCompilationUnit compilationUnit) {
		SymbolTable st = startScope0(compilationUnit);
		stack.push(st);
		
		AliasingSymbolTable alst = compilationUnit.getSymbolTable();
		
		for(ImportDeclaration imp: compilationUnit.getImportDeclarations()) {
			for(ImportDeclarationElement element: imp.getElements()) {
				
				alst.addImportSymbol(imp.getPack(), element.getImportedTypeName(), element.getAlias());
			}
		}
	}

	@Override
	public void endCompilationUnit(StandardCompilationUnit compilationUnit) {
		stack.pop();
//		endScope(compilationUnit);
	}

	
	
	
	@Override
	public void startClassDeclaration(AstClassDeclaration classDeclaration) {
		SymbolTable symbolTable = startScope0(classDeclaration);
		stack.push(symbolTable);
		
		for(TypeFormalParameterDeclaration formalParameter: classDeclaration.getTypeParameters()) {
//			symbolTable.addFormalParameter(formalParameter.getFormalName(), formalParameter);
			classDeclaration.getSymbolTable().addFormalParameter(formalParameter.getFormalName(), formalParameter); 	// TODO: demeter
		}
		
		AstToken className = classDeclaration.getName();
		classDeclaration.getSymbolTable().addClass(className, classDeclaration);	// TODO: demeter
//		symbolTable.addClass(className, classDeclaration);
		
		globalSymbolTable.addClass(packageQName, classDeclaration);
	}

	@Override
	public void endClassDeclaration(AstClassDeclaration classDeclaration) {
		stack.pop();
//		endScope(classDeclaration);
	}

	@Override
	public void startFunctionDeclaration(AstFunctionDeclaration functionDeclaration) {
		AstToken functionName = functionDeclaration.getName();

		SymbolTable symbolTable = startScope0(functionDeclaration);
		stack.push(symbolTable);
		
//		symbolTable.addFunction(functionName, functionDeclaration);
//		functionDeclaration.getSymbolTable().addFunction(functionName, functionDeclaration);	// TODO: demeter
		
		globalSymbolTable.addFunction(packageQName, functionDeclaration);
	}

	@Override
	public void endFunctionDeclaration(AstFunctionDeclaration functionDeclaration) {
//		endScope(functionDeclaration);
		stack.pop();
	}
	
	
}
