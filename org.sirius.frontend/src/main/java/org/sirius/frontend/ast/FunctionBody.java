package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

public class FunctionBody implements Verifiable{
	private List<AstStatement> statements;

	public FunctionBody(List<AstStatement> statements) {
		super();
		this.statements = statements;
	}

	public List<AstStatement> getStatements() {
		return statements;
	}

	public int getStatementSize() {
		return statements.size();
	}
	public AstStatement getStatement(int pos) {
		int statementSize = statements.size();
		if(pos >= statementSize)
			throw new AssertionError("Get statement at position " + pos + " >= size of list (" + statementSize + ")");
		
		return statements.get(pos);
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(statements, featureFlags);
	}
	
}
