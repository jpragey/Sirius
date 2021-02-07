package org.sirius.frontend.symbols;

import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;

/** Visitor that sets the 'parent' symbol table field throughout the AST.
 * 
 * @author jpragey
 *
 */
public class SymbolExportVisitor implements AstVisitor {
	private ExportedSymbolTable tableToFill = new ExportedSymbolTable();
	
	public SymbolExportVisitor(ExportedSymbolTable tableToFill) {
		super();
		this.tableToFill = tableToFill;
		
	}
	public SymbolExportVisitor() {
		this(new ExportedSymbolTable() );
		
	}
	
	public ExportedSymbolTable getTableToFill() {
		return tableToFill;
	}
	
	@Override
	public void startInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		tableToFill.addInterface(interfaceDeclaration);
	}
	
	@Override
	public void startClassDeclaration(AstClassDeclaration classDeclaration) {
		tableToFill.addClass(classDeclaration);
	}
	
	@Override
	public void startFunctionDefinition(FunctionDefinition functionDefinition) {
		tableToFill.addFunctionDefinition(functionDefinition);;
	}
	@Override
	public void startFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		tableToFill.addFunctionDeclaration(functionDeclaration);
	}
	
}
