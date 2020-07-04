package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Symbol;

public class AstLocalVariableStatement implements AstStatement {

	private AstToken varName;
	private Optional<AstExpression> initialValue = Optional.empty();
	private DefaultSymbolTable symbolTable = null;
	private AstType type;
	
	public AstLocalVariableStatement(AnnotationList annotationList, AstType type, AstToken varName, Optional<AstExpression> initialValue) {
		super();
		this.type = type;
		this.varName = varName;
		this.initialValue = initialValue;
	}

	public AstLocalVariableStatement(AnnotationList annotationList, AstType type, AstToken varName) {
		this(annotationList, type, varName, Optional.empty());
	}

	public void setInitialValue(AstExpression initialValue) {
		this.initialValue = Optional.of(initialValue);
	}
	
	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public AstToken getVarName() {
		return varName;
	}

	public AstType getType() {
		return type;
	}

	public Optional<AstExpression> getInitialValue() {
		return initialValue;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		type.visit(visitor);
		if(initialValue.isPresent())
			initialValue.get().visit(visitor);
		visitor.end(this);
	}

	private class LocalVariableStatementImpl implements LocalVariableStatement {
		AstLocalVariableStatement stmt;
		Type type;
		public LocalVariableStatementImpl(AstLocalVariableStatement stmt) {
			super();
			this.stmt = stmt;
//			AstLocalVariableStatement.this.type.resolve();
			this.type = AstLocalVariableStatement.this.type.getApiType();
		}

		@Override
		public Type getType() {
			return type;
		}

		@Override
		public Token getName() {
			return varName;
		}

		@Override
		public Optional<Expression> getInitialValue() {
			if(initialValue.isPresent()) {
				Expression exp = initialValue.get().getExpression();
				return Optional.of(exp);
			}
			return Optional.empty();
		}
		@Override
		public String toString() {
			return stmt.toString();
		}

	}
	LocalVariableStatementImpl impl = null;
	
	@Override
	public LocalVariableStatement toAPI() {
		if(impl == null) {
//			impl = new LocalVariableStatementImpl(this);	// TODO: restore
			Optional<Symbol> optSymbol = symbolTable.lookup(varName.getText());
			if(optSymbol.isPresent()) {
				Symbol symbol = optSymbol.get();
				Optional<AstLocalVariableStatement> lvs = symbol.getLocalVariableStatement();
				if(lvs.isPresent()) {
					AstLocalVariableStatement stmt = lvs.get();
					impl = new LocalVariableStatementImpl(stmt);
				}
			}
		}
		if(impl == null)
			throw new UnsupportedOperationException("Local variable " + varName + " not found or processed, TODO");
		
		return impl;
		
//		return new LocalVariableStatement() {
//
//			@Override
//			public Type getType() {
//				return declaration.getType().getApiType();
//			}
//
//			@Override
//			public Token getName() {
//				return declaration.getName();
//			}
//
//			@Override
//			public Optional<Expression> getInitialValue() {
//				return declaration.getApiInitialValue();
//			}
//		};
	}
	@Override
	public String toString() {
		return type + "" + varName;
	}
}


