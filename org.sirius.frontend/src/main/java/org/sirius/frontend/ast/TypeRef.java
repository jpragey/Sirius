package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.symbols.SymbolTableImpl;
import org.sirius.frontend.symbols.Symbol;

/** most simple (class or interface) type 
 * 
 * @author jpragey
 *
 */
public final class TypeRef  {
	private AstToken name;

	/**  */
//	private AstType effectiveType;
	private Optional<AstClassDeclaration> effectiveType;
	
	public TypeRef(AstToken name) {
		super();
		this.name = name;
	}

	public AstToken getName() {
		return name;
	}
	
	/** Type from symbol table, null if not found or not evaluated yet */
	public Optional<AstClassDeclaration> getEffectiveType() {
		return effectiveType;
	}

	public void setSymbolTable(SymbolTableImpl symbolTable) {
		this.effectiveType = getClassDeclaration(symbolTable);
	}
	
	@Override
	public String toString() {
		return name.getText();
	}
	
	private Optional<AstClassDeclaration> getClassDeclaration(SymbolTableImpl symbolTable) {
		Optional<Symbol> optSymbol = symbolTable.lookupBySimpleName(name.getText());
		if(optSymbol.isPresent()) {
			Symbol symbol = optSymbol.get();
			Optional<AstClassDeclaration> optClassDeclaration = symbol.getClassDeclaration();
			return optClassDeclaration;
		}
		return Optional.empty();
	}
	
}
