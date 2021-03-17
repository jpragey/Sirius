package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.frontend.api.Statement;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class AstBlock implements AstStatement {

	private SymbolTableImpl symbolTable; 
	private List<AstStatement> statements;
	
	public AstBlock(SymbolTableImpl symbolTable, List<AstStatement> statements) {
		super();
		this.symbolTable = symbolTable;
		this.statements = statements;
	}
	
	public AstBlock() {
		this(new SymbolTableImpl("AstBlock"), new ArrayList<>());
	}

	public SymbolTableImpl getSymbolTable() {
		return symbolTable;
	}
	public List<AstStatement> getStatements() {
		return statements;
	} 

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startBlock(this);
		for(AstStatement st: statements) {
			st.visit(visitor);
		}
		visitor.endBlock(this);
	}

	@Override
	public Optional<Statement> toAPI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(statements, featureFlags);
		
	}
	
}
