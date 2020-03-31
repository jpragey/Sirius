package org.sirius.frontend.api.testimpl;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.core.Token;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ConstructorCall;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.TopLevelFunction;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.Visitor;

public class ConstructorCallImpl implements ConstructorCall {

	private ClassType classType; 
	private List<Expression> arguments;

	public ConstructorCallImpl(ClassType classType, List<Expression> arguments) {
		super();
		this.classType = classType;
		this.arguments = arguments;
	}

	@Override
	public void visitMe(Visitor visitor) {
		visitor.start(this);
		visitor.end(this);
	}

	/** Return containing class type
	 * 
	 */
	@Override
	public Type getType() {
		return classType;
	}

	@Override
	public List<Expression> getArguments() {
		return arguments;
	}

}
