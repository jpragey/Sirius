package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotationList {

	private ArrayList<Annotation> annotations  = new ArrayList<>();
	
	public AnnotationList() {
		super();
	}
	public AnnotationList(List<Annotation> annotations) {
		super();
		this.annotations.addAll(annotations);
	}
	
	public AnnotationList(Annotation... annotations) {
		this(Arrays.asList(annotations));
	}

	public void addAnnotation(Annotation annotation) {
		this.annotations.add(annotation);
	}

	public ArrayList<Annotation> getAnnotations() {
		return annotations;
	}
	
	
}
