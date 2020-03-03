package org.sirius.frontend.api;

import org.sirius.common.core.Token;

public interface AbstractValue {
	Type getType();
	Token getName();
}
