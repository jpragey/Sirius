package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.Collections;
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
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Symbol;
import org.sirius.frontend.symbols.SymbolTable;

public class AstFunctionCallExpression implements AstExpression, Scoped {
	/** Function name */
	private AstToken name;
	
	private List<AstExpression> actualArguments = new ArrayList<>();

	private DefaultSymbolTable symbolTable = null;
	
	private Reporter reporter;
	
	// 
	private Optional<AstExpression> thisExpression = Optional.empty();
	

	
	private AstFunctionCallExpression(AstToken name, List<AstExpression> actualArguments, Reporter reporter,
			Optional<AstExpression> thisExpression, DefaultSymbolTable symbolTable) {
		super();
		this.name = name;
		this.actualArguments = actualArguments;
		this.reporter = reporter;
		this.thisExpression = thisExpression;
		this.symbolTable = symbolTable;
	}

	public AstFunctionCallExpression(Reporter reporter, AstToken name) {
		super();
		this.name = name;
		this.reporter = reporter;
	}
	
	public AstFunctionCallExpression(Reporter reporter, Token name) {
		this(reporter, new AstToken(name));
	}

	@Override
	public DefaultSymbolTable getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public AstToken getName() {
		return name;
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
		
		Optional<Symbol> symbol = symbolTable.lookup(fctName);
		if(symbol.isPresent()) {
			Optional<AstFunctionDeclaration> fct = symbol.get().getFunctionDeclaration();
			if(fct.isPresent()) {
				AstFunctionDeclaration decl = fct.get();
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

	private Optional<Expression> mapArg(AstExpression argExpression, AstFunctionFormalArgument formalArgument) {
		AstType argType = argExpression.getType();
		AstType expectedType = formalArgument.getType();
		if(argType.isExactlyA(expectedType)) {
			return Optional.of(argExpression.getExpression());
		}
		
		// -- 
		if(expectedType.isAncestorOrSameAs(argType)) {
			Expression expr = argExpression.getExpression();
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
				public Type getType() {
					return expr.getType();
				}};
			return Optional.of(castExpression);
		}
		
		reporter.error("Couldn't map actual type " + argType.messageStr() + " to type " + expectedType.messageStr());
		return Optional.empty();
//		System.out.println("---");
	}
	
	private class FunctionCallImpl implements FunctionCall {
		private AstFunctionDeclaration functionDeclaration;
		
		public FunctionCallImpl(AstFunctionDeclaration functionDeclaration) {
			super();
			this.functionDeclaration = functionDeclaration;
		}

		@Override
		public org.sirius.common.core.Token getFunctionName() {
			return name.asToken();
		}
		@Override
		public Optional<Expression> getThis() {
			return thisExpression.map(expr -> expr.getExpression());
		}
		@Override
		public List<Expression> getArguments() {
			List<AstFunctionFormalArgument> formalArgs = functionDeclaration.getFormalArguments();
			Iterator<AstFunctionFormalArgument> it = formalArgs.iterator();
			
			ArrayList<Expression> l = new ArrayList<>();
			for(AstExpression arg: actualArguments) {
				AstFunctionFormalArgument formalArgument = it.next();
				Optional<Expression> expr = mapArg(arg, formalArgument);
				if(expr.isPresent()) {
					l.add(expr.get());
				} else {	// TODO ???
				}
//				Expression ex = arg.getExpression();
//				l.add(ex);
//				System.out.println(ex);
			}
			return l;
		}

		@Override
		public Optional<AbstractFunction> getDeclaration() {
			Optional<Symbol> optSymbol = symbolTable.lookup(name.getText());
			if(optSymbol.isPresent()) {
				Optional<AstFunctionDeclaration> optFunc =  optSymbol.get().getFunctionDeclaration();
				if(optFunc.isPresent()) {
					AstFunctionDeclaration funcDecl = optFunc.get();
//					Optional<TopLevelFunction> tlFunc = funcDecl.getTopLevelFunction();
					AbstractFunction tlFunc = funcDecl.toAPI();
					return Optional.of(tlFunc);
				}
			}
			
			return Optional.empty();
		}
		@Override
		public Type getType() {
			return functionDeclaration.getReturnType().getApiType();
		}
		@Override
		public String toString() {
			return "FunctionCallImpl (" + name.getText() + ")";
		}
		
	}
	
	@Override
	public FunctionCall getExpression() {
		Optional<Symbol> symbol = symbolTable.lookup(name.getText());
		if(symbol.isPresent()) {
			Optional<AstFunctionDeclaration> fd = symbol.get().getFunctionDeclaration();
			if(fd.isPresent()) {
				AstFunctionDeclaration functionDeclaration = fd.get();
				
				int expectedArgCount = functionDeclaration.getFormalArguments().size();
				if(expectedArgCount != actualArguments.size()) {
					reporter.error(name.getText() + " has a wrong number of arguments: " + expectedArgCount + " actual, " + actualArguments.size() + " expected.", name);
				} else {
					return new FunctionCallImpl(functionDeclaration);
				}
				
			} else {
				reporter.error(name.getText() + " is not a function name.", name);
			}
		} else {
			reporter.error("Function " + name.getText() + " not found.", name);
		}
		
		// -- If function call expression couldn't be created, return a descent fake
		return new FunctionCall() {
			@Override
			public String toString() {
				return "Fake FunctionCall (couldn't be generated))";
			}
			@Override
			public org.sirius.common.core.Token getFunctionName() {
				return name;
			}
			
			@Override
			public List<Expression> getArguments() {
				return Collections.emptyList();
			}

			@Override
			public Optional<AbstractFunction> getDeclaration() {
				return Optional.empty();
			}

			@Override
			public Type getType() {
				return Type.voidType;
			}

			@Override
			public Optional<Expression> getThis() {
				return Optional.empty();
			}
		};
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
	public AstExpression linkToParentST(DefaultSymbolTable parentSymbolTable) {

		List<AstExpression> newArgs = actualArguments.stream().map(arg -> arg.linkToParentST(parentSymbolTable)).collect(Collectors.toList());  
//		Optional<AstExpression> thisExpression, 

		AstFunctionCallExpression newExpr = new AstFunctionCallExpression(
				name, 
				newArgs, 
				reporter,
				thisExpression,	// TODO: linkToParentST ???  
				new DefaultSymbolTable(parentSymbolTable));
		
		return newExpr;
	}

}

