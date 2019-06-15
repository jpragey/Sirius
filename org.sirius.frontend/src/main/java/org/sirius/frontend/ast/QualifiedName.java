package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;

public class QualifiedName {

	private List<AstToken> elements;
	private List<String> stringElements;

	public QualifiedName(List<AstToken> elements) {
		super();
		this.elements = new ArrayList<>();
		this.elements.addAll(elements);
		this.stringElements = elements.stream().map(tk -> tk.getText()).collect(Collectors.toUnmodifiableList());
	}
	public QualifiedName() {
		this(Collections.emptyList());
	}

	@Override
	public int hashCode() {	// TODO: cache 
		final int prime = 31;
		int result = 1;
		for(AstToken e: elements) {
			result = result * prime + e.getText().hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QualifiedName other = (QualifiedName) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}



	public List<AstToken> getTokenElements() {
		return elements;
	}
//	@Override
//	public List<? extends org.sirius.common.core.Token> getElements() {
//		return elements;
//	}
	
//	public List<String> getStringElements() {
//		return elements.stream().map(e -> e.getText()).collect(Collectors.toList());	// TODO: cache
//	}
	
//	public String dotSeparated() {
//		return QName.super.dotSeparated();
//	}

//	@Override
//	public String toString() {
//		return dotSeparated();
//	}
	
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
//	@Override
//	public List<String> getStringElements() {
//		return stringElements;
//	}
	public QName toQName() {
		return new QName(stringElements);
	}
}
