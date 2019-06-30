package org.sirius.frontend.ast;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Type;

/** Argument for function / method or class constructor declaration
 * 
 * @author jpragey
 *
 */
public class AstFunctionFormalArgument {
	
	private AstType type;
	private AstToken name;
	
	public AstFunctionFormalArgument(AstType type, AstToken name) {
		super();
		this.type = type;
		this.name = name;
	}
	
	public AstType getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}

	public FunctionFormalArgument toAPI(QName functionQName) {
		return new FunctionFormalArgument() {
			QName argQName = functionQName.child(name.getText());
			
			@Override
			public QName getQName() {
				return argQName;
			}

			@Override
			public Type getType() {
				return type.getApiType();
			}
			
			
		};
	}
	
}
