package org.sirius.frontend.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstFunctionParameter;

public class FunctionImpl implements AbstractFunction {
	private QName functionQName;
	private List<FunctionFormalArgument> implArguments;
	private Type returnType;

	private Optional<List<Statement>> bodyStatements;
	boolean member;

	public FunctionImpl(QName functionQName, List<AstFunctionParameter> formalArguments, Type returnType, 
			List<Statement> bodyStatements, boolean member) {
		this.functionQName = functionQName;
		this.returnType = returnType;
		this.bodyStatements = Optional.ofNullable(bodyStatements);
		this.member = member;

		ArrayList<FunctionFormalArgument> implArgs = new ArrayList<>(formalArguments.size()); 
		for(AstFunctionParameter arg: formalArguments) {
			FunctionFormalArgument formalArg = arg.toAPI(functionQName);
			implArgs.add(formalArg);
		}
		this.implArguments = List.copyOf(implArgs); 
	}
	/** Construct a  function declaration (No body) (TODO: refactor) */
	public FunctionImpl(QName functionQName, List<AstFunctionParameter> formalArguments, Type returnType, boolean member) {
		this(functionQName, formalArguments, returnType, 
				null /*List<Statement> bodyStatements*/ /* Oops*/ , member);
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