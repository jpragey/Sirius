package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.MemberValueAccessExpression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class AstMemberAccessExpression implements AstExpression, Scoped {

	private Reporter reporter; 
	private AstExpression containerExpression;
	private AstToken valueName;
	private DefaultSymbolTable symbolTable = null;

	
	private AstMemberAccessExpression(Reporter reporter, AstExpression containerExpression, AstToken valueName,
			DefaultSymbolTable symbolTable) {
		super();
		this.reporter = reporter;
		this.containerExpression = containerExpression;
		this.valueName = valueName;
		this.symbolTable = symbolTable;
	}

	public AstMemberAccessExpression(Reporter reporter, AstExpression containerExpression, AstToken valueName) {
		super();
		this.reporter = reporter;
		this.containerExpression = containerExpression;
		this.valueName = valueName;
	}

	
	public AstToken getValueName() {
		return valueName;
	}

	@Override
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	@Override
	public String toString() {
		String cs = containerExpression.toString();
		return "AstMemberAccessExpression: <container>." + valueName.getText();
//		return "AstMemberAccessExpression: " + containerExpression.toString() + "." + valueName.getText();
	}
	@Override
	public String asString() {
		return toString();
	}
	
	@Override
	public AstType getType() {
		// Check container type is Class or Inteface
		AstType containerType = containerExpression.getType().resolve();
		if(containerType instanceof AstNoType) {
			return AstType.noType; // Error message has soon been reported
		}
//		AstType containerType = optType.get();
		
		if(containerType instanceof AstClassDeclaration) {
			AstClassDeclaration cd = (AstClassDeclaration)containerType;
			Optional<AstMemberValueDeclaration> astVd = cd.getValueDeclarations().stream()	// TODO: cd.getValueDeclarations should be a map
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

	private static class MemberValueAccessExpressionImpl implements MemberValueAccessExpression {
//		Optional<AstType> optType = containerExpression.getType();

		private MemberValue memberValue;
		private Expression containerExpression;
		private Type type;
		
		public MemberValueAccessExpressionImpl(MemberValue memberValue, Expression containerExpression, Type type) {
			super();
			this.memberValue = memberValue;
			this.containerExpression = containerExpression;
			this.type = type;
		}


		@Override
		public Type getType() {
//			AstType astType = AstMemberAccessExpression.getType();
//			Type type = astType.getApiType();
			return type;
		}

		@Override
		public Expression getContainerExpression() {
			return containerExpression;
//			Expression ex = containerExpression.getExpression();
//			return ex;
		}

		@Override
		public MemberValue getMemberValue() {
			return memberValue;
		}
		
	}
	
	private Optional<Expression> impl = null;
	
	@Override
	public Optional<Expression> getExpression() {
		if(impl == null) {
			AstType contType = containerExpression.getType().resolve();
			assert(contType instanceof AstClassDeclaration);	// TODO
			AstClassDeclaration contClassDef = (AstClassDeclaration)contType;

			Optional<Expression> optContainerExpr = containerExpression.getExpression();
			assert(optContainerExpr.isPresent());	// TODO
			
			AstType astMemberType = getType();
			Type memberType = astMemberType.getApiType();

			for(AstMemberValueDeclaration vd: contClassDef.getValueDeclarations()) { // TODO should be a map vdName -> VD
				String vdName = vd.getName().getText();
				if(vdName.equals(valueName.getText())) {
//					Optional<MemberValue> optVd = vd.getMemberValue();
//					assert(optVd.isPresent());	// TODO : ok for now, we have only member values (no top level)
					MemberValue mv = vd.getMemberValue();
			
					MemberValueAccessExpressionImpl mvaExpr = new MemberValueAccessExpressionImpl(mv, optContainerExpr.get(), memberType);
					impl = Optional.of(mvaExpr);
//					impl = Optional.of(new MemberValueAccessExpressionImpl(mv));
					return impl;
				}
			}
		}
		
		if(impl == null)
			impl = containerExpression.getExpression(); // default if member value can't be evaluated
		
		return impl;
	}

	@Override
	public AstExpression linkToParentST(DefaultSymbolTable parentSymbolTable) {
		
		AstMemberAccessExpression expr = new AstMemberAccessExpression(
				reporter, 
				containerExpression.linkToParentST(parentSymbolTable),
				valueName,
				new DefaultSymbolTable(parentSymbolTable, this.getClass().getSimpleName()));
		return expr;
	}

	@Override
	public void verify(int featureFlags) {
		containerExpression.verify(featureFlags);
		verifyNotNull(symbolTable, "AstMemberAccessExpression.symbolTable");
		verifyNotNull(impl, "AstMemberAccessExpression.impl");
	}
	
}
