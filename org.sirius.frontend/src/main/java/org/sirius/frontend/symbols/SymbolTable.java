package org.sirius.frontend.symbols;

import java.util.Optional;

public interface SymbolTable {
	public Optional<Symbol> lookupBySimpleName(String simpleName);
}
