package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

/** Class constructor declaration
 * 
 * @author jpragey
 *
 */
public interface ConstructorDeclaration {

	QName getQName();

	List<FunctionFormalArgument> getArguments();
	
	List<Statement> getBodyStatements();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getArguments().forEach(arg -> arg.visitMe(visitor));
		getBodyStatements().forEach(arg -> arg.visitMe(visitor));
		visitor.end(this);
	}

}
