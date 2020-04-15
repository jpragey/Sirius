package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.TopLevelValue;
import org.sirius.frontend.api.Type;

public class AstMemberValueDeclaration implements /*Type, Scoped, */Visitable  {

	private AstType type;
	private AstToken name;
	private List<Annotation> annotations;
	private Optional<AstExpression> initialValue;
	
	public AstMemberValueDeclaration(AnnotationList annotations, AstType type, AstToken name) {
		super();
		this.annotations = annotations.getAnnotations();
		this.type = type;
		this.name = name;
		this.initialValue = Optional.empty();
	}
	
	public AstType getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}
	public void setInitialValue(AstExpression expression) {
		this.initialValue = Optional.of(expression);
	}
	
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startValueDeclaration(this);
		type.visit(visitor);	// TODO: ok for SimpleType, but ClassDeclaration soon done
//		initialValue.ifPresent(expr -> expr.visit(visitor));
		visitor.endValueDeclaration(this);
	}

	@Override
	public String toString() {
		return getType() + " " + getName().getText();
	}

	public Optional<TopLevelValue> getTopLevelValue() {
		return Optional.of(new TopLevelValue() {

			@Override
			public Type getType() {
				return type.getApiType();
			}

			@Override
			public Token getName() {
				return name.asToken();
			}

			@Override
			public Optional<Expression> getInitialValue() {
				return AstMemberValueDeclaration.this.getApiInitialValue();
			}
			@Override
			public String toString() {
				return "TopLevelValue: " + getType() + " " + getName().getText();
			}
			
		});
	}
	
	public MemberValue getMemberValue() {
		return new MemberValue() {

			@Override
			public Type getType() {
				return type.getApiType();
			}

			@Override
			public Token getName() {
				return name.asToken();
			}

			@Override
			public Optional<Expression> getInitialValue() {
				return AstMemberValueDeclaration.this.getApiInitialValue();
			}
			@Override
			public String toString() {
				return "MemberValue: " + getType() + " " + getName().getText();
			}
		};
	}
	
	public Optional<Expression> getApiInitialValue() {
		return initialValue.map(AstExpression::getExpression);
	}
	
}
