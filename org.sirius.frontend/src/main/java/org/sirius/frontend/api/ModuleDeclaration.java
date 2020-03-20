package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.core.PhysicalPath;

public interface ModuleDeclaration {

	List<PackageDeclaration> getPackages();
	
	QName getQName(); 
	
	PhysicalPath getPhysicalPath();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getPackages().stream().forEach(v -> v.visitMe(visitor));
		visitor.end(this);
	}

}
