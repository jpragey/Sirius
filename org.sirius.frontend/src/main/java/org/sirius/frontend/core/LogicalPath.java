package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.sirius.frontend.ast.QName;

public class LogicalPath {

	private List<String> elements = new ArrayList<>();
	
	private Optional<LogicalPath> parent;

	public LogicalPath(List<String> elements) {
		super();
		this.elements = elements;
		this.parent = elements.isEmpty() ? 
				Optional.empty() : 
					Optional.of(new LogicalPath(elements.subList(0, elements.size()-1))); 
	}
	public LogicalPath(String... elements) {
		this(Arrays.asList(elements));
	}
	
	public final static LogicalPath empty = new LogicalPath(Collections.emptyList());

	/** Parses an "." separated path string */
	public static LogicalPath parse(String str) {
		return new LogicalPath(Arrays.asList(str.split(".")));
	}

	public static LogicalPath fromPhysical(PhysicalPath physicalPath) {
		return new LogicalPath(physicalPath.getElements());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
		LogicalPath other = (LogicalPath) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}
	
	

	public boolean startWith(LogicalPath other) {
		if(other.elements.size() > elements.size())
			return false;
		
		Iterator<String> current = elements.iterator();
		for(String e: other.elements) {
			String n = current.next(); 
			if(!n.equals(e))
				return false;
		}
		return true;
	}

	public List<String> getElements() {
		return elements;
	}

	public Optional<LogicalPath> getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return String.join(".", this.elements);
	}
	
	public boolean matchQName(QName qName) {
		return elements == qName.getStringElements();
	}
	
}
