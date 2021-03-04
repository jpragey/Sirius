package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.core.PhysicalPath;

/** Module declaration.
 * A ModuleDeclaration is basically a collection of {@link PackageDeclaration}.
 * It corresponds to a module declarator (module.sirius).
 * @author jpragey
 *
 */
public interface ModuleDeclaration {

	List<PackageDeclaration> getPackages();
	
	QName getQName();
	
	String getVersion();
	
	default String getQNameString() {
		return getQName().dotSeparated();
	}
	
	PhysicalPath getPhysicalPath();
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		getPackages().stream().forEach(v -> v.visitMe(visitor));
		visitor.end(this);
	}

}
