package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

import org.sirius.frontend.api.Statement;
import org.sirius.frontend.apiimpl.BlockStatementImpl;
import org.sirius.frontend.symbols.Scope;

public class AstBlock implements AstStatement, Scoped {

	private Scope scope = null;
	private List<AstStatement> statements;
	
	public AstBlock(List<AstStatement> statements) {
		super();
		this.statements = statements;
	}
	
	public List<AstStatement> getStatements() {
		return statements;
	} 

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startBlock(this);
		for(AstStatement st: statements) {
			st.visit(visitor);
		}
		visitor.endBlock(this);
	}

	private Optional<Statement> impl;
	@Override
	public Optional<Statement> toAPI() {
		if(impl == null) {
			impl = Optional.of(new BlockStatementImpl());
		}
		return impl;
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(statements, featureFlags);
		
	}

	@Override
	public Scope getScope() {
		assert(this.scope != null);
		return this.scope;
	}

	@Override
	public void setScope2(Scope scope) {
		assert(scope != null);
		this.scope = scope;
	}
	
}
