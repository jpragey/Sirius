package org.sirius.frontend.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.apiimpl.FunctionImpl;
import org.sirius.frontend.ast.LambdaDefinition.APIFunctionInfo;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class Partial implements Visitable, Verifiable, Scoped {
	
	private AstToken name;
	private QName qName;

	private LambdaDefinition lambdaDefinition;

	private boolean member = false;

	private SymbolTableImpl symbolTable = null;
	private Scope scope = null;

	private FunctionImpl functionImpl = null;
	
	public Partial(AstToken name,
			List<AstFunctionParameter> args, 
			boolean member,
			AstType returnType,
			List<AstStatement> body) 
	{
		super();
		this.name = name;

		this.lambdaDefinition = new LambdaDefinition(args, returnType, new FunctionBody(body));

		this.member = member;
		this.qName = null;
	}

	public void assignSymbolTable(SymbolTableImpl symbolTable) {
		this.symbolTable = symbolTable;
		for(AstFunctionParameter arg: lambdaDefinition.getArgs()) {
			this.symbolTable.addFunctionArgument(arg);
		}
	}


//	private Type resolveReturnType() {
//		Type resolved = lambdaDefinition.getReturnType().getApiType();
//		return resolved;
//	}

	public void setContainerQName(QName containerQName) {
		this.qName = containerQName.child(new QName(name.getText()));
	}

	public AstToken getName() {
		return name;
	}

	public List<AstFunctionParameter> getArgs() {
		return lambdaDefinition.getArgs();
	}
	public AstFunctionParameter getArg(int argIndex) {
		List<AstFunctionParameter> args = getArgs();
		
		if(argIndex<0 || argIndex > args.size())
			throw new IllegalArgumentException("Trying to get arg " + argIndex + " of function of " + args.size() + " args; function " + toString());
		return args.get(argIndex);
	}

	public SymbolTableImpl getSymbolTable() {
		assert(symbolTable != null);
		return symbolTable;
	}
	@Override
	public String toString() {
		List<AstFunctionParameter> args = getArgs();
		String text =
				name.getText() + 
				"_" + args.size() + "_" +
				args.stream().map(capt -> capt.toString()).collect(Collectors.joining(", ", "(", ")"))
				;
		return text;
	}

	@Override
	public void visit(AstVisitor visitor) {
		visitor.startPartial(this);
		getArgs().stream().forEach(formalArg -> formalArg.visit(visitor));
		lambdaDefinition.getBody().getStatements().stream().forEach(st -> st.visit(visitor));
		lambdaDefinition.getReturnType().visit(visitor);
		visitor.endPartial(this);
	}


	public FunctionImpl toAPI() {
		
		List<AstFunctionParameter> args = getArgs();
		if(functionImpl == null) {
			APIFunctionInfo functionInfo = lambdaDefinition.toAPI(qName);
			this.functionImpl = functionInfo.getFunctionType();	// TODO
			assert(functionImpl.getArguments().size() == args.size());
		}

		assert(functionImpl.getArguments().size() == args.size());

		return functionImpl;
	}
	public AstType getReturnType() {
		return lambdaDefinition.getReturnType();
	}

	public List<AstStatement> getBodyStatements() {
		return lambdaDefinition.getBody().getStatements();
	}
	
	
	public void assignScope(Scope scope) {
		this.scope = scope;

		for(AstFunctionParameter arg: lambdaDefinition.getArgs())
			this.scope.addFunctionArgument(arg);

		// -- add closure to scope
//		for(AstFunctionParameter d : this.closure) {
//			// -- Convert function parameter to local variable
//			// TODO: ???
//			AstLocalVariableStatement stmt = new AstLocalVariableStatement(new AnnotationList(), d.getType(), d.getName(), Optional.empty() /*d.initialValue*/);
//			
//			scope.addLocalVariable(stmt);
//		}
		
		
		for(AstStatement stmt: lambdaDefinition.getBody().getStatements()) {
			if(stmt instanceof AstLocalVariableStatement) {
				scope.addLocalVariable((AstLocalVariableStatement)stmt);
			}
		}
	}

	public QName getqName() {
		return qName;
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public void verify(int featureFlags) {
		verifyList(getArgs(), featureFlags);

		verifyList(lambdaDefinition.getBody().getStatements(), featureFlags); 
		
		verifyCachedObjectNotNull(symbolTable, "Partial.symbolTable", featureFlags);
		verifyNotNull(scope, "partial.scope");
		verifyCachedObjectNotNull(functionImpl, "Partial.functionImpl", featureFlags);
	}
	
}
