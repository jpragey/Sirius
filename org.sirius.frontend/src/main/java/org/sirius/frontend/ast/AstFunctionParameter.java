package org.sirius.frontend.ast;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.FunctionFormalArgumentImpl;
import org.sirius.frontend.symbols.SymbolTableImpl;

/** Argument for function / method or class constructor declaration
 * 
 * @author jpragey
 *
 */
public class AstFunctionParameter implements Verifiable {
	
	private AstType type;
	private AstToken name;
	private SymbolTableImpl symbolTable;
	/** index in arg list (set *after* construction */
	private int index;

	public AstFunctionParameter(AstType type, AstToken name) {
		super();
		this.type = type;
		this.name = name;
		this.index = -1;
	}
	
	
	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public void visit(AstVisitor visitor) {
		visitor.startFunctionParameter(this);
		type.visit(visitor);
		visitor.endFunctionParameter(this);
	}
	
//	/** index in argument list */
//	public int getIndex() {
//		return index;
//	}
	
	public AstType getType() {
		return type;
	}
	public AstToken getName() {
		return name;
	}
	public String getNameString() {
		return name.getText();
	}
	
	public void setSymbolTable(SymbolTableImpl symbolTable) {
		this.symbolTable = symbolTable;
	}
	
//	public void resolve() {
//	}
	
	private FunctionFormalArgumentImpl impl = null;
	
	public FunctionParameter toAPI(QName functionQName) {
		if(impl == null) {
			Type paramType = type.getApiType();
			QName paramQName = functionQName.child(name.getText());
			impl = new FunctionFormalArgumentImpl(paramQName, paramType);
		}
		return impl;
	}
	
	 @Override
	public String toString() {
	
		return "(" + type.toString() + " " + name + ")";
	}


	@Override
	public void verify(int featureFlags) {
		// Nothing to do yet
		
	}
}
