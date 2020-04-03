package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.ValueAccessExpression;
import org.sirius.frontend.symbols.SymbolTable;

public class AstFieldAccessExpression implements AstExpression {

	private Reporter reporter; 
	private AstExpression containerExpression;
	private AstToken valueName;
	private SymbolTable symbolTable = null;

	public AstFieldAccessExpression(Reporter reporter, AstExpression containerExpression, AstToken valueName) {
		super();
		this.reporter = reporter;
		this.containerExpression = containerExpression;
		this.valueName = valueName;
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	@Override
	public AstType getType() {
		// Check container type is Class or Inteface
		AstType containerType = containerExpression.getType();
		if(containerType instanceof AstNoType) {
			return AstType.noType; // Error message has soon been reported
		}
//		AstType containerType = optType.get();
		
		if(containerType instanceof AstClassDeclaration) {
			AstClassDeclaration cd = (AstClassDeclaration)containerType;
			Optional<AstValueDeclaration> astVd = cd.getValueDeclarations().stream()	// TODO: cd.getValueDeclarations should be a map
					.filter(v -> v.getName().getText().equals(valueName.getText()))
					.findFirst();
			if(astVd.isPresent()) {
				AstType valueType = astVd.get().getType();
				return valueType;
			} else {
				reporter.error("Type " + containerType + " has no value named " + valueName.getText(), valueName); // TODO: is containerType valid ?
			}
		} else {
			reporter.error("Type " + containerType + " is not a container, can't get field '" + valueName.getText(), valueName); // TODO: is containerType valid ?
		}
		return AstType.noType;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startFieldAccess(this);
		containerExpression.visit(visitor);
		visitor.endFieldAccess(this);
	}

	private class ValueAccessExpressionImpl implements ValueAccessExpression {
//		Optional<AstType> optType = containerExpression.getType();

		@Override
		public Type getType() {
			AstType astType = AstFieldAccessExpression.this.getType();
			Type type = astType.getApiType();
			return type;
		}

		@Override
		public Expression getContainerExpression() {
			Expression ex = containerExpression.getExpression();
			return ex;
		}

		@Override
		public MemberValue getMemberValue() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private ValueAccessExpressionImpl impl = null;
	
	@Override
	public Expression getExpression() {
		if(impl == null)
			impl = new ValueAccessExpressionImpl();
		return impl;
	}
	
}
