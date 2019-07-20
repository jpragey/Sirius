package org.sirius.frontend.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstValueDeclaration;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.ast.TypeFormalParameterDeclaration;

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
public class DefaultSymbolTable implements SymbolTable {

	private HashMap<QName, Symbol> symbols = new HashMap<>();
	
	private HashMap<String, Symbol> symbolsBySimpleName = new HashMap<>();
	
	private Optional<DefaultSymbolTable> parent;
	
	
	public DefaultSymbolTable(DefaultSymbolTable parent) {
		super();
		this.parent = Optional.ofNullable(parent);
	}
	public DefaultSymbolTable() {
		super();
		this.parent = Optional.empty();
	}

	public void addSymbol(QName symbolQName, Symbol symbol) {
		symbols.put(symbolQName, symbol);
		symbolsBySimpleName.put(symbolQName.getLast(), symbol);
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

	/** Add type formal parameter */
	public void addFormalParameter(QName containerQName, TypeFormalParameterDeclaration formalParameter) {
		AstToken paramName = formalParameter.getFormalName();
		QName paramQName = containerQName.child(paramName.getText());
		addSymbol(paramQName, new Symbol(paramName, formalParameter));
	}

	/** Top-level value */
	public void addValue(AstValueDeclaration valueDeclaration) {
		AstToken simpleName = valueDeclaration.getName();
////		addSymbol(packageQName, simpleName, new Symbol(simpleName, valueDeclaration));	// TODO
	}

//	public Optional<Symbol> lookup(QName packageQName, String simpleName) {
	public Optional<Symbol> lookup(QName symbolQName) {
		Symbol symbol = symbols.get(symbolQName);
		
		if(symbol == null && parent.isPresent()) {
			return parent.get().lookup(symbolQName);
		}
		Optional<Symbol> s = Optional.ofNullable(symbol);
		return s;
	}

	@Override
	public Optional<Symbol> lookup(String simpleName) {
		Symbol symbol = symbolsBySimpleName.get(simpleName);

		if(symbol == null && parent.isPresent()) {
			return parent.get().lookup(simpleName);
		}
		return Optional.ofNullable(symbol);
	}

	public void forEach( BiConsumer<QName, Symbol> action) {
		symbols.forEach(action);;
	}

	public void addImportSymbol(QualifiedName pkgQname, AstToken simpleName, Optional<AstToken> aliasName) {
		throw new UnsupportedOperationException("addImportSymbol() not supported yet.");
	}
	
	@Override
	public String toString() {
		String s = symbols.entrySet().stream()
			.map(entry -> entry.getKey().toString() + "->" + entry.getValue())
			.collect(Collectors.joining(","));
		if(parent.isPresent()) {
			s = s + "\n=>" +parent.get().toString();
		}
		
		return s;
	}
}
