package org.sirius.frontend.ast;

import java.util.Optional;

public interface AstParametric<NextType> {
	Optional<NextType> apply(AstType parameter);

}
