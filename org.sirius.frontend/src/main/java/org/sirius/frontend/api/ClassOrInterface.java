package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

public interface ClassOrInterface extends ClassType {

	List<MemberValue> getMemberValues();
	List<AbstractFunction> getFunctions();
	
	// get implemented interfaces of first level  (no grandparent)
	List<InterfaceDeclaration> getDirectInterfaces();
	
	/** This class/interface qualified name */
	QName getQName();

	
	default void visitContent(Visitor visitor) {
		
		for(MemberValue mv: getMemberValues()) {
			mv.visitMe(visitor);
		}
		
		for(AbstractFunction fct: getFunctions()) {
			fct.visitMe(visitor);
		}
	}

	
}
