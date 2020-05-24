package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;

public interface AbstractFunction {

	QName getQName();

	/** Arguments declarations. For instance methods, the first arg must be 'this'.
	 * 
	 * @return
	 */
	List<FunctionFormalArgument> getArguments();
	
	Type getReturnType();

	Optional<List<Statement> > getBodyStatements();

	default void visitMe(Visitor visitor) {
		visitor.startAbstractFunction(this);
//		visitor.start(this);
		
//		getArguments().forEach(arg -> arg.visitMe(visitor));
		getArguments().forEach(arg -> arg.visitMe(visitor));
		
		getBodyStatements().ifPresent(
				stmtList -> stmtList.forEach(arg -> arg.visitMe(visitor))
				);
		
//		for(Statement stmt: getBodyStatements()) {
//			stmt.visitMe(visitor);
//		}

		//		visitor.end(this);
		visitor.endAbstractFunction(this);
	}

}
