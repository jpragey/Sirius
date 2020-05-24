package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

public interface PackageDeclaration {

	QName getQName(); 

	List<ClassDeclaration> getClasses();
	List<InterfaceDeclaration> getInterfaces();
	List<TopLevelValue> getValues();
//	List<TopLevelFunction> getFunctions();
	List<AbstractFunction> getFunctions();

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		
		getInterfaces().stream().forEach(v -> v.visitMe(visitor));
		getClasses().stream().forEach(v -> v.visitMe(visitor));
		getValues().stream().forEach(v -> v.visitMe(visitor));
		getFunctions().stream().forEach(v -> v.visitMe(visitor));

		visitor.end(this);
	}

}
