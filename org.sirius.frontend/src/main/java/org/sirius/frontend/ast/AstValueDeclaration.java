package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;

import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.TopLevelValue;

public class AstValueDeclaration implements /*Type, Scoped, */Visitable  {

	private Type type;
	private AstToken name;
	private List<Annotation> annotations;
	
	public AstValueDeclaration(AnnotationList annotations, Type type, AstToken name) {
		super();
		this.annotations = annotations.getAnnotations();
		this.type = type;
		this.name = name;
	}
//	public ValueDeclaration(Type type, Token name) {
//		this(type, new AstToken(name));
//	}
	
	public Type getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startValueDeclaration(this);
		visitor.endValueDeclaration(this);
	}
	
	public Optional<TopLevelValue> getTopLevelValue() {	// TODO
		return Optional.of(new TopLevelValue() {
			
		});
	}
	
	public Optional<MemberValue> getMemberValue() {	// TODO
		return Optional.of(new MemberValue() {
			
		});
	}
}
