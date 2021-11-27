package org.sirius.frontend.apiimpl;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;

public record MemberValueImpl(Type type, Token nameToken, Optional<Expression> initialValue) implements MemberValue {

	@Override
	public String toString() {
		return "MemberValue: " + type() + " " + nameToken().getText();
	}
}