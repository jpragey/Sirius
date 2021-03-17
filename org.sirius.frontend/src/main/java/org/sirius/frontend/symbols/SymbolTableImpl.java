package org.sirius.frontend.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.ast.TypeParameter;

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
public class SymbolTableImpl implements SymbolTable {

	private HashMap<QName, Symbol> symbols = new HashMap<>();
	
	private HashMap<String, Symbol> symbolsBySimpleName = new HashMap<>();
	
	private Optional<SymbolTable> parent;
	
	/** Name of the container (debug only) */
	private String dbgName;
	
	public SymbolTableImpl(Optional<SymbolTable> parent, String dbgName) {
		super();
		this.parent = parent;
		this.dbgName = dbgName;
	}
	public SymbolTableImpl(String dbgName) {
		this(Optional.empty(), dbgName);
	}

	
	public String getDbgName() {
		return dbgName;
	}
//	public void setDbgName(String dbgName) {
//		this.dbgName = dbgName;
//	}
	public void addSymbol(QName symbolQName, Symbol symbol) {
		assert(symbolQName != null);
		symbols.put(symbolQName, symbol);
		symbolsBySimpleName.put(symbolQName.getLast(), symbol);
	}
	
	
	public void addClass(AstClassDeclaration classDeclaration) {
		AstToken simpleName = classDeclaration.getName();
		QName classQName = classDeclaration.getQName();
		addSymbol(classQName, new Symbol(simpleName, classDeclaration));
	}
	
	public void addInterface(AstInterfaceDeclaration classDeclaration) {
		AstToken simpleName = classDeclaration.getName();
		QName classQName = classDeclaration.getQName();
		addSymbol(classQName, new Symbol(simpleName, classDeclaration));
	}
	
	public void addFunction(FunctionDefinition functionDeclaration) {
		AstToken simpleName = functionDeclaration.getName();
		QName funcQName = functionDeclaration.getqName();
		addSymbol(funcQName, new Symbol(simpleName, functionDeclaration));
	}

	/** Add type formal parameter */
	public void addFormalParameter(QName containerQName, TypeParameter formalParameter) {
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
		
	}

//	private Optional<AstFunctionFormalArgument> functionArgument = Optional.empty();
	/** Local variable */
	public void addFunctionArgument(AstFunctionParameter functionArgument) {
		AstToken simpleName = functionArgument.getName();

		// TODO: add symbol in qname-based 'symbols' map 
		symbolsBySimpleName.put(simpleName.getText(), new Symbol(simpleName, functionArgument));
		
	}
	
	
	public Optional<Symbol> lookupByQName(QName symbolQName) {
		Symbol symbol = symbols.get(symbolQName);
		
		if(symbol == null && parent.isPresent()) {
			return parent.get().lookupByQName(symbolQName);
		}
		Optional<Symbol> s = Optional.ofNullable(symbol);
		return s;
	}

	@Override
	public Optional<Symbol> lookupBySimpleName(String simpleName) {
		Symbol symbol = symbolsBySimpleName.get(simpleName);

		if(symbol == null && parent.isPresent()) {
			return parent.get().lookupBySimpleName(simpleName);
		}
		return Optional.ofNullable(symbol);
	}

	public Optional<AstClassDeclaration> lookupClassDeclaration(String simpleName) {
		Symbol symbol = symbolsBySimpleName.get(simpleName);

		if(symbol == null && parent.isPresent()) {
			return parent.get().lookupClassDeclaration(simpleName);
		}
		if(symbol == null) {
			return Optional.empty();
		}
		
		return symbol.getClassDeclaration();
	}

	public Optional<AstFunctionParameter> lookupFunctionArgument(String simpleName) {
		Symbol symbol = symbolsBySimpleName.get(simpleName);

		if(symbol == null && parent.isPresent()) {
			return parent.get().lookupFunctionArgument(simpleName);
		}
		if(symbol == null) {
			return Optional.empty();
		}
		
		return symbol.getFunctionArgument();
	}

	public Optional<FunctionDefinition> lookupPartialList(String simpleName) {	// TODO:rename
		Symbol symbol = symbolsBySimpleName.get(simpleName);

		if(symbol == null && parent.isPresent()) {
			return parent.get().lookupPartialList(simpleName);
		}
		if(symbol == null) {
			return Optional.empty();
		}
		
		return symbol.getFunctionDeclaration();
	}

	public Optional<AstMemberValueDeclaration> lookupValue(String simpleName) {
		Symbol symbol = symbolsBySimpleName.get(simpleName);

		if(symbol == null && parent.isPresent()) {
			return parent.get().lookupValue(simpleName);
		}
		if(symbol == null) {
			return Optional.empty();
		}
		
		return symbol.getValueDeclaration();
	}

	
	public Optional<AstLocalVariableStatement> lookupLocalVariable(String simpleName) {
		Symbol symbol = symbolsBySimpleName.get(simpleName);

		if(symbol == null && parent.isPresent()) {
			return parent.get().lookupLocalVariable(simpleName);
		}
		if(symbol == null) {
			return Optional.empty();
		}
		
		return symbol.getLocalVariableStatement();
	}
	
	public Optional<AstInterfaceDeclaration> lookupInterfaceDeclaration(String simpleName) {
		Symbol symbol = symbolsBySimpleName.get(simpleName);

		if(symbol == null && parent.isPresent()) {
			return parent.get().lookupInterfaceDeclaration(simpleName);
		}
		if(symbol == null) {
			return Optional.empty();
		}
		
		return symbol.getInterfaceDeclaration();
	}

	public void forEach( BiConsumer<QName, Symbol> action) {
		symbols.forEach(action);;
	}

	
	public void addImportSymbol(QualifiedName pkgQname, ImportDeclarationElement e) {
//		private HashMap<String, Symbol> symbolsBySimpleName = new HashMap<>();
		// import org.example.metasyntax { ExampleFoo=Foo, Bar } :
		//	Bar => org.example.metasyntax.Bar
		//	ExampleFoo => org.example.metasyntax.Foo
		
		AstToken simpleName = e.getImportedTypeName();
		Optional<AstToken> aliasName = e.getAlias();
		
		AstToken effectiveNameTk = aliasName.orElse(simpleName);
		String effectiveName = effectiveNameTk.getText();
		
		QName symbolQName = pkgQname.toQName().child(simpleName.getText());

		ImportedSymbol is = new ImportedSymbol(simpleName, symbolQName, e);
		Symbol s = new Symbol(simpleName, is);
		
		symbolsBySimpleName.put(effectiveName, s);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ST:");
		sb.append(dbgName);
		sb.append(": (");
		sb.append(symbolsBySimpleName.entrySet().size());
		sb.append(" symbols): ");
		
		for(Map.Entry<String, Symbol> s: symbolsBySimpleName.entrySet()) {
			sb.append(s.getKey());
			sb.append("->");
			sb.append(s.getValue());
			sb.append(", ");
		}
		String s = sb.toString();
//		String s = symbolsBySimpleName.entrySet().stream()
//			.map(entry -> entry.getKey().toString() + "->" + entry.getValue())
//			.collect(Collectors.joining(","));
		
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
		
		print.accept(prefix + dbgName + ": " + this.getClass());
		for(Map.Entry<String, Symbol> e : symbolsBySimpleName.entrySet()) {
			print.accept("  " + e.getKey() + " => " + e.getValue());
		}
		parent.ifPresent(p-> p.dump(prefix+"parent: ", print));
	}
	
}
