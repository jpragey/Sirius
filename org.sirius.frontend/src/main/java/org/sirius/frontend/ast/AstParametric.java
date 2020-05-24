package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

public interface AstParametric<NextType> {
	
	List<TypeParameter> getTypeParameters();

	
	Optional<NextType> apply(AstType parameter);

}
