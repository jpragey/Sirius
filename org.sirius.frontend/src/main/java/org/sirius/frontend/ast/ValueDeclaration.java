package org.sirius.frontend.ast;

import java.util.List;

public class ValueDeclaration {

	private Type type;
	private AstToken name;
	private List<Annotation> annotations;
	
	public ValueDeclaration(AnnotationList annotations, Type type, AstToken name) {
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
	
	
}
