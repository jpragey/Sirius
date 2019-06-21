package org.sirius.frontend.api;

import org.sirius.common.core.Token;

/** Function call expression
 * 
 * @author jpragey
 *
 */
public interface FunctionCall extends Expression {
	
	Token getFunctionName(); 
}
