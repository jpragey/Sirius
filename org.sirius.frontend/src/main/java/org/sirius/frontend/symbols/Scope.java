package org.sirius.frontend.symbols;

import java.util.Optional;

import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;

public class Scope {
	private Optional<Scope> parentScope;

	private SymbolTableImpl symbolTable;
	
	private String dbgName;
	
	private Scope(Optional<Scope> parentScope, SymbolTableImpl symbolTable, String dbgName) {
		super();
		this.parentScope = parentScope;
		this.symbolTable = symbolTable;
		this.dbgName = dbgName;
	}
	
	public Scope(Optional<Scope> parentScope, String dbgName) {
		this(parentScope, new SymbolTableImpl(parentScope.map(ps->ps.getSymbolTable())  , dbgName), dbgName);
	}
	
	public Scope(Scope parentScope, String dbgName) {
		this(Optional.of(parentScope), dbgName);
	}
	
	public Scope(String dbgName) {
		this(Optional.empty(), dbgName);
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
	
	public void addInterface(AstInterfaceDeclaration interfaceDeclaration) {
		symbolTable.addInterface(interfaceDeclaration);
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
	public Optional<AstFunctionParameter> getFunctionArgument(String simpleName) {
		Optional<AstFunctionParameter> lvpl = symbolTable.lookupFunctionArgument(simpleName);
		return lvpl;
	}
	
	public Optional<Symbol> lookupSymbol(String simpleName) {
		Optional<Symbol> symb = symbolTable.lookupBySimpleName(simpleName);
		return symb;
	}
	public SymbolTableImpl getSymbolTable() {
		return symbolTable;
	}

	@Override
	public String toString() {
		return dbgName;
	}

	public Optional<Scope> getParentScope() {
		return parentScope;
	}

	
}
