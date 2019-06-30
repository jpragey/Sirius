package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.Type;

/** most simple (class or interface) type 
 * 
 * @author jpragey
 *
 */
public class SimpleType implements AstType {
	AstToken name;

	List<AstType> appliedParameters = new ArrayList<>();
	
	public SimpleType(AstToken name) {
		super();
		this.name = name;
	}

	public AstToken getName() {
		return name;
	}
	
	public void appliedParameter(AstType type) {
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

	@Override
	public ClassType getApiType() {
		return new ClassType() {
			QName qName = new QName(name.getText());	// TODO : must be a full class name
			@Override
			public QName getQName() {
				return qName;
			}
			
		};
	}

}
