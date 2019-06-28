package org.sirius.common.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QName {

	private List<String> elements;
	
	public QName(List<String> elements) {
		super();
		this.elements = new ArrayList<String>(elements);
	}
	public QName(String... elements) {
		super();
		this.elements = Arrays.asList(elements);
	}
	private QName(List<String> elements0, List<String> elements1) {
		super();
		
		this.elements = new ArrayList<String>(elements0.size() + elements1.size());
		this.elements.addAll(elements0);
		this.elements.addAll(elements1);
	}
	private QName(List<String> elements0, String child) {
		super();
		
		this.elements = new ArrayList<String>(elements0.size() + 1);
		this.elements.addAll(elements0);
		this.elements.add(child);
	}
	
	public List<String> getStringElements() {
		return elements;
	}
	public String[] toArray() {
		return elements.toArray(new String[0]);
	}
	
	public QName child(String childName) {
		return new QName(elements, childName); 
	}
	public QName child(QName childPath) {
		return new QName(elements, childPath.elements); 
	}
	
	public Optional<QName> parent() {
		if(elements.isEmpty())
			return Optional.empty();
		
		return Optional.of(new QName(elements.subList(0, elements.size()-1)));
	}
	
	public boolean isEmpty() {
		return elements.isEmpty();
	}
	
	public String dotSeparated() {
		return getStringElements().stream()
				.collect(Collectors.joining("."));
	}
	@Override
	public String toString() {
		return dotSeparated();
	}
}
