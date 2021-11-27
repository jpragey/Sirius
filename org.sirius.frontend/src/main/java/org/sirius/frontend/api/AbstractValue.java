package org.sirius.frontend.api;

import org.sirius.common.core.Token;

public interface AbstractValue {
	Type type();
	Token nameToken();
}
