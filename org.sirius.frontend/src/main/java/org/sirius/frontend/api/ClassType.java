package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
/** Class or interface
 * 
 * @author jpragey
 *
 */
public interface ClassType extends Type {

	List<MemberValue> memberValues();
	List<AbstractFunction> memberFunctions();

	QName qName();
	
	/** Return the ExecutionEnvironment if class is callable.
	 * 
	 * @return
	 */
	Optional<ExecutionEnvironment> executionEnvironment();
	
	default void visitContent(Visitor visitor) {
		
		for(MemberValue mv: memberValues()) {
			mv.visitMe(visitor);
		}
		
		for(AbstractFunction fct: memberFunctions()) {
			fct.visitMe(visitor);
		}
	}

	default void visitMe(Visitor visitor) {
		visitor.startClassType(this);
		visitContent(visitor);
		visitor.endClassType(this);
	}


}
