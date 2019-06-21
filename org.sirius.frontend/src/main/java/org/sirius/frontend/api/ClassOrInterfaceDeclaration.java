package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

public interface ClassOrInterfaceDeclaration {

	List<MemberValue> getValues();
	List<MemberFunction> getFunctions();
	
	/** This class/interface qualified name */
	QName getQName();

	
	default void visitContent(Visitor visitor) {
		
		getValues().stream().forEach(v -> v.visitMe(visitor));
		getFunctions().stream().forEach(v -> v.visitMe(visitor));
		
	}

	
}
