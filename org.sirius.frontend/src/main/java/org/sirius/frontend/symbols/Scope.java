package org.sirius.frontend.symbols;

import java.util.Optional;

import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;

public class Scope {
	private Optional<Scope> parentScope;

	private DefaultSymbolTable symbolTable = new DefaultSymbolTable("");
	
	public Scope(Optional<Scope> parentScope) {
		super();
		this.parentScope = parentScope;
	}
	
	public void addFunction(FunctionDefinition funct) {
		AstToken name = funct.getName();
		symbolTable.addFunction(funct);
	}
	
	public void addMemberValue(AstMemberValueDeclaration declaration) {
		symbolTable.addValue(declaration);
	}
	
	public void addLocalVariable(AstLocalVariableStatement declaration) {
		symbolTable.addLocalVariable(declaration);
	}
	public void addFunctionArgument(AstFunctionParameter functionArgument) {
		symbolTable.addFunctionArgument(functionArgument);
	}
	
	
	
	public Optional<FunctionDefinition> getFunction(String simpleName) {
		Optional<FunctionDefinition> pl = symbolTable.lookupPartialList(simpleName);
		return pl;
	}
	public Optional<AstMemberValueDeclaration> getValue(String simpleName) {
		Optional<AstMemberValueDeclaration> pl = symbolTable.lookupValue(simpleName);
		return pl;
	}
	
	public Optional<AstLocalVariableStatement> getLocalVariable(String simpleName) {
		Optional<AstLocalVariableStatement> lvpl = symbolTable.lookupLocalVariable(simpleName);
		return lvpl;
	}
	public Optional<AstFunctionParameter> getFunctionParameter(String simpleName) {
		Optional<AstFunctionParameter> lvpl = symbolTable.lookupFunctionArgument(simpleName);
		return lvpl;
	}
	
	
	
	
	public Optional<Symbol> lookupSymbol(String simpleName) {
		Optional<Symbol> symb = symbolTable.lookupBySimpleName(simpleName);
		return symb;
	}

	
}
