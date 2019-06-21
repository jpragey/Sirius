package org.sirius.frontend.api;

import java.util.List;

public interface ClassOrInterfaceDeclaration {

	List<MemberValue> getValues();
	List<MemberFunction> getFunctions();

	
	default void visitContent(Visitor visitor) {
		
		getValues().stream().forEach(v -> v.visitMe(visitor));
		getFunctions().stream().forEach(v -> v.visitMe(visitor));
		
	}

	
}
