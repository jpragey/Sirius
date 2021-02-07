package org.sirius.frontend.ast;

import java.util.Optional;

public class ClosureElement {
	private AstType type;
	private AstToken name;
	/** exists if this closure element maps to a function argument */
	private Optional<AstFunctionParameter> functionArg;
	
	public ClosureElement(AstType type, AstToken name, AstFunctionParameter functionArg) {
		super();
		this.type = type;
		this.name = name;
		this.functionArg = Optional.of(functionArg);
	}
	public ClosureElement(AstFunctionParameter param) {
		this(param.getType(), param.getName(), param);
	}
	public AstType getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}
	public Optional<AstFunctionParameter> getFunctionArg() {
		return functionArg;
	}
	
}