package org.sirius.frontend.api;

import java.util.Optional;

public interface IfElseStatement extends Statement {
	Expression expression();
	Statement ifStatement();
	Optional<Statement> elseStatement();
}
