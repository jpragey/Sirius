package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;

public interface AstExpression {
	
	/** Expression type. Note that it may be absent at parsing type (returns null), at least for function call expression,
	 *  
	 * */
	AstType getType();
	
	void visit(AstVisitor visitor);
	
	/**
	 * 
	 * @return the API expression, empty if it can't be defined (eg syntax error)
	 */
	Expression getExpression();

	String asString();
	@Override
	String toString();
}
