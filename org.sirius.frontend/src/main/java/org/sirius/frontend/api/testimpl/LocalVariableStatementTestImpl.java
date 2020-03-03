package org.sirius.frontend.api.testimpl;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.Type;

public class LocalVariableStatementTestImpl implements LocalVariableStatement {
	
	private Type type;
	private Token name;
	private Optional<Expression> initialValue;
	

	public LocalVariableStatementTestImpl(Type type, Token name, Optional<Expression> initialValue) {
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


}
