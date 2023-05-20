package org.sirius.frontend.apiimpl;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.MemberValue;

public record ClassDeclarationImpl(QName qName, 
		List<AbstractFunction> memberFunctions,
		List<MemberValue> memberValues,
		Optional<ExecutionEnvironment> executionEnvironment
		) implements ClassType 
{
	@Override
	public String toString() {
		assert(qName != null);
		return "API class " + qName;
	}

}
