package org.sirius.frontend.apiimpl;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstLocalVariableStatement;

public record LocalVariableStatementImpl(
		AstLocalVariableStatement stmt, 
		Token nameToken, 
		Optional<AstExpression> initialAstValue, 
		Type type
		) implements LocalVariableStatement 
{
	@Override
	public String toString() {
		return stmt.toString();
	}

	@Override
	public Token nameToken() {
		return nameToken;
	}

	@Override
	public Optional<Expression> initialValue() {
		return initialAstValue.flatMap(astExpr -> astExpr.getExpression());
	}

}
