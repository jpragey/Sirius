package org.sirius.frontend.ast;

import org.antlr.v4.runtime.Token;

/** Argument for function / method or class constructor declaration
 * 
 * @author jpragey
 *
 */
public class FunctionFormalArgument {
	
	private Type type;
	private AstToken name;
	
	public FunctionFormalArgument(Type type, AstToken name) {
		super();
		this.type = type;
		this.name = name;
	}
	
//	public FunctionFormalArgument(Type type, Token name) {
//		this(type, new AstToken(name));
//	}
	
	public Type getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}
	
	
}
