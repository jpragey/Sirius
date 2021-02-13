package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.frontend.api.Statement;

public interface AstStatement extends Verifiable {

	public void visit(AstVisitor visitor);
	
	public Optional<Statement> toAPI();
	

}
