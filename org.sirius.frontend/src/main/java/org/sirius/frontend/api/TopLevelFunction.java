package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

/** Top-level function declaration
 * 
 * @author jpragey
 *
 */
public interface TopLevelFunction extends AbstractFunction {

	
	List<Statement> getBodyStatements();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getArguments().forEach(arg -> arg.visitMe(visitor));
		visitor.end(this);
	}

}
