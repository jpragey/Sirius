package org.sirius.frontend.apiimpl;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.Token;
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

public record FunctionCallImpl(
		Reporter reporter, 
		FunctionDefinition functionDefinition, 
		AstToken nameToken, 
		List<Expression> arguments, 
		Optional<Expression> thisExpression, 
		SymbolTable symbolTable
		) implements FunctionCall 
{

//	@Override
//	public Optional<Expression> getThis() {
//		//			return thisExpression.map(expr -> expr.getExpression());
//		return thisExpression;
//	}

	@Override
	public List<Expression> arguments() {
		return arguments;
	}

	@Override
	public Optional<AbstractFunction> getDeclaration() {
		Optional<Symbol> optSymbol = symbolTable.lookupBySimpleName(nameToken.getText());
		if(optSymbol.isPresent()) {
			Optional<FunctionDefinition> optFunc =  optSymbol.get().getFunctionDeclaration();
			if(optFunc.isPresent()) {
				FunctionDefinition funcDecl = optFunc.get();

				int actualArgCount = arguments.size();
				List<Partial> partials = funcDecl.getPartials();
				if(actualArgCount > partials.size()) {
					reporter.error("Too many (" + actualArgCount + "argument(s) provided to function call." , nameToken);
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
	public Type type() {
		return functionDefinition.getReturnType().getApiType();
	}
	@Override
	public String toString() {
		return "FunctionCallImpl (" + nameToken.getText() + ")";
	}

}