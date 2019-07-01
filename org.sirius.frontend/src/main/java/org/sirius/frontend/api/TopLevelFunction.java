package org.sirius.frontend.api;

/** Top-level function declaration
 * 
 * @author jpragey
 *
 */
public interface TopLevelFunction extends AbstractFunction {

	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getArguments().forEach(arg -> arg.visitMe(visitor));
		getBodyStatements().forEach(arg -> arg.visitMe(visitor));
		visitor.end(this);
	}

}
