package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableReference;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.FunctionActualArgumentImpl;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.Symbol;

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
	private DefaultSymbolTable symbolTable = null;
	private Scope scope = null;
	
	private Optional<Expression> impl = null;
	
	private SimpleReferenceExpression(Reporter reporter, AstToken referenceName, DefaultSymbolTable symbolTable) {
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

	public void setSymbolTable(DefaultSymbolTable symbolTable) {
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
//		return getType().toString() + " " + referenceName.getText() + "->" ;
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
		public Type getType() {
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
		if(impl == null) {
			String simpleName = referenceName.getText();

			Optional<AstLocalVariableStatement> localVarDecl = scope.getLocalVariable(simpleName);
			if(localVarDecl.isPresent()) {
				AstLocalVariableStatement st = localVarDecl.get();
				LocalVariableReferenceImpl lvr = new LocalVariableReferenceImpl(st);
				impl = Optional.of(lvr);
				return impl;
			}
			
			Optional<AstFunctionParameter> functionParamDecl = scope.getFunctionParameter(simpleName);
			if(functionParamDecl.isPresent()) {
				AstFunctionParameter st = functionParamDecl.get();
				FunctionActualArgumentImpl actualArg = new FunctionActualArgumentImpl(st);
				impl = Optional.of(actualArg);
				return impl;
			}
		}
		return impl;
		
	}

	@Override
	public AstExpression linkToParentST(DefaultSymbolTable parentSymbolTable) {
		SimpleReferenceExpression expr = new SimpleReferenceExpression(reporter, referenceName, 
				new DefaultSymbolTable(parentSymbolTable, this.getClass().getSimpleName()));
		return expr;
	}

	@Override
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}

	@Override
	public void verify(int featureFlags) {
		verifyNotNull(symbolTable, "SimpleReferenceExpression.symbolTable");
		verifyNotNull(scope, "SimpleReferenceExpression.scope");
		verifyNotNull(impl, "SimpleReferenceExpression.impl");
	}


}
