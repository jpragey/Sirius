package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;

public interface ModuleDeclaration {

	List<PackageDeclaration> getPackages();
	
	QName getQName(); 
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getPackages().stream().forEach(v -> v.visitMe(visitor));
		visitor.end(this);
	}

}
