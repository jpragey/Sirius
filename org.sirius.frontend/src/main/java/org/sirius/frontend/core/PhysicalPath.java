package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PhysicalPath {

	private List<String> elements = new ArrayList<>();
	
	private Optional<PhysicalPath> parent;

	public PhysicalPath(List<String> elements) {
		super();
		this.elements = elements;
		this.parent = elements.isEmpty() ? 
				Optional.empty() : 
					Optional.of(new PhysicalPath(elements.subList(0, elements.size()-1))); 
	}
	
	public final static PhysicalPath empty = new PhysicalPath(Collections.emptyList());

	/** Parses an "/" separated path string */
	public static PhysicalPath parse(String str) {
		return new PhysicalPath(Arrays.asList(str.split("/")));
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
		PhysicalPath other = (PhysicalPath) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}
	
	

	public boolean startWith(PhysicalPath other) {
		if(other.elements.size() > elements.size())
			return false;
		
		Iterator<String> current = elements.iterator();
		for(String e: other.elements) {
			if(current.next() != e)
				return false;
		}
		return true;
	}

	public List<String> getElements() {
		return elements;
	}

	public Optional<PhysicalPath> getParent() {
		return parent;
	}
	
}
