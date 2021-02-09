package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.Statement;

public class AstIfElseStatement implements AstStatement {

	private Reporter reporter;

	private AstExpression ifExpression;
	private AstStatement ifBlock;
	private Optional<AstStatement> elseBlock;

	public AstIfElseStatement(Reporter reporter, AstExpression ifExpression, AstStatement ifBlock, Optional<AstStatement> elseBlock) {
		super();
		this.reporter = reporter;
		this.ifExpression = ifExpression;
		this.ifBlock = ifBlock;
		this.elseBlock = elseBlock;
	}
	public AstIfElseStatement(Reporter reporter, AstExpression ifExpression, AstStatement ifBlock) {
		super();
		this.reporter = reporter;
		this.ifExpression = ifExpression;
		this.ifBlock = ifBlock;
		this.elseBlock = Optional.empty();
	}

	public AstIfElseStatement withElse(AstStatement elseStmt) {
		return new AstIfElseStatement(reporter, ifExpression, ifBlock, Optional.of(elseStmt));
	}
	
	
	public AstExpression getIfExpression() {
		return ifExpression;
	}
	public AstStatement getIfBlock() {
		return ifBlock;
	}
	public Optional<AstStatement> getElseBlock() {
		return elseBlock;
	}
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startIfElseStatement(this);
		ifExpression.visit(visitor);
		ifBlock.visit(visitor);
		elseBlock.ifPresent(stmt -> stmt.visit(visitor));
		visitor.endIfElseStatement(this);
	}

	private IfElseStatement ifElseStatementImpl = null;
	
	@Override
	public IfElseStatement toAPI() {
		if(ifElseStatementImpl == null) {
			Expression apiIfExpression = ifExpression.getExpression();
			Statement apiIfStatement = ifBlock.toAPI();
			Optional<Statement> apiElseStatement = elseBlock.map(astStmt -> astStmt.toAPI());
			
			
			ifElseStatementImpl = new IfElseStatement() {

				@Override
				public Expression getExpression() {
					return apiIfExpression;
				}

				@Override
				public Statement getIfStatement() {
					return apiIfStatement;
				}

				@Override
				public Optional<Statement> getElseStatement() {
					return apiElseStatement;
				}
				
			};
		}
		return ifElseStatementImpl;
	}
	@Override
	public void verify(int featureFlags) {
		ifExpression.verify(featureFlags);
		ifBlock.verify(featureFlags);
		verifyOptional(elseBlock, "elseBlock", featureFlags);
		
	}

}
