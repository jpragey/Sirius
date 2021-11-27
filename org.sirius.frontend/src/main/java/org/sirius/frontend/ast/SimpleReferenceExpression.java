package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableReference;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.FunctionActualArgumentImpl;
import org.sirius.frontend.symbols.SymbolTableImpl;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

/** 'single token' reference; meaning is highly context-dependant (local variable, function parameter, member value...)
 *
 * Concrete type determination is done by {@link #getExpression()} and can be:
 * -  
 * @author jpragey
 *
 */
public class SimpleReferenceExpression implements AstExpression, Scoped {
	private Reporter reporter;
	private AstToken referenceName;
	private SymbolTableImpl symbolTable = null;
	private Scope scope = null;
	
	private Optional<Expression> impl = null;
	
	private SimpleReferenceExpression(Reporter reporter, AstToken referenceName, SymbolTableImpl symbolTable) {
		super();
		this.reporter = reporter;
		this.referenceName = referenceName;
		this.symbolTable = symbolTable;
	}

	public SimpleReferenceExpression(Reporter reporter, AstToken referenceName) {
		this(reporter, referenceName, null);
	}

	public AstToken getReferenceName() {
		return referenceName;
	}
	public String getNameString() {
		return referenceName.getText();
	}

	public void setSymbolTable(SymbolTableImpl symbolTable) {
		this.symbolTable = symbolTable;
	}


	@Override
	public AstType getType() {
		Optional<Symbol> optSymbol = symbolTable.lookupBySimpleName(referenceName.getText());
		if(optSymbol.isPresent()) {
			Symbol symbol = optSymbol.get();
			
			Optional<AstMemberValueDeclaration> optVd = symbol.getValueDeclaration();
			if(optVd.isPresent()) {
				AstMemberValueDeclaration vd = optVd.get();
				AstType t = vd.getType();
				return t;
			} 
			Optional<AstLocalVariableStatement> optVs = symbol.getLocalVariableStatement();
			if(optVs.isPresent()) {
				AstLocalVariableStatement vd = optVs.get();
				AstType t = vd.getType();
				return t;
			} 
			reporter.error("Reference: " + referenceName.getText() + " is not a value declaration", referenceName);
			
					
		} else {
			reporter.error("Reference not found: " + referenceName.getText(), referenceName);
		}
		return AstType.noType;
	}

	@Override
	public String toString() {
		return "ref to " + referenceName.getText() + "->" ;
	}
	@Override
	public String asString() {
		return toString();
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startSimpleReferenceExpression(this);
		visitor.endSimpleReferenceExpression(this);
	}

	
	class LocalVariableReferenceImpl implements LocalVariableReference {
		private AstLocalVariableStatement astStmt;
		
		public LocalVariableReferenceImpl(AstLocalVariableStatement astStmt) {
			super();
			this.astStmt = astStmt;
		}

		@Override
		public Type type() {
			return astStmt.getType().getApiType();
		}

		@Override
		public Token getName() {
			return referenceName;
		}
		
		@Override
		public String toString() {
			return "loc.var " + getType() + " " + getName();
		}
		
	}
	
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	@Override
	public Optional<Expression> getExpression() {
		if(this.impl != null) {
			return this.impl;
		}

		String simpleName = referenceName.getText();

		Optional<AstLocalVariableStatement> localVarDecl = scope.getLocalVariable(simpleName);
		if(localVarDecl.isPresent()) {
			AstLocalVariableStatement st = localVarDecl.get();
			LocalVariableReferenceImpl lvr = new LocalVariableReferenceImpl(st);
			this.impl = Optional.of(lvr);
		}

		if(this.impl == null) {
			Optional<AstFunctionParameter> functionParamDecl = scope.getFunctionArgument(simpleName);
			if(functionParamDecl.isPresent()) {
				AstFunctionParameter st = functionParamDecl.get();
				FunctionActualArgumentImpl actualArg = new FunctionActualArgumentImpl(st);
				this.impl = Optional.of(actualArg);
			}
		}

		if(this.impl == null) {
			reporter.error("Reference not found: " + referenceName.getText(), this.referenceName);
			this.impl = Optional.empty();
		}
		
		return this.impl;
	}

	@Override
	public AstExpression linkToParentST(SymbolTable parentSymbolTable) {
		SimpleReferenceExpression expr = new SimpleReferenceExpression(reporter, referenceName, 
				new SymbolTableImpl(Optional.of(parentSymbolTable), this.getClass().getSimpleName()));
		return expr;
	}

	@Override
	public SymbolTableImpl getSymbolTable() {
		return symbolTable;
	}

	@Override
	public void verify(int featureFlags) {
		verifyNotNull(symbolTable, "SimpleReferenceExpression.symbolTable");
		verifyNotNull(scope, "SimpleReferenceExpression.scope");
		verifyNotNull(impl, "SimpleReferenceExpression.impl");
	}

	@Override
	public void setScope2(Scope scope) {
		assert(this.scope == null);
		assert(scope != null);
		this.scope = scope;
	}

}
