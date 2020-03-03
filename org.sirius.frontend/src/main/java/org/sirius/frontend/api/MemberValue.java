package org.sirius.frontend.api;

import java.util.Optional;

/** Class member value
 * 
 * @author jpragey
 *
 */
public interface MemberValue extends AbstractValue {
	
	Optional<Expression> getInitialValue();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

}
