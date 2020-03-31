package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

/** Constructor call expression
 * 
 * @author jpragey
 *
 */
public interface ConstructorCall extends Expression {
	
//	Optional<TopLevelFunction> getDeclaration();
	
//	QName getName(); 
	List<Expression> getArguments();

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		for(Expression arg: getArguments())
			arg.visitMe(visitor);
		visitor.end(this);
	}

}
