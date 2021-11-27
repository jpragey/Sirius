package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.core.Token;
import org.sirius.frontend.sdk.SdkContent;

public interface StringConstantExpression extends Expression {

	static Type type = new ClassType() {
		
		@Override
		public QName qName() {
			return SdkContent.siriusLangStringQName;
		}
		// TODO: check ???
		@Override
		public List<MemberValue> memberValues() {
			return List.of();
		}

		// TODO: check ???
		@Override
		public List<AbstractFunction> memberFunctions() {
			return List.of();
		}
		@Override
		public Optional<ExecutionEnvironment> executionEnvironment() {
			return Optional.empty();
		}

	};
	
	/** Get content as given in source code, eg with starting and terminating quotes. */
	Token getContent();
	
	/** Get processed code, without starting and ending quotes. */
	String getText();

	@Override
	default Type type() {
		return type;
	}
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

	
}
