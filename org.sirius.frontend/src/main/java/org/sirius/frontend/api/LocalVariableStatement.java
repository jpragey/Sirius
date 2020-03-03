package org.sirius.frontend.api;

import java.util.Optional;

/** Method local variable
 * 
 * @author jpragey
 *
 */
public interface LocalVariableStatement extends Statement, AbstractValue {

	Optional<Expression> getInitialValue();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
//		visitContent(visitor);
		visitor.end(this);
	}

}
