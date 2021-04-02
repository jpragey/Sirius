package org.sirius.frontend.ast;

import java.util.Optional;

public class ClosureElement implements Verifiable {
	private AstType type;
	private AstToken name;
	/** exists if this closure element maps to a function argument */
//	private Optional<AstFunctionParameter> functionArg;
	
	public ClosureElement(AstType type, AstToken name) {
		super();
		this.type = type;
		this.name = name;
	}
	public ClosureElement(AstFunctionArgument param) {
		this(param.getType(), param.getName());
	}
	public AstType getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}
//	public Optional<AstFunctionParameter> getFunctionArg() {
//		return functionArg;
//	}
	@Override
	public void verify(int featureFlags) {
		type.verify(featureFlags);
//		verifyOptional(functionArg, "functionArg", featureFlags);
	}
	
}