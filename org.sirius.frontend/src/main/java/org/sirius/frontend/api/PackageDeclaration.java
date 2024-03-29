package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;

public interface PackageDeclaration {

	Optional<QName> qName(); 

	List<ClassType> getClasses();
	List<AbstractFunction> getFunctions();

	default void visitMe(Visitor visitor) {
		visitor.start(this);
		
		getClasses().stream().forEach(v -> v.visitMe(visitor));
		getFunctions().stream().forEach(v -> v.visitMe(visitor));

		visitor.end(this);
	}

}
