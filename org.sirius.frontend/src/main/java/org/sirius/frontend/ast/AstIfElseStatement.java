package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.apiimpl.IfElseStatementImpl;

public class AstIfElseStatement implements AstStatement {

	private Reporter reporter;

	private AstExpression ifExpression;
	private AstStatement ifBlock;
	private Optional<AstStatement> elseBlock;

	private Optional<Statement> ifElseStatementImpl = null;

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

	
	@Override
	public Optional<Statement> toAPI() {
		if(ifElseStatementImpl == null) {
			// -- if-expression (mandatory)
			Optional<Expression> optIfExpression = ifExpression.getExpression();
			if(optIfExpression.isEmpty()) {
				ifElseStatementImpl = Optional.empty();
				return Optional.empty();
			}
			Expression apiIfExpression = optIfExpression.get();
			
			
			Optional<Statement> optIfStatement = ifBlock.toAPI();
			if(optIfStatement.isEmpty()) {
				ifElseStatementImpl = Optional.empty();
				return Optional.empty();
			}
			Statement apiIfStatement = optIfStatement.get();
			
			
//			Optional<Statement> apiElseStatement = elseBlock.map(astStmt -> astStmt.toAPI());
			Optional<Statement> apiElseStatement = elseBlock.flatMap(astStmt -> astStmt.toAPI());
			
			Statement st = new IfElseStatementImpl(apiIfExpression, apiIfStatement, apiElseStatement);
			ifElseStatementImpl = Optional.of(st);
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
