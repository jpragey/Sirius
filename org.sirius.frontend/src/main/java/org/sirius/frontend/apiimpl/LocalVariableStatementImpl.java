package org.sirius.frontend.apiimpl;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstLocalVariableStatement;

public class LocalVariableStatementImpl implements LocalVariableStatement {
	private AstLocalVariableStatement stmt;
	private Type type;
	private Token varName;
	private Optional<AstExpression> initialValue;

	public LocalVariableStatementImpl(AstLocalVariableStatement stmt, Token varName, Optional<AstExpression> initialValue, Type type) {
		super();
		this.stmt = stmt;
		//			AstLocalVariableStatement.this.type.resolve();
		this.type = type;
		this.varName = varName;
		this.initialValue = initialValue;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Token getName() {
		return varName;
	}

	@Override
	public Optional<Expression> getInitialValue() {
		if(initialValue.isPresent()) {
			Optional<Expression> exp = initialValue.flatMap(e -> e.getExpression());
			return exp;
		}
		return Optional.empty();
	}
	@Override
	public String toString() {
		return stmt.toString();
	}

}