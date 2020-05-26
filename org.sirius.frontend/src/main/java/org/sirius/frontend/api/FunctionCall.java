package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.Token;

/** Function call expression
 * 
 * @author jpragey
 *
 */
public interface FunctionCall extends Expression {
	
	Optional<AbstractFunction> getDeclaration();
	
	Token getFunctionName(); 
	
	List<Expression> getArguments();
	
	/** 'this', for instance methods */
	Optional<Expression> getThis();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		
		for(Expression e: getArguments())
			e.visitMe(visitor);
		
		visitor.end(this);
	}

	
}
