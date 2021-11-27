package org.sirius.frontend.api;

import java.util.Optional;

public interface Expression {
	void visitMe(Visitor visitor); 
	
	Type type();
}
