package org.sirius.frontend.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.symbols.LocalSymbolTable;
import org.sirius.frontend.symbols.SymbolTable;

public class FunctionDeclaration implements Scoped, Visitable {

	private AstToken name;
	
	private List<TypeFormalParameterDeclaration> typeParameters = new ArrayList<>();
	
	private List<FunctionFormalArgument> formalArguments = new ArrayList<>();
	

	private List<Statement> statements = new ArrayList<>(); 
	
	private Type returnType = new VoidType();

	private Reporter reporter;

	private LocalSymbolTable symbolTable; 

	public FunctionDeclaration(Reporter reporter, AstToken name, Type returnType) {
		super();
		this.reporter = reporter;
		this.name = name;
		this.returnType = returnType;
		this.symbolTable = new LocalSymbolTable(reporter); 
		
		this.symbolTable.addFunction(name, this);
	}

	public AstToken getName() {
		return name;
	}
	
	public void addStatement(Statement statement) {
		this.statements.add(statement);
	}

	public List<Statement> getStatements() {
		return statements;
	}
	
	public void visit(AstVisitor visitor) {
		visitor.startFunctionDeclaration(this);
		statements.stream().forEach(st -> st.visit(visitor));
		visitor.endFunctionDeclaration(this);
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public void setSymbolTableParent(SymbolTable newParent) {
		this.symbolTable.setParentSymbolTable(newParent);
	}

	@Override
	public LocalSymbolTable getSymbolTable() {
		return symbolTable;
	}

	public List<TypeFormalParameterDeclaration> getTypeParameters() {
		return typeParameters;
	}

	public void addTypeParameterDeclaration(TypeFormalParameterDeclaration typeParameter) {
		this.typeParameters.add(typeParameter);
	}

	public void addFormalArgument(FunctionFormalArgument argument) {
		this.formalArguments.add(argument);
		this.symbolTable.addFunctionArgument(argument.getName(), argument);
	}

	public List<FunctionFormalArgument> getFormalArguments() {
		return formalArguments;
	}

	public String messageStr() {
		List<String> typeParams = typeParameters.stream().map(p -> p.messageStr()).collect(Collectors.toList());

		return returnType.messageStr() + 
				" " + 
				name.getText() +
				(typeParams.isEmpty() ? "" : ("<" + String.join(", ", typeParams) + ">")) +
				"(" + ")"; // TODO: parameters
	}
	
	public Optional<FunctionDeclaration> apply(Type parameter) {
		TypeFormalParameterDeclaration formalParam = typeParameters.get(0);
		if(formalParam == null) {
			reporter.error("Can't apply type " + parameter.messageStr() + " to function " + messageStr() + ", it has no formal parameter." );
			return Optional.empty();
		}
		
		FunctionDeclaration cd = new FunctionDeclaration(reporter, name, returnType);
		
		cd.typeParameters.addAll(typeParameters.subList(1, typeParameters.size()));
	
//		for(FunctionDeclaration fd: functionDeclarations) {
//			cd.addFunctionDeclaration(fd.apply(parameter));
//		}
		
		return Optional.of(cd);
	}

}
