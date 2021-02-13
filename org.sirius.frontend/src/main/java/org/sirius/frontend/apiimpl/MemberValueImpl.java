package org.sirius.frontend.apiimpl;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;

public class MemberValueImpl implements MemberValue {
	private Type type;
	private Token name;

	private Optional<Expression> initialValue;

	public MemberValueImpl(Type type, Token name, Optional<Expression> initialValue) {
		super();
		this.type = type;
		this.name = name;
		this.initialValue = initialValue;
	}

	@Override
	public Type getType() {
		return type;
		//				return type.getApiType();
	}

	@Override
	public Token getName() {
		return name;
		//				return name.asToken();
	}

	@Override
	public Optional<Expression> getInitialValue() {
		return initialValue;
		//				return AstMemberValueDeclaration.this.getApiInitialValue();
	}
	@Override
	public String toString() {
		return "MemberValue: " + getType() + " " + getName().getText();
	}
}