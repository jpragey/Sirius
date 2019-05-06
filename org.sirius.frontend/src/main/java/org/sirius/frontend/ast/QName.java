package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;

public class QName {

	private List<AstToken> elements = new ArrayList<>();

	public QName() {
		super();
	}

	@Override
	public int hashCode() {	// TODO: cache 
		final int prime = 31;
		int result = 1;
		for(AstToken e: elements) {
			result = result * prime + e.getText().hashCode();
		}
//		
//		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
		QName other = (QName) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}



	public List<AstToken> getElements() {
		return elements;
	}
	
	public List<String> getStringElements() {
		return elements.stream().map(e -> e.getText()).collect(Collectors.toList());	// TODO: cache
	}
	
	
	public void add(AstToken element) {
		this.elements.add(element);
	}
	
	public void add(Token element) {
		this.elements.add(new AstToken(element));
	}
	
	public String dotSeparated() {
		StringBuilder sb = new StringBuilder();
		
		
		for(AstToken e: elements) {
			if(sb.length() != 0) {
				sb.append('.');
			}
			sb.append(e.getText());
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return dotSeparated();
	}
}
