package org.sirius.frontend.apiimpl;

import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

public class FunctionCallImpl implements FunctionCall {
	private Reporter reporter;
	private FunctionDefinition functionDefinition;
	private AstToken name;
	private List<Expression> actualArguments;
	private Optional<Expression> thisExpression;
	private SymbolTable symbolTable;



	public FunctionCallImpl(Reporter reporter, FunctionDefinition functionDefinition, AstToken name, 
			List<Expression> actualArguments, 
			Optional<Expression> thisExpression, SymbolTable symbolTable) {
		super();
		this.reporter = reporter;
		this.functionDefinition = functionDefinition;
		this.name = name;
		this.actualArguments = actualArguments;
		this.thisExpression = thisExpression;
		this.symbolTable = symbolTable;
	}

	@Override
	public org.sirius.common.core.Token getFunctionName() {
		return name.asToken();
	}
	@Override
	public Optional<Expression> getThis() {
		//			return thisExpression.map(expr -> expr.getExpression());
		return thisExpression;
	}
	@Override
	public List<Expression> getArguments() {
		return actualArguments;
	}

	@Override
	public Optional<AbstractFunction> getDeclaration() {
		Optional<Symbol> optSymbol = symbolTable.lookupBySimpleName(name.getText());
		if(optSymbol.isPresent()) {
			Optional<FunctionDefinition> optFunc =  optSymbol.get().getFunctionDeclaration();
			if(optFunc.isPresent()) {
				FunctionDefinition funcDecl = optFunc.get();

				int actualArgCount = actualArguments.size();
				List<Partial> partials = funcDecl.getPartials();
				if(actualArgCount > partials.size()) {
					reporter.error("Too many (" + actualArgCount + "argument(s) provided to function call." , name);
					return Optional.empty();
				}

				Partial partial = partials.get(actualArgCount);
				AbstractFunction result = partial.toAPI();
				return Optional.of(result);
			}
		}

		return Optional.empty();
	}
	@Override
	public Type getType() {
		return functionDefinition.getReturnType().getApiType();
	}
	@Override
	public String toString() {
		return "FunctionCallImpl (" + name.getText() + ")";
	}

}