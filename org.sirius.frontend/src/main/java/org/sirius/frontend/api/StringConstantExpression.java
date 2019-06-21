package org.sirius.frontend.api;

import org.sirius.common.core.Token;

public interface StringConstantExpression extends Expression {

	/** Get content as given in source code, eg with starting and terminating quotes. */
	Token getContent();
	
	/** Get processed code, without starting and ending quotes. */
	String getText();
	
}
