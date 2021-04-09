package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.SymbolTable;
import org.sirius.frontend.symbols.SymbolTableImpl;

public interface AstExpression extends Verifiable {
	
	
	AstExpression linkToParentST(SymbolTable parentSymbolTable);
	
	/** Expression type. Note that it may be absent at parsing type (returns null), at least for function call expression,
	 *  
	 * */
	AstType getType();
	
	void visit(AstVisitor visitor);
	
	/**
	 * 
	 * @return the API expression, empty if it can't be defined (eg syntax error)
	 */
	Optional<Expression> getExpression();

	String asString();
	
	@Override
	String toString();
}
