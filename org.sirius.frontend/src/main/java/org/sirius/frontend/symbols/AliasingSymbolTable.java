package org.sirius.frontend.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstFunctionFormalArgument;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.ast.TypeFormalParameterDeclaration;

/** Symbol table that supports aliases, as in import statements.
 * 
 */
public class AliasingSymbolTable implements SymbolTable {
//	private Optional<SymbolTable> parent = Optional.empty();
	
	private Reporter reporter;
	
	private GlobalSymbolTable globalSymbolTable;
	
	/** Map locally declared simple name -> symbol */
	private Map<String, Symbol> symbolMap = new HashMap<>();
	
	public static class ImportedSymbol {
		/** Local alias, same as name if there's no alias */
//		private String alias;
		private QualifiedName pkqQname;
		private AstToken name;
		public ImportedSymbol(/*String alias, */QualifiedName pkqQname, AstToken name) {
			super();
//			this.alias = alias;
			this.pkqQname = pkqQname;
			this.name = name;
		}
		//	public String getAlias() {
		//		return alias;
		//	}
		public QualifiedName getPkqQname() {
			return pkqQname;
		}
		public AstToken getName() {
			return name;
		}
	}

	// Map localName (simple or alias) -> importedSymbol 
	private Map<String, ImportedSymbol> aliases = new HashMap<>();
	
	
	
//	private List<ImportedSymbol> importedSymbols = new ArrayList<>(); 
	
	
	public AliasingSymbolTable(Reporter reporter, GlobalSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
	}

//	@Override
//	public void setParentSymbolTable(SymbolTable parentTable) {
//		this.parent = Optional.of(parentTable);
//	}
	
	public Optional<Symbol> lookup(String simpleName) {
		Symbol s = symbolMap.get(simpleName);
		if(s != null) {
			return Optional.of(s);
		}
//		if(parent.isPresent()) {
//			return parent.get().lookup(simpleName);
//		}
		return Optional.empty();
	}
	
	/** Look up in the import declaration. It may be unresolved.
	 * 
	 * @param simpleName local name or alias
	 * @return
	 */
	public Optional<ImportedSymbol> lookupImport(String simpleName) {
		ImportedSymbol s = aliases.get(simpleName);
		if(s != null) {
			return Optional.of(s);
		}
//		if(parent.isPresent()) {
//			return parent.get().lookup(simpleName);
//		}
		return Optional.empty();
	}
	
	private void addSymbol(AstToken simpleName, /*Optional<AstToken> alias, */Symbol symbol) {
		String nameText = simpleName.getText();
		Symbol s = symbolMap.get(nameText);
		
		if(s != null) {
			// TODO
			reporter.error("Symbol " + simpleName.getText() + " soon defined "); 
		} else {
			symbolMap.put(nameText, symbol);
		}
	}
	
	public void addGlobalSymbol(QualifiedName pkqQname, AstToken simpleName, Optional<AstToken> alias) {
//		String nameText = simpleName.getText();
		
		ImportedSymbol importedSymbol = new ImportedSymbol(pkqQname, simpleName);
		// TODO: check duplicates
		if(alias.isPresent()) {
			aliases.put(alias.get().getText(), importedSymbol);
		} else {
			aliases.put(simpleName.getText(), importedSymbol);
		}
	}
	
	public void addClass(AstToken simpleName, AstClassDeclaration classDeclaration) {
		addSymbol(simpleName, new Symbol(simpleName, classDeclaration));
	}
	
	public void addImportSymbol(QualifiedName pkqQname, AstToken simpleName, Optional<AstToken> aliasName) {
		// Name local to the CU
		AstToken localName = aliasName.isPresent() ? aliasName.get() : simpleName;
		
		var symbol = new ImportedSymbol(pkqQname, simpleName);

		this.aliases.put(localName.getText(), symbol);
		
//		this.importedSymbols.add(new ImportedSymbol(localName.getText(), pkqQname, simpleName));
	}
	
	public void addFunction(AstToken simpleName, AstFunctionDeclaration declaration) {
		addSymbol(simpleName, new Symbol(simpleName, declaration));
	}
	
	/** Add type formal parameter */
	public void addFormalParameter(AstToken simpleName, TypeFormalParameterDeclaration formalParameter) {
		addSymbol(simpleName, new Symbol(simpleName, formalParameter));
	}

	/** Add function argument */
	public void addFunctionArgument(AstToken simpleName, AstFunctionFormalArgument formalArgument) {
		addSymbol(simpleName, new Symbol(simpleName, formalArgument));
	}


//	public List<ImportedSymbol> getImportedSymbols() {
//		return importedSymbols;
//	}

	
}
