package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableReference;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.MemberValueAccessExpression;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

/** 'single token' reference; meaning is highly context-dependant (local variable, function parameter, member value...)
 * 
 * @author jpragey
 *
 */
public class SimpleReferenceExpression implements AstExpression {
	private Reporter reporter;
	private AstToken referenceName;
	private SymbolTable symbolTable = null;
	
	public SimpleReferenceExpression(Reporter reporter, AstToken referenceName) {
		super();
		this.reporter = reporter;
		this.referenceName = referenceName;
	}

	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}


	@Override
	public AstType getType() {
		Optional<Symbol> optSymbol = symbolTable.lookup(referenceName.getText());
		if(optSymbol.isPresent()) {
			Symbol symbol = optSymbol.get();
			
			Optional<AstMemberValueDeclaration> optVd = symbol.getValueDeclaration();
			if(optVd.isPresent()) {
				AstMemberValueDeclaration vd = optVd.get();
				AstType t = vd.getType();
				return t;
//				vd.ge
			} 
			Optional<AstLocalVariableStatement> optVs = symbol.getLocalVariableStatement();
			if(optVs.isPresent()) {
				AstLocalVariableStatement vd = optVs.get();
				AstType t = vd.getType();
				return t;
//				vd.ge
			} 
			reporter.error("Reference: " + referenceName.getText() + " is not a value declaration", referenceName);
			
					
		} else {
			reporter.error("Reference not found: " + referenceName.getText(), referenceName);
		}
		return AstType.noType;
	}

	@Override
	public String toString() {
		return getType().toString() + " " + referenceName.getText() + "->" ;
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

	private class ValueAccessExpressionImpl implements MemberValueAccessExpression {
		private AstMemberValueDeclaration valueDecl;
		
		public ValueAccessExpressionImpl(AstMemberValueDeclaration valueDecl) {
			super();
			this.valueDecl = valueDecl;
		}

		@Override
		public Type getType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expression getContainerExpression() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MemberValue getMemberValue() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	private Expression impl = null;
	
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
	
	
	
	@Override
	public Expression getExpression() {
		if(impl == null) {
			Optional<Symbol> optSymbol = symbolTable.lookup(referenceName.getText());
			if(optSymbol.isPresent()) {
				Symbol symbol = optSymbol.get();
				
				Optional<AstLocalVariableStatement> localVarDecl = symbol.getLocalVariableStatement();
				if(localVarDecl.isPresent()) {
					AstLocalVariableStatement st = localVarDecl.get();
					impl = new LocalVariableReferenceImpl(st);
					return impl;
				}
				
				Optional<AstMemberValueDeclaration> valueDecl = symbol.getValueDeclaration();
				if(valueDecl.isPresent()) {
					MemberValueAccessExpression expr = new ValueAccessExpressionImpl(valueDecl.get());
					return expr;
				} else {
					reporter.error("Reference: " + referenceName.getText() + " is not a container (class/interface)", referenceName);
				}

			} else {
				reporter.error("Reference not found: " + referenceName.getText(), referenceName);
			}
		}
		return impl;
		
	}


}
