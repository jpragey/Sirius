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
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

public class ConstructorCallExpression implements AstExpression, Scoped {

	private Reporter reporter;
	private AstToken name;
	
	private List<AstExpression> actualArguments = new ArrayList<>();
	
	private Scope scope = null;
	
	public ConstructorCallExpression(Reporter reporter, AstToken name, List<AstExpression> actualArguments) {
		super();
		this.reporter = reporter;
		this.name = name;
		this.actualArguments = actualArguments;
		this.scope = null;
	}

	public void addArgument(AstExpression argExpression) {
		this.actualArguments.add(argExpression);
	}
	
	public Scope getScope() {
		assert(scope != null);
		return scope;
	}

	public void setScope(Scope scope) {
		assert(scope != null);
		this.scope = scope;
	}

	public List<AstExpression> getActualArguments() {
		return actualArguments;
	}

	@Override
	public AstType getType() {
		String fctName = name.getText();
		
		Optional<Symbol> symbol = scope.getSymbolTable().lookupBySimpleName(fctName);
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
		public Type type() {
			String simpleName = name.getText();
			Optional<Symbol> s = scope.lookupSymbol(simpleName);
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
			return "ConstructorCallImpl(...)";
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
		
		List<AstExpression> newArgs = actualArguments.stream().map(exp -> exp.linkToParentST(parentSymbolTable)).collect(Collectors.toList());
		
		AstExpression expr = new ConstructorCallExpression(reporter, name, newArgs);
		return expr;
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(actualArguments, featureFlags);
		verifyNotNull(scope, "scope");
		verifyNotNull(impl, "ConstructorCallExpression.impl");
	}

	@Override
	public void setScope2(Scope scope) {
		assert(this.scope == null);
		assert(scope != null);
		this.scope = scope;
	}

}
