package org.sirius.frontend.api.testimpl;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;

public class MemberValueImpl implements MemberValue {
	private Type type;
	private Token qname;
	private Optional<Expression> initialValue;

	public MemberValueImpl(Type type, Token qname, Optional<Expression> initialValue) {
		super();
		this.type = type;
		this.qname = qname;
		this.initialValue = initialValue;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Token getName() {
		return qname;
	}

	@Override
	public Optional<Expression> getInitialValue() {
		return initialValue;
	}

}
