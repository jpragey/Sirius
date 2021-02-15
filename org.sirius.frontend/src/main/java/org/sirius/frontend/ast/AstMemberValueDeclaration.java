package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.MemberValueImpl;

public class AstMemberValueDeclaration implements /*Type, Scoped, */Visitable, Verifiable {

	private AstType type;
	private AstToken name;
	private List<Annotation> annotations;
	private Optional<AstExpression> initialValue;
	private QName qname = null;

	private MemberValueImpl memberValueImpl = null;

	public AstMemberValueDeclaration(AnnotationList annotations, AstType type, AstToken name, Optional<AstExpression> initialValue) {
		super();
		this.annotations = annotations.getAnnotations();
		this.type = type;
		this.name = name;
		this.initialValue = initialValue;
	}
	public AstMemberValueDeclaration(AnnotationList annotations, AstType type, AstToken name) {
		this(annotations, type, name, Optional.empty());
	}	
	public AstType getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}
	public String getNameString() {
		return name.getText();
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
		visitor.endValueDeclaration(this);
	}

	@Override
	public String toString() {
		return getType() + " " + getName().getText();
	}

	public MemberValue getMemberValue() {
		if(memberValueImpl == null) {
			Type apiType = type.getApiType();
			Optional<Expression> initialValue = AstMemberValueDeclaration.this.getApiInitialValue();
			memberValueImpl = new MemberValueImpl(apiType, name.asToken(), initialValue);
		}
		return memberValueImpl; 
	}
	
	public Optional<Expression> getApiInitialValue() {
		return initialValue.flatMap(AstExpression::getExpression);
	}
	public Optional<AstExpression> getInitialValue() {
		return initialValue;
	}
	
	
	public QName getQname() {
		return qname;
	}
	public void setContainerQName(QName containerQName) {
		this.qname = containerQName.child(this.name.getText());
	}
	@Override
	public void verify(int featureFlags) {
		type.verify(featureFlags);
		
		verifyList(annotations, featureFlags);
		verifyOptional(initialValue, "initialValue", featureFlags);
	}
	
}
