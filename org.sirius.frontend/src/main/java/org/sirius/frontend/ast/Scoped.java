package org.sirius.frontend.ast;

import org.sirius.frontend.symbols.SymbolTable;

public interface Scoped {

	public SymbolTable getSymbolTable();
	
	default void setScopeName(String name) {getSymbolTable().setName(name);}
	
//	public String getScopeName();
	
}
