package org.sirius.frontend.symbols;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ClassDeclaration;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.ValueDeclaration;

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

	static class Key {
		QName packageQName;
		String simpleName;
		public Key(QName packageQName, String simpleName) {
			super();
			this.packageQName = packageQName;
			this.simpleName = simpleName;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((packageQName == null) ? 0 : packageQName.hashCode());
			result = prime * result + ((simpleName == null) ? 0 : simpleName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (packageQName == null) {
				if (other.packageQName != null)
					return false;
			} else if (!packageQName.equals(other.packageQName))
				return false;
			if (simpleName == null) {
				if (other.simpleName != null)
					return false;
			} else if (!simpleName.equals(other.simpleName))
				return false;
			return true;
		}
		
	}
	
	private Map<Key, Symbol> symbols = new HashMap<>();
	
	public void addSymbol(QName packageQName, AstToken simpleName, Symbol symbol) {
		Key key = new Key(packageQName, simpleName.getText());
		symbols.put(key, symbol);
	}
	
	public void addClass(QName packageQName, ClassDeclaration classDeclaration) {
		AstToken simpleName = classDeclaration.getName();
		addSymbol(packageQName, simpleName, new Symbol(simpleName, classDeclaration));
	}
	
	public void addFunction(QName packageQName, FunctionDeclaration functionDeclaration) {
		AstToken simpleName = functionDeclaration.getName();
		addSymbol(packageQName, simpleName, new Symbol(simpleName, functionDeclaration));
	}
	
	/** Top-level value */
	public void addValue(QName packageQName, ValueDeclaration valueDeclaration) {
		AstToken simpleName = valueDeclaration.getName();
		addSymbol(packageQName, simpleName, new Symbol(simpleName, valueDeclaration));
	}

	public Optional<Symbol> lookup(QName packageQName, String simpleName) {
		Key key = new Key(packageQName, simpleName);
		Symbol symbol = symbols.get(key);
		Optional<Symbol> s = Optional.ofNullable(symbol);
		return s;
	}
}
