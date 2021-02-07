package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

/** Argument for function / method or class constructor declaration
 * 
 * @author jpragey
 *
 */
public class AstLambdaParameter {
	
	private AstType type;
	private Optional<AstToken> name;
	private DefaultSymbolTable symbolTable;
	/** index in arg list (set *after* construction */
	private int index;

	public AstLambdaParameter(AstType type, AstToken name) {
		super();
		this.type = type;
		this.name = Optional.of(name);
		this.index = -1;
	}
	public AstLambdaParameter(AstType type, Optional<AstToken> name) {
		super();
		this.type = type;
		this.name = name;
		this.index = -1;
	}
	
	
	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public void visit(AstVisitor visitor) {
		visitor.startLambdaFormalArgument(this);
		type.visit(visitor);
		visitor.endLambdaFormalArgument(this);
	}
	
//	/** index in argument list */
//	public int getIndex() {
//		return index;
//	}
	
	public AstType getType() {
		return type;
	}
	public Optional<AstToken> getName() {
		return name;
	}
	public String getNameString() {
		return name.map(AstToken::getText).orElse("");
	}
	
	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public void resolve() {
	}
	
	private class FunctionFormalArgumentImpl implements FunctionFormalArgument {
		private Optional<QName> argQName;
		
		public FunctionFormalArgumentImpl(Optional<QName> argQName) {
			super();
			this.argQName = argQName;
		}

		@Override
		public QName getQName() {
			return argQName.orElse(QName.empty);	// TODO: better
		}

		@Override
		public Type getType() {
			return type.getApiType();
		}
		@Override
		public String toString() {
			return "param. " + argQName.map(qn-> qn.dotSeparated()).orElse("<none>");
		}
	}
	
//	private FunctionFormalArgumentImpl impl = null;
//	
//	public FunctionFormalArgument toAPI(QName functionQName) {
//		if(impl == null)
//			impl =new FunctionFormalArgumentImpl(functionQName.child(name.getText()));
//		return impl;
//	}
	
	 @Override
	public String toString() {
	
		return "(" + type.toString() + " " + name + ")";
	}
}
