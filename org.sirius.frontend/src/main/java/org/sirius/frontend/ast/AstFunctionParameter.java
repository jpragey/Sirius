package org.sirius.frontend.ast;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

/** Argument for function / method or class constructor declaration
 * 
 * @author jpragey
 *
 */
public class AstFunctionParameter {
	
	private AstType type;
	private AstToken name;
	private DefaultSymbolTable symbolTable;
	
	public AstFunctionParameter(AstType type, AstToken name) {
		super();
		this.type = type;
		this.name = name;
	}
	
	public void visit(AstVisitor visitor) {
		visitor.startFunctionFormalArgument(this);
		type.visit(visitor);
		visitor.endFunctionFormalArgument(this);
	}
	
	public AstType getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}
	
	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public void resolve() {
	}
	
	private class FunctionFormalArgumentImpl implements FunctionFormalArgument {
		private QName argQName;
		
		public FunctionFormalArgumentImpl(QName argQName) {
			super();
			this.argQName = argQName;
		}

		@Override
		public QName getQName() {
			return argQName;
		}

		@Override
		public Type getType() {
			return type.getApiType();
		}
		@Override
		public String toString() {
			return "param. " + argQName.dotSeparated();
		}
	}
	
	private FunctionFormalArgumentImpl impl = null;
	
	public FunctionFormalArgument toAPI(QName functionQName) {
		if(impl == null)
			impl =new FunctionFormalArgumentImpl(functionQName.child(name.getText()));
		return impl;
	}
	
	 @Override
	public String toString() {
	
		return "(" + type.toString() + " " + name + ")";
	}
}
