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

import com.google.common.collect.ImmutableList;

public class Partial implements Visitable{
	private AstToken name;
	private ImmutableList<AstFunctionParameter> args;

	private boolean concrete;
	private boolean member = false;
	private QName qName;


	private AstType returnType = new AstVoidType();
	private List<AstStatement> statements/* = new ArrayList<>()*/; 
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

	public Partial(
			AstToken name,
			List<AstFunctionParameter> args, 
			boolean concrete,
			boolean member,
			QName qName,

			AstType returnType,
			List<AstStatement> statements) 
	{
		super();
		this.name = name;
		this.args = ImmutableList.copyOf(args);
	
		this.concrete = concrete;
		this.member = member;
		this.qName = qName;

		this.returnType = returnType;
		this.statements = statements;
	}

	public void assignSymbolTable(DefaultSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
		for(AstFunctionParameter arg: args) {
			this.symbolTable.addFunctionArgument(arg);
		}
	}


	private Type resolveReturnType() {
		Type resolved = returnType.getApiType();
		return resolved;
	}

	public AstToken getName() {
		return name;
	}

	public ImmutableList<AstFunctionParameter> getArgs() {
		return args;
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
		statements.stream().forEach(st -> st.visit(visitor));
		returnType.visit(visitor);
		visitor.endPartial(this);
	}

	private FunctionImpl functionImpl = null;

	public FunctionImpl toAPI() {
		if(functionImpl == null) {
			List<Statement> apiStatements = new ArrayList<>(statements.size());
			for(AstStatement stmt: statements) {
				Statement st = stmt.toAPI();
				apiStatements.add(st);
			}

			functionImpl = new FunctionImpl(qName, args, 
					resolveReturnType(),
					concrete ? Optional.of(apiStatements) : Optional.empty(),
							member);
			assert(functionImpl.getArguments().size() == args.size());
		}

		assert(functionImpl.getArguments().size() == args.size());

		return functionImpl;
	}
	public AstType getReturnType() {
		return returnType;
	}
}
