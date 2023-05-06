package org.sirius.frontend.api;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.core.PhysicalPath;

/** Module declaration.
 * A ModuleDeclaration is basically a collection of {@link PackageDeclaration}.
 * It corresponds to a module declarator (module.sirius).
 * @author jpragey
 *
 */
public interface ModuleDeclaration {

	List<PackageDeclaration> packageDeclarations();
	
	Optional<QName> qName();
	
	String version();

	PhysicalPath physicalPath();

	default String getQNameString() {
//		return qName().dotSeparated();
		return qName().map(qn -> qn.dotSeparated()).orElse("");
	}
	
	
	default void visitMe(Visitor visitor) {
		visitor.start(this);
		packageDeclarations().stream().forEach(v -> v.visitMe(visitor));
		visitor.end(this);
	}

}
