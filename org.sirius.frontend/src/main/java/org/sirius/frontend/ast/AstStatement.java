package org.sirius.frontend.ast;

import org.sirius.frontend.api.Statement;

public interface AstStatement extends Verifiable {

	public void visit(AstVisitor visitor);
	
	public Statement toAPI();
	

}
