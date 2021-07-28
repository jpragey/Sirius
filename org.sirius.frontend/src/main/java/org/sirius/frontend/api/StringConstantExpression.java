package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.core.Token;
import org.sirius.frontend.sdk.SdkContent;

public interface StringConstantExpression extends Expression {

	static Type type = new ClassType() {
		
		@Override
		public QName getQName() {
			return SdkContent.siriusLangStringQName;
		}
		@Override
		public boolean isAncestorOrSame(Type type) {
			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
		}
		// TODO: check ???
		@Override
		public List<MemberValue> getMemberValues() {
			return List.of();
		}

		// TODO: check ???
		@Override
		public List<AbstractFunction> getFunctions() {
			return List.of();
		}
		@Override
		public Optional<ExecutionEnvironment> getExecutionEnvironment() {
			return Optional.empty();
		}

	};
	
	/** Get content as given in source code, eg with starting and terminating quotes. */
	Token getContent();
	
	/** Get processed code, without starting and ending quotes. */
	String getText();

	@Override
	default Type getType() {
		return type;
	}
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

	
}
