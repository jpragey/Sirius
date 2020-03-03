package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public class AstLocalVariableStatement implements AstStatement {

	private AstValueDeclaration declaration;

	public AstLocalVariableStatement(AstValueDeclaration declaration) {
		super();
		this.declaration = declaration;
	}

	public AstValueDeclaration getValueDeclaration() {
		return declaration;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		declaration.visit(visitor);
		visitor.end(this);
	}

	@Override
	public Statement toAPI() {
		return new LocalVariableStatement() {

			@Override
			public Type getType() {
				return declaration.getType().getApiType();
			}

			@Override
			public Token getName() {
				return declaration.getName();
			}

			@Override
			public Optional<Expression> getInitialValue() {
				return declaration.getApiInitialValue();
			}
		};
	}
	
}


