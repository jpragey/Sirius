package org.sirius.frontend.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionFormalArgument;
import org.sirius.frontend.ast.TypeFormalParameterDeclaration;

public class LocalSymbolTable implements SymbolTable {
	private Optional<SymbolTable> parent = Optional.empty();
	
	private Reporter reporter;
	
	/** Map simple or qualified name -> symbol */
	private Map<String, Symbol> symbolMap = new HashMap<>();
	
	public LocalSymbolTable(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

//	@Override
	public void setParentSymbolTable(SymbolTable parentTable) {
		this.parent = Optional.of(parentTable);
	}
	
	public Optional<Symbol> lookup(String simpleName) {
		Symbol s = symbolMap.get(simpleName);
		if(s != null) {
			return Optional.of(s);
		}
		if(parent.isPresent()) {
			return parent.get().lookup(simpleName);
		}
		return Optional.empty();
	}
	
	private void addSymbol(AstToken simpleName, Symbol symbol) {
		String nameText = simpleName.getText();
		Symbol s = symbolMap.get(nameText);
		
		if(s != null) {
			 // TODO
			reporter.error("Symbol " + simpleName.getText() + " soon defined ");
		} else {
			symbolMap.put(nameText, symbol);
		}
	}
	
	public void addClass(AstToken simpleName, AstClassDeclaration classDeclaration) {
		addSymbol(simpleName, new Symbol(simpleName, classDeclaration));
	}
	
	public void addFunction(AstToken simpleName, AstFunctionDeclaration declaration) {
		addSymbol(simpleName, new Symbol(simpleName, declaration));
	}
	
	/** Add type formal parameter */
	public void addFormalParameter(AstToken simpleName, TypeFormalParameterDeclaration formalParameter) {
		addSymbol(simpleName, new Symbol(simpleName, formalParameter));
	}

	/** Add function argument */
	public void addFunctionArgument(AstToken simpleName, FunctionFormalArgument formalArgument) {
		addSymbol(simpleName, new Symbol(simpleName, formalArgument));
	}

}
