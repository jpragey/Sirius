package org.sirius.frontend.ast;

public class IntersectionType implements Type{
	private Type first;
	private Type second;
	public IntersectionType(Type first, Type second) {
		super();
		this.first = first;
		this.second = second;
	}
	public Type getFirst() {
		return first;
	}
	public Type getSecond() {
		return second;
	}
	@Override
	public String messageStr() {
		return first.messageStr() + "&" + second.messageStr();
	}
	
	
}
