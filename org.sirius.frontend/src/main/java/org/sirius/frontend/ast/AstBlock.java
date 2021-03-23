package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.frontend.api.BlockStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.apiimpl.BlockStatementImpl;
import org.sirius.frontend.symbols.SymbolTable;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class AstBlock implements AstStatement, Scoped {

	private SymbolTable symbolTable; 
	private List<AstStatement> statements;
	
	public AstBlock(SymbolTable symbolTable, List<AstStatement> statements) {
		super();
		this.symbolTable = symbolTable;
		this.statements = statements;
	}
	
	public AstBlock() {
		this(new SymbolTableImpl("AstBlock"), new ArrayList<>());
	}

	@Override
	public SymbolTable getSymbolTable() {
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

	private Optional<Statement> impl;
	@Override
	public Optional<Statement> toAPI() {
		if(impl == null) {
			impl = Optional.of(new BlockStatementImpl());
		}
		return impl;
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(statements, featureFlags);
		
	}
	
}
