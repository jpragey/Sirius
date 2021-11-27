package org.sirius.frontend.api;

import java.util.Optional;

public interface Expression {
	void visitMe(Visitor visitor); 
	
	// TODO: should be optional, (TODO: done) 
	Type getType();
}
