package org.sirius.frontend.ast;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;

/** Argument for function / method or class constructor declaration
 * 
 * @author jpragey
 *
 */
public class AstFunctionFormalArgument {
	
	private AstType type;
	private AstToken name;
	private DefaultSymbolTable symbolTable;
	
	public AstFunctionFormalArgument(AstType type, AstToken name) {
		super();
		this.type = type;
		this.name = name;
	}
	
	public void visit(AstVisitor visitor) {
		visitor.startFunctionFormalArgument(this);
		type.visit(visitor);
		visitor.endFunctionFormalArgument(this);
	}
	
	public AstType getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}
	
	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public void resolve() {
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
	
	 @Override
	public String toString() {
	
		return "(" + type.toString() + " " + name + ")";
	}
}
