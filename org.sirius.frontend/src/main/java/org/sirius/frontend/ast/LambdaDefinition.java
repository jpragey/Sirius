package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.FunctionImpl;
import org.sirius.frontend.symbols.SymbolTable;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class LambdaDefinition implements AstExpression, Verifiable, Visitable, Scoped {

	private AstType returnType; 
	private List<AstFunctionParameter> args;

	private FunctionBody body;
	
	private SymbolTableImpl symbolTable = null;
	
	private FunctionImpl functionImpl = null;

	public LambdaDefinition(List<AstFunctionParameter> args, AstType returnType, FunctionBody body) {
		this.args = args;
		this.returnType = returnType;
		this.body = body;
	}

	public AstType getReturnType() {
		return returnType;
	}

	public List<AstFunctionParameter> getArgs() {
		return args;
	}

	public FunctionBody getBody() {
		return body;
	}

	@Override
	public void verify(int featureFlags) {
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startLambdaDefinition(this);
		visitor.endLambdaDefinition(this);
	}

	public FunctionImpl toAPI(QName lambdaQName) {
		
		List<AstFunctionParameter> args = getArgs();
		if(functionImpl == null) {
			Type resolvedReturnType = returnType.getApiType();

			List<AstStatement> bodyStmts = body.getStatements();
			
			Optional<List<Statement>> apiBody = Optional.empty();
			
			List<Statement> apiStatements = new ArrayList<>(bodyStmts.size());
			for(AstStatement stmt:  bodyStmts/*statements*/) {
				Optional<Statement> optSt = stmt.toAPI();
				assert(optSt.isPresent());	// TODO
				Statement st = optSt.get();
				apiStatements.add(st);
			}
			boolean member = false;	// TODO: ???
			functionImpl = new FunctionImpl(lambdaQName, args, resolvedReturnType, apiStatements, member);
			assert(functionImpl.getArguments().size() == args.size());
		}

		assert(functionImpl.getArguments().size() == args.size());

		return functionImpl;
	}

	@Override
	public AstExpression linkToParentST(SymbolTable parentSymbolTable) {
		throw new UnsupportedOperationException();	// TODO
	}

	@Override
	public AstType getType() {
		throw new UnsupportedOperationException();	// TODO
	}

	@Override
	public Optional<Expression> getExpression() {
		throw new UnsupportedOperationException();	// TODO
	}

	@Override
	public String asString() {
		throw new UnsupportedOperationException();	// TODO
	}
	

	@Override
	public SymbolTable getSymbolTable() {
		assert(this.symbolTable != null);
		return this.symbolTable;
	}

	public void setSymbolTable(SymbolTableImpl symbolTable) {
		this.symbolTable = symbolTable;
	}

	
}
