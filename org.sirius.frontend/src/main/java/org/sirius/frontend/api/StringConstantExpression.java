package org.sirius.frontend.api;

import org.sirius.common.core.QName;
import org.sirius.common.core.Token;

public interface StringConstantExpression extends Expression {

	static QName typeClassName = new QName("sirius", "lang", "String");
	static StringType type = new StringType() {
		
		@Override
		public QName getQName() {
			return typeClassName;
		}
	};
	
	/** Get content as given in source code, eg with starting and terminating quotes. */
	Token getContent();
	
	/** Get processed code, without starting and ending quotes. */
	String getText();

	@Override
	default StringType getType() {
		return type;
	}
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

	
}
