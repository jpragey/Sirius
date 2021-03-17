package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.LocalVariableStatementImpl;
import org.sirius.frontend.symbols.SymbolTableImpl;
import org.sirius.frontend.symbols.Symbol;

public class AstLocalVariableStatement implements AstStatement {

	private AstToken varName;
	private Optional<AstExpression> initialValue = Optional.empty();
	private SymbolTableImpl symbolTable = null;
	private AstType type;

//	private LocalVariableStatementImpl impl = null;
	private Optional<Statement> impl = null;

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
	
	public void setSymbolTable(SymbolTableImpl symbolTable) {
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

	@Override
	public Optional<Statement> toAPI() {
		if(impl == null) {
//			impl = new LocalVariableStatementImpl(this);	// TODO: restore
			Optional<Symbol> optSymbol = symbolTable.lookupBySimpleName(varName.getText());
			if(optSymbol.isPresent()) {
				Symbol symbol = optSymbol.get();
				Optional<AstLocalVariableStatement> lvs = symbol.getLocalVariableStatement();
				if(lvs.isPresent()) {
					AstLocalVariableStatement stmt = lvs.get();
					Type type = AstLocalVariableStatement.this.type.getApiType();
					LocalVariableStatementImpl lvStmt =new LocalVariableStatementImpl(stmt, varName, initialValue, type);
					this.impl = Optional.of(lvStmt);
				}
			}
		}
		if(impl == null)
			throw new UnsupportedOperationException("Local variable " + varName + " not found or processed, TODO");
		
		return impl;
	}
	
	@Override
	public String toString() {
		return type + "" + varName;
	}

	@Override
	public void verify(int featureFlags) {
		verifyOptional(initialValue, "AstLocalVariableStatement.initialValue", featureFlags);
		verifyNotNull(symbolTable, "AstLocalVariableStatement.symbolTable");
		verifyCachedObjectNotNull(impl, "AstLocalVariableStatement.LocalVariableStatementImpl impl",featureFlags);
		type.verify(featureFlags);
	}
}


