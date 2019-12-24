package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

public interface ClassOrInterfaceDeclaration extends ClassType {

	List<MemberValue> getValues();
	List<MemberFunction> getFunctions();
	
	/** This class/interface qualified name */
	QName getQName();

	
	default void visitContent(Visitor visitor) {
		
		getValues().stream().forEach(
				val -> val.visitMe(visitor)
		);
//		getFunctions().stream().forEach(
//				fct -> fct.visitMe(visitor)
//		);
		for(MemberFunction fct: getFunctions()) {
			fct.visitMe(visitor);
		}
	}

	
}
