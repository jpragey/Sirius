package org.sirius.frontend.ast;

import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.SymbolTable;

public interface Scoped {

	public Scope getScope();
	default SymbolTable getSymbolTable() {return getScope().getSymbolTable();}
	
	default void setScopeName(String name) {getScope().getSymbolTable().setName(name);}
	
	void setScope2(Scope scope);
}
