package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;

public interface AbstractFunction {

	QName qName();

	/** for member function only
	 * 
	 * @return
	 */
	Optional<QName> getClassOrInterfaceContainerQName();
	
	/** Arguments declarations. For instance methods, the first arg must be 'this'.
	 * 
	 * @return
	 */
	List<FunctionParameter> parameters();
	
	Type returnType();

	Optional<List<Statement> > bodyStatements();

	default void visitMe(Visitor visitor) {
		visitor.startAbstractFunction(this);
//		visitor.start(this);
		
//		getArguments().forEach(arg -> arg.visitMe(visitor));
		parameters().forEach(arg -> arg.visitMe(visitor));
		
		bodyStatements().ifPresent(
				stmtList -> stmtList.forEach(arg -> {
					arg.visitMe(visitor);
					})
				);
//		getBodyStatements().ifPresent(
//				stmtList -> stmtList.forEach(arg -> arg.visitMe(visitor))
//				);
		
//		for(Statement stmt: getBodyStatements()) {
//			stmt.visitMe(visitor);
//		}

		//		visitor.end(this);
		visitor.endAbstractFunction(this);
	}

}
