package org.sirius.frontend.ast;

public class UnionType implements Type{
	private Type first;
	private Type second;
	public UnionType(Type first, Type second) {
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
		return first.messageStr() + " | " + second.messageStr();
	}

}
