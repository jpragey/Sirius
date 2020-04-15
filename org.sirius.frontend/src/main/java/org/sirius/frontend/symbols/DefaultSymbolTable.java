package org.sirius.frontend.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
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
	public void addValue(AstMemberValueDeclaration valueDeclaration) {
		AstToken simpleName = valueDeclaration.getName();

		// TODO: add symbol in qname-based 'symbols' map 
		symbolsBySimpleName.put(simpleName.getText(), new Symbol(simpleName, valueDeclaration));
		
//		addSymbol(packageQName, simpleName, new Symbol(simpleName, valueDeclaration));
	}

	/** Local variable */
	public void addLocalVariable(AstLocalVariableStatement localVariableDeclaration) {
		AstToken simpleName = localVariableDeclaration.getVarName();

		// TODO: add symbol in qname-based 'symbols' map 
		symbolsBySimpleName.put(simpleName.getText(), new Symbol(simpleName, localVariableDeclaration));
		
//		addSymbol(packageQName, simpleName, new Symbol(simpleName, valueDeclaration));
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

	
	public void addImportSymbol(QualifiedName pkgQname, ImportDeclarationElement e /* AstToken simpleName, Optional<AstToken> aliasName*/) {
//		private HashMap<String, Symbol> symbolsBySimpleName = new HashMap<>();
		// import org.example.metasyntax { ExampleFoo=Foo, Bar } :
		//	Bar => org.example.metasyntax.Bar
		//	ExampleFoo => org.example.metasyntax.Foo
		
		AstToken simpleName = e.getImportedTypeName();
		Optional<AstToken> aliasName = e.getAlias();
		
		AstToken effectiveNameTk = aliasName.orElse(simpleName);
		String effectiveName = effectiveNameTk.getText();
		
		QName symbolQName = pkgQname.toQName().child(simpleName.getText());

//		addSymbol(paramQName, new Symbol(paramName, formalParameter));
//
//		symbols.put(symbolQName, symbol);
		
		ImportedSymbol is = new ImportedSymbol(simpleName, symbolQName, e);
		Symbol s = new Symbol(simpleName, is);
		
		 
		symbolsBySimpleName.put(effectiveName, s);

//		throw new UnsupportedOperationException("addImportSymbol() not supported yet.");
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
	
	public void dump() {
		dump(System.out::println);
	}
	public void dump(Consumer<String> print) {
		dump("Symbol table: ", print);
	}
	public void dump(String prefix, Consumer<String> print) {
		
		print.accept(prefix + this.getClass());
		for(Map.Entry<String, Symbol> e : symbolsBySimpleName.entrySet()) {
			print.accept("  " + e.getKey() + " => " + e.getValue());
		}
		parent.ifPresent(p-> p.dump(prefix+"parent: ", print));
	}
	
}
