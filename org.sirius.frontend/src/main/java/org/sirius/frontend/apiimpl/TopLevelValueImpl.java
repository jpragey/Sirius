package org.sirius.frontend.apiimpl;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.TopLevelValue;
import org.sirius.frontend.api.Type;

public class TopLevelValueImpl implements TopLevelValue {
	private Type type;
	private Token name;
	private Optional<Expression> initialValue;
	
	public TopLevelValueImpl(Type type, Token name, Optional<Expression> initialValue) {
		super();
		this.type = type;
		this.name = name;
		this.initialValue = initialValue;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Token getName() {
		return name;
	}

	@Override
	public Optional<Expression> getInitialValue() {
		return initialValue;
	}
	@Override
	public String toString() {
		return "TopLevelValue: " + getType() + " " + getName().getText();
	}
	
}