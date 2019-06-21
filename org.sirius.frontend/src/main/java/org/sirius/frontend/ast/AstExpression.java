package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Expression;

public interface AstExpression {
	
	/** Expression type. Note that it may be absent at parsing type (returns null), at least for function call expression,
	 *  
	 * */
	public Optional<Type> getType();
	
	public void visit(AstVisitor visitor);
	
	Expression getExpression();

}
