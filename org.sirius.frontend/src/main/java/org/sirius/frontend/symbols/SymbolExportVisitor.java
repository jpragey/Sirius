package org.sirius.frontend.symbols;

import java.util.Optional;
import java.util.Stack;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstFunctionDeclarationBuilder;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.AstStringConstantExpression;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.StandardCompilationUnit;

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
	public void startPartialList(PartialList partialList) {
		tableToFill.addFunction(partialList);
	}
	
}
