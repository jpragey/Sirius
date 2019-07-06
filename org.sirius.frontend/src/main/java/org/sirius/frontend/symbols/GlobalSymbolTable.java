package org.sirius.frontend.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstValueDeclaration;

/**
 * Table of all symbols that can be accessed globally (in external package or module).
 * 
 * It's basically a map key -> symbol, where a symbol can be 
 * 
 * ClassDeclaration (top-level / nested)		package	nestedClassName?
 * FunctionDeclaration (top-level / member)		package	nestedClassName? functionName
 * ValueDeclaration (top-level / member)		package	nestedClassName? valueName
 * 
 * @author jpragey
 *
 */
public class GlobalSymbolTable /*implements SymbolTable */{

	private Map<QName, Symbol> symbols = new HashMap<>();
	
	public void addSymbol(QName symbolQName, Symbol symbol) {
		symbols.put(symbolQName, symbol);
	}
	
	public void addClass(AstClassDeclaration classDeclaration) {
		AstToken simpleName = classDeclaration.getName();
		QName classQName = classDeclaration.getQName();
		addSymbol(classQName, new Symbol(simpleName, classDeclaration));
	}
	
	public void addFunction(AstFunctionDeclaration functionDeclaration) {
		AstToken simpleName = functionDeclaration.getName();
		QName funcQName = functionDeclaration.getQName();
		addSymbol(funcQName, new Symbol(simpleName, functionDeclaration));
	}
	
	/** Top-level value */
	public void addValue(AstValueDeclaration valueDeclaration) {
		AstToken simpleName = valueDeclaration.getName();
////		addSymbol(packageQName, simpleName, new Symbol(simpleName, valueDeclaration));	// TODO
	}

//	public Optional<Symbol> lookup(QName packageQName, String simpleName) {
	public Optional<Symbol> lookup(QName symbolQName) {
		Symbol symbol = symbols.get(symbolQName);
		Optional<Symbol> s = Optional.ofNullable(symbol);
		return s;
	}
}
