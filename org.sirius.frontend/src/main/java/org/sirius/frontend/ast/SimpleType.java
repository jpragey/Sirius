package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleType implements Type {
	AstToken name;

	List<Type> appliedParameters = new ArrayList<>();
	
	public SimpleType(AstToken name) {
		super();
		this.name = name;
	}

	public AstToken getName() {
		return name;
	}
	
	public void appliedParameter(Type type) {
		appliedParameters.add(type);
	}
	
	@Override
	public String messageStr() {
		
		List<String> typeParams = appliedParameters.stream().map(p -> p.messageStr()).collect(Collectors.toList());
		
		return "class " + 
				name.getText() + 
				"<" + 
				String.join(",", typeParams) + 
				">";
	}

}
