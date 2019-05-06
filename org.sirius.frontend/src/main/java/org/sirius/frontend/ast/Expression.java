package org.sirius.frontend.ast;

import java.util.Optional;

public interface Expression {
	
	/** Expression type. Note that it may be absent at parsing type (returns null), at least for function call expression,
	 *  
	 * */
	public Optional<Type> getType();
	
	public void visit(AstVisitor visitor);

}
