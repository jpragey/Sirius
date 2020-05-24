package org.sirius.frontend.api;

import java.util.Optional;

public interface IfElseStatement extends Statement {
	Expression getExpression();
	Statement getIfStatement();
	Optional<Statement> getElseStatement();
}
