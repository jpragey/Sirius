package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Scope;

import com.google.common.collect.ImmutableList;

public class Partial implements Visitable{
	private AstToken name;
	private ImmutableList<AstFunctionParameter> args;

	private boolean member = false;
	private QName qName;

	private AstType returnType = new AstVoidType();
	private Optional<List<AstStatement>> body/* = new ArrayList<>()*/; 
	private DefaultSymbolTable symbolTable = null;

	static public class FunctionImpl implements AbstractFunction {
		QName functionQName;
		private ImmutableList<FunctionFormalArgument> implArguments;
		Type returnType;

		Optional<List<Statement>> bodyStatements;
		boolean member;

		public FunctionImpl(QName functionQName, ImmutableList<AstFunctionParameter> formalArguments, Type returnType, 
				Optional<List<Statement>> bodyStatements, boolean member) {
			this.functionQName = functionQName;
			this.returnType = returnType;
			this.bodyStatements = bodyStatements;
			this.member = member;

			ArrayList<FunctionFormalArgument> implArgs = new ArrayList<>(formalArguments.size()); 
			for(AstFunctionParameter arg: formalArguments) {
				FunctionFormalArgument formalArg = arg.toAPI(functionQName);
				implArgs.add(formalArg);
			}
			this.implArguments = ImmutableList.copyOf(implArgs); 
		}

		@Override
		public String toString() {

			return "API function " + functionQName.dotSeparated() + "(" + implArguments.size() + " args)";
		}
		@Override
		public QName getQName() {
			return functionQName;
		}

		@Override
		public List<FunctionFormalArgument> getArguments() {
			return implArguments;
		}

		@Override
		public Type getReturnType() {
			return returnType;
		}

		@Override
		public Optional<List<Statement>> getBodyStatements() {
			return bodyStatements;
		}

		@Override
		public Optional<QName> getClassOrInterfaceContainerQName() {
			if(member /*&& containerQName.isPresent()*/) {
				return functionQName.parent();
			}

			return Optional.empty();
		}
	}
	
	private List<AstFunctionParameter> closure0;
	
	public Partial(AstToken name,
//			List<AstFunctionParameter> closure, 
			List<AstFunctionParameter> args, 
			boolean member,
//			QName qName,
			AstType returnType,
			Optional<List<AstStatement>> body) 
	{
		super();
		this.name = name;
//		this.closure = ImmutableList.copyOf(closure);
		this.args = ImmutableList.copyOf(args);
	
		this.member = member;
//		this.qName = qName;
		this.qName = null;

		this.returnType = returnType;
		this.body = body;
	}

	public void assignSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
		for(AstFunctionParameter arg: args) {
			this.symbolTable.addFunctionArgument(arg);
//			this.scope.addFunctionArgument(arg);
		}
	}


	private Type resolveReturnType() {
		Type resolved = returnType.getApiType();
		return resolved;
	}

	public void setContainerQName(QName containerQName) {
		this.qName = containerQName.child(new QName(name.getText()));
	}

	public AstToken getName() {
		return name;
	}

	public ImmutableList<AstFunctionParameter> getArgs() {
		return args;
	}
	public AstFunctionParameter getArg(int argIndex) {
		if(argIndex<0 || argIndex > args.size())
			throw new IllegalArgumentException("Trying to get arg " + argIndex + " of function of " + args.size() + " args; function " + toString());
		return args.get(argIndex);
	}

	public DefaultSymbolTable getSymbolTable() {
		assert(symbolTable != null);
		return symbolTable;
	}
	@Override
	public String toString() {
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
		args.stream().forEach(formalArg -> formalArg.visit(visitor));
		if(body.isPresent())
			body.get().stream().forEach(st -> st.visit(visitor));
		returnType.visit(visitor);
		visitor.endPartial(this);
	}

	private FunctionImpl functionImpl = null;

	public FunctionImpl toAPI() {
		
//		assert(body.isPresent());	// TODO: do something for function pure declaration
		
		if(functionImpl == null) {
			
			Optional<List<Statement>> apiBody = Optional.empty();
			if(body.isPresent()) {	// TODO: ???
				List<AstStatement> statements = body.get();
				List<Statement> apiStatements = new ArrayList<>(statements.size());
				for(AstStatement stmt: statements) {
					Statement st = stmt.toAPI();
					apiStatements.add(st);
				}
				apiBody = Optional.of(apiStatements);
			}

			functionImpl = new FunctionImpl(qName, args, resolveReturnType(), apiBody, member);
			assert(functionImpl.getArguments().size() == args.size());
		}

		assert(functionImpl.getArguments().size() == args.size());

		return functionImpl;
	}
	public AstType getReturnType() {
		return returnType;
	}

	public Optional<List<AstStatement>> getBodyStatements() {
		return body;
	}
	
	private Scope scope = null;
	
	public void assignScope(Scope scope) {
		this.scope = scope;

		for(AstFunctionParameter arg: args)
			this.scope.addFunctionArgument(arg);

		// -- add closure to scope
//		for(AstFunctionParameter d : this.closure) {
//			// -- Convert function parameter to local variable
//			// TODO: ???
//			AstLocalVariableStatement stmt = new AstLocalVariableStatement(new AnnotationList(), d.getType(), d.getName(), Optional.empty() /*d.initialValue*/);
//			
//			scope.addLocalVariable(stmt);
//		}
		
		
		if(body.isPresent()) {
			for(AstStatement stmt: body.get()) {
				if(stmt instanceof AstLocalVariableStatement) {
					scope.addLocalVariable((AstLocalVariableStatement)stmt);
				}
			}
		}
		
	}

	public QName getqName() {
		return qName;
	}

	public Scope getScope() {
		return scope;
	}
	
}
