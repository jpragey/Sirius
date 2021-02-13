package org.sirius.frontend.api;

import java.util.Optional;

public interface MemberValueAccessExpression extends Expression {

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getContainerExpression().visitMe(visitor);
//		getContainerExpression().ifPresent(expr ->{
//			expr.visitMe(visitor);
//		});
		visitor.end(this);
	}

	Expression getContainerExpression();
	
	MemberValue getMemberValue();
}
