package org.sirius.common.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sirius.common.error.Reporter;

public class QName {

	private List<String> elements;

	private String cachedDotSeparatedString = null;
	
	public static final QName empty = new QName();
	
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
	
	
//	public static QName parseDotSeparated(String dotSeparated) {
//		return new QName(dotSeparated.split("\\."));
//	}

	/** Parse + validate (not empty, no empty element, )
	 * 
	 * @param dotSeparated
	 * @param reporter print an error if empty, if an element is empty, or if an element contains a non-id char
	 * @return
	 */
	public static Optional<QName> parseAndValidate(String dotSeparated, Reporter reporter) {
		
		if(dotSeparated.isEmpty()) {
			reporter.error("Empty qualified name: [" + dotSeparated + "]");
			return Optional.empty();
		}
		
		Pattern qnamePattern = Pattern.compile("(\\s)*([a-zA-Z_$][a-zA-Z0-9_$]*)(\\s)*(\\.)?");
		Matcher m = qnamePattern.matcher(dotSeparated);
		List<String> elements = new ArrayList<String>();
		while(m.find()) {
			String elementStr = m.group(2);
			String lastDot = m.group(4);	// null at end of qname
			
			elements.add(elementStr);
			
			if(lastDot == null) { // '.' not found, end of parsing
				int matchEnd = m.end();	// offset after last char matched
				if(dotSeparated.length() > matchEnd) {
					reporter.error("Extra chars in qualified name : [" + dotSeparated + "]");
					return Optional.empty();
				}
				QName qName = new QName(elements);
				return Optional.of(qName);
			}
		}
		
		reporter.error("Could not find end while parsing qualified name : [" + dotSeparated + "]");
		return Optional.empty();
		
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
		QName other = (QName) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}
	
	public List<String> getStringElements() {
		return elements;
	}
	
	public String getLast() {
		return elements.get(elements.size()-1);
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
		if(cachedDotSeparatedString == null)
			cachedDotSeparatedString = getStringElements().stream()
				.collect(Collectors.joining("."));
		
		return cachedDotSeparatedString;
	}
	public String slashSeparated() {
		return getStringElements().stream()
				.collect(Collectors.joining("/"));
	}
	@Override
	public String toString() {
		return dotSeparated();
	}
	
	public Stream<String> stream() {
		return elements.stream();
	}
}
