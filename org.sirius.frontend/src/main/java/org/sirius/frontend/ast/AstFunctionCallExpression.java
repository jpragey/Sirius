package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.apiimpl.FunctionCallImpl;
import org.sirius.frontend.symbols.SymbolTableImpl;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

public class AstFunctionCallExpression implements AstExpression, Scoped {
	/** Function name */
	private AstToken name;
	
	private List<AstExpression> actualArguments = new ArrayList<>();

	private Scope scope = null;

	private Reporter reporter;
	
	private Optional<AstExpression> thisExpression = Optional.empty();
	

	private AstFunctionCallExpression(AstToken name, List<AstExpression> actualArguments, Reporter reporter, Optional<AstExpression> thisExpression) {
		super();
		this.name = name;
		this.actualArguments = actualArguments;
		this.reporter = reporter;
		this.thisExpression = thisExpression;
	}

	public AstFunctionCallExpression(Reporter reporter, AstToken name, List<AstExpression> actualArguments, 
			Optional<AstExpression> thisExpression) 
	{
		this(name, actualArguments, reporter, thisExpression/*, new SymbolTableImpl("")*/);
	}
	public AstFunctionCallExpression(Reporter reporter, AstToken name) {
		super();
		this.name = name;
		this.reporter = reporter;
	}
	
	public AstFunctionCallExpression(Reporter reporter, Token name) {
		this(reporter, new AstToken(name));
	}

	public AstToken getName() {
		return name;
	}
	public String getNameString() {
		return name.getText();
	}

	public List<AstExpression> getActualArguments() {
		return actualArguments;
	}
	
	public void addActualArgument(AstExpression argument) {
		this.actualArguments.add(argument);
	}

	
	public Optional<AstExpression> getThisExpression() {
		return thisExpression;
	}

	public void setThisExpression(AstExpression thisExpression) {
		this.thisExpression = Optional.of(thisExpression);
	}

	@Override
	public AstType getType() {
		String fctName = name.getText();
		
		Optional<Symbol> symbol = scope.getSymbolTable().lookupBySimpleName(fctName);
		if(symbol.isPresent()) {
			Optional<FunctionDefinition> fct = symbol.get().getFunctionDeclaration();
			if(fct.isPresent()) {
				Partial decl = fct.get().getAllArgsPartial();
				AstType returnType = decl.getReturnType();
				return returnType;
				
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
		visitor.startFunctionCallExpression(this);
		this.actualArguments.forEach(expr -> expr.visit(visitor));
		visitor.endFunctionCallExpression(this);
	}

	private Optional<Expression> mapArg(AstExpression argExpression, AstFunctionParameter formalArgument) {
		AstType argType = argExpression.getType();
		AstType expectedType = formalArgument.getType();
		if(argType.isExactlyA(expectedType)) {
			return argExpression.getExpression();
		}
		
		if(expectedType.isAncestorOrSameAs(argType)) {
			Optional<Expression> optExpr = argExpression.getExpression();
			assert(optExpr.isPresent());	// TODO: ???
			Expression expr = optExpr.get();
			TypeCastExpression castExpression = new TypeCastExpression() {

				@Override
				public Expression expression() {
					return expr;
				}

				@Override
				public Type targetType() {
					return argType.getApiType();
				}

				@Override
				public Type type() {
					return expr.type();
				}
				@Override
				public String toString() {
					return "TypeCastExpression " + expr.type().toString() + " -> " + targetType().toString();
				}};
			return Optional.of(castExpression);
		}
		
		reporter.error("Couldn't map actual type " + argType.messageStr() + " to type " + expectedType.messageStr());
		return Optional.empty();
	}

	private List<Expression> mapArgList(FunctionDefinition functionDeclaration) {
		List<AstFunctionParameter> formalArgs = functionDeclaration.getArgs();
		Iterator<AstFunctionParameter> it = formalArgs.iterator();
		
		ArrayList<Expression> l = new ArrayList<>();
		for(AstExpression arg: actualArguments) {
			AstFunctionParameter formalArgument = it.next();
			Optional<Expression> expr = mapArg(arg, formalArgument);
			if(expr.isPresent()) {
				l.add(expr.get());
			} else {	// TODO ???
			}
		}
		return l;
	}
	
	@Override
	public Optional<Expression> getExpression() {
		assert(scope != null);
		Optional<Symbol> symbol = scope.lookupSymbol(name.getText());
		if(symbol.isPresent()) {
			Optional<FunctionDefinition> fd = symbol.get().getFunctionDeclaration();
			if(fd.isPresent()) {
				FunctionDefinition functionDeclaration = fd.get();
				
				int expectedArgCount = functionDeclaration.getArgs().size();
				if(expectedArgCount != actualArguments.size()) {
					reporter.error(name.getText() + " has a wrong number of arguments: " + expectedArgCount + " actual, " + actualArguments.size() + " expected.", name);
				} else {
					List<Expression> argExpressions = mapArgList(functionDeclaration);
					Optional<Expression> optThisExpr = thisExpression.flatMap(expr -> expr.getExpression());

					FunctionCallImpl fctCallImpl = new FunctionCallImpl(reporter, functionDeclaration, name, 
							argExpressions, 
							optThisExpr, scope.getSymbolTable() /* symbolTable*/);
						
					return Optional.of(fctCallImpl);
				}
				
			} else {
				reporter.error(name.getText() + " is not a function name.", name);
			}
		} else {
			reporter.error("Function " + name.getText() + " not found.", name);
		}
		
		// -- If function call expression couldn't be created, return an empty optional
		return Optional.empty();
	}
	
	@Override
	public String toString() {
		return "AstFunctionCallExpression " + name.getText() + "()";
	}
	@Override
	public String asString() {
		return toString();
	}

	@Override
	public AstExpression linkToParentST(SymbolTable parentSymbolTable) {

		List<AstExpression> newArgs = actualArguments.stream().map(arg -> arg.linkToParentST(parentSymbolTable)).collect(Collectors.toList());  

		AstFunctionCallExpression newExpr = new AstFunctionCallExpression(
				name, 
				newArgs, 
				reporter,
				thisExpression	// TODO: linkToParentST ???  
						);
		
		return newExpr;
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(actualArguments, featureFlags);
		verifyNotNull(scope, "");
		
		if(thisExpression.isPresent())
			 verifyOptional(thisExpression, "thisExpression", featureFlags);
	}

	@Override
	public Scope getScope() {
		assert(this.scope != null);
		return this.scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	@Override
	public void setScope2(Scope scope) {
		assert(this.scope == null);
		assert(scope != null);
		this.scope = scope;
	}

}

