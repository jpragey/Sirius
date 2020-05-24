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
}
