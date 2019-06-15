package org.sirius.frontend.symbols;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ClassDeclaration;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.ast.FunctionCallExpression;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.StringConstantExpression;

/** Visitor that sets the 'parent' symbol table field throughout the AST.
 * 
 * @author jpragey
 *
 */
public class SymbolResolutionVisitor implements AstVisitor {


//	private List<SymbolTable> stack = new ArrayList<>();
	private Stack<SymbolTable> stack = new Stack<>();
	
	private QName packageQName;
	private GlobalSymbolTable globalSymbolTable;

	public SymbolResolutionVisitor(/*SymbolTable rootSymbolTable, */GlobalSymbolTable globalSymbolTable, QName packageQName) {
		super();
		this.packageQName = packageQName;
		this.globalSymbolTable = globalSymbolTable;
	}

	
	@Override
	public void startCompilationUnit(StandardCompilationUnit compilationUnit) {
	}

	@Override
	public void endCompilationUnit(StandardCompilationUnit compilationUnit) {
//		endScope(compilationUnit);
	}

	@Override
	public void startClassDeclaration(ClassDeclaration classDeclaration) {
//		startScope(classDeclaration);
	}
	@Override
	public void endClassDeclaration(ClassDeclaration classDeclaration) {
//		endScope(classDeclaration);
	}
	
	@Override
	public void startFunctionDeclaration(FunctionDeclaration functionDeclaration) {
//		startScope(functionDeclaration);
	}
	
	@Override
	public void endFunctionDeclaration(FunctionDeclaration functionDeclaration) {
//		endScope(functionDeclaration);
	}
	
	
	private void resolveFctCallSymbol(FunctionCallExpression expression, Symbol symbol) {
		
	}
	
	@Override
	public void startFunctionCallExpression(FunctionCallExpression expression) {
		String funcName = expression.getName().getText();
		Optional<Symbol> f = stack.peek().lookup(funcName);
		if(f.isPresent()) {
			resolveFctCallSymbol(expression, f.get());
		} else {
			
//			f = globalSymbolTable.lookup(packageQName, simpleName)(funcName);
			
		}
		
	}
	
	@Override
	public void endStringConstant(StringConstantExpression expression) {
	}
}
