package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ConstructorCall;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.SymbolTableImpl;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

public class ConstructorCallExpression implements AstExpression, Scoped {

	private Reporter reporter;
	/** Function name */
	private AstToken name;
	
	private List<AstExpression> actualArguments = new ArrayList<>();

	private SymbolTableImpl symbolTable = null;

	
	
	private ConstructorCallExpression(Reporter reporter, AstToken name, List<AstExpression> actualArguments,
			SymbolTableImpl symbolTable) {
		super();
		this.reporter = reporter;
		this.name = name;
		this.actualArguments = actualArguments;
		this.symbolTable = symbolTable;
	}

	public ConstructorCallExpression(Reporter reporter, AstToken name, List<AstExpression> actualArguments) {
		this(reporter,name, actualArguments, new SymbolTableImpl(name.getText()));
	}

	public ConstructorCallExpression(Reporter reporter, AstToken name) {
		super();
		this.reporter = reporter;
		this.name = name;
	}

	public void setSymbolTable(SymbolTableImpl symbolTable) {
		this.symbolTable = symbolTable;
	}
	@Override
	public SymbolTableImpl getSymbolTable() {
		return symbolTable;
	}

	public void addArgument(AstExpression argExpression) {
		this.actualArguments.add(argExpression);
	}
	
	
	public List<AstExpression> getActualArguments() {
		return actualArguments;
	}

	@Override
	public AstType getType() {
		String fctName = name.getText();
		
		Optional<Symbol> symbol = symbolTable.lookupBySimpleName(fctName);
		if(symbol.isPresent()) {
			Optional<AstClassDeclaration> fct = symbol.get().getClassDeclaration();
			if(fct.isPresent()) {
				AstClassDeclaration decl = fct.get();
				return decl;
				
			} else {
				reporter.error("Symbol named '" + fctName + "' is not a  function.", name);
			}
		} else {
			reporter.error("Function named '" + fctName + "' not found.", name);
		}
		
		return AstType.noType;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startConstructorCallExpression(this);
		this.actualArguments.forEach(expr -> expr.visit(visitor));
		visitor.endConstructorCallExpression(this);
	}

	private class ConstructorCallImpl implements ConstructorCall {

		@Override
		public Type getType() {
			String simpleName = name.getText();
			Optional<Symbol> s = symbolTable.lookupBySimpleName(simpleName);
			if(s.isPresent()) {
				Optional<AstClassDeclaration> cd = s.get().getClassDeclaration();
				if(cd.isPresent()) {
					Type d = cd.get().getApiType();
					if(d instanceof IntegerType) {
						return (IntegerType)d;
					}
					assert(d instanceof ClassType);	// TODO
					return (ClassType)d;
				} else {
					reporter.error("Symbol is not a class or interface declaration: ", name);
				}
			} else {
				reporter.error("Symbol not found: ", name);
			}
			throw new UnsupportedOperationException("ConstructorCallImpl.getType() should return an Optional<>");
//			return null;
		}

		@Override
		public List<Expression> getArguments() {
			List<Expression> args = actualArguments.stream()
					.map(expr -> expr.getExpression().get())	// TODO: check for empty (invalid) getExpression()
					.collect(Collectors.toList());
			return args;
		}
		
		@Override
		public String toString() {
			return getType().toString() + "(...)";
		}
	}
	
	private Optional<Expression> impl = null;
	
	@Override
	public Optional<Expression> getExpression() {
		if(impl == null) {
			ConstructorCallImpl cci = new ConstructorCallImpl();
			impl = Optional.of(cci);
		}
		return impl;
	}
	@Override
	public String toString() {
		return "ctor: " + name.getText() + "(" + "..." + ")";
	}
	@Override
	public String asString() {
		return toString();
	}

	@Override
	public AstExpression linkToParentST(SymbolTable parentSymbolTable) {
		AstExpression expr = new ConstructorCallExpression(reporter, 
				name, 
				actualArguments.stream().map(exp -> exp.linkToParentST(parentSymbolTable)).collect(Collectors.toList()),
				new SymbolTableImpl(Optional.of(parentSymbolTable), ConstructorCallExpression.class.getSimpleName()));
		return expr;
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(actualArguments, featureFlags);
		verifyNotNull(symbolTable, "symbolTable");
		verifyNotNull(impl, "ConstructorCallExpression.impl");
	}

}
